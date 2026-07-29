package com.q1.qatania.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.q1.qatania.model.gameboard.TileType
import com.q1.qatania.model.player.PlayerModel
import com.q1.qatania.theme.buttons

@Composable
fun BankTrade(
    modifier: Modifier = Modifier,
    player: PlayerModel?,
    onSubmit: (Pair<Map<TileType, Int>, Map<TileType, Int>>) -> Unit,
    onCancel: () -> Unit
) {
    val resourceOrder =
        listOf(TileType.WOOD, TileType.CLAY, TileType.SHEEP, TileType.WHEAT, TileType.ORE)
    var tradeOffer by remember {
        mutableStateOf(
            Pair<Map<TileType, Int>, Map<TileType, Int>>(
                emptyMap(),
                emptyMap()
            )
        )
    }
    val validTrade = tradeOffer.first.isNotEmpty() && tradeOffer.second.isNotEmpty()
    val onUpdateOffer: (TileType, Int) -> Unit = { resource, increase ->
        val currentMap = tradeOffer.first.toMutableMap()
        val current = tradeOffer.first.getOrDefault(resource, 0)
        val availableResources = player?.resources?.getOrDefault(resource, 0)
        val next = (current + increase).coerceIn(0, availableResources)
        currentMap[resource] = next
        tradeOffer = Pair(currentMap, tradeOffer.second)
    }
    val onUpdateTarget: (TileType, Int) -> Unit = { resource, increase ->
        val currentMap = tradeOffer.second.toMutableMap()
        val current = tradeOffer.second.getOrDefault(resource, 0)
        var next = current + increase
        if (next < 0) next = 0
        currentMap[resource] = next
        tradeOffer = Pair(tradeOffer.first, currentMap)
    }

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onCancel() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .widthIn(max = 600.dp)
                    .heightIn(max = 800.dp)
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Trade with Bank", style = MaterialTheme.typography.headlineSmall)
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "You Give",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                resourceOrder.forEach { resource ->
                                    ResourceSelector(
                                        resource = resource,
                                        count = tradeOffer.first.getOrDefault(resource, 0),
                                        current = null,
                                        onIncrement = { onUpdateOffer(resource, 1) },
                                        onDecrement = { onUpdateOffer(resource, -1) }
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Trade to",
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(horizontal = 8.dp, vertical = 48.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "You Get (You have)",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                resourceOrder.forEach { resource ->
                                    ResourceSelector(
                                        resource = resource,
                                        count = tradeOffer.second.getOrDefault(resource, 0),
                                        current = player?.resources?.getOrDefault(resource, 0),
                                        onIncrement = { onUpdateTarget(resource, 1) },
                                        onDecrement = { onUpdateTarget(resource, -1) }
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onCancel(); tradeOffer = Pair(emptyMap(), emptyMap()) },
                            colors = ButtonDefaults.buttonColors(containerColor = buttons),
                            border = BorderStroke(1.dp, Color.Black)
                        ) {
                            Text("Back", color = Color.White)
                        }
                        Button(
                            onClick = {
                                onSubmit(tradeOffer); tradeOffer = Pair(emptyMap(), emptyMap())
                            },
                            enabled = validTrade,
                            colors = ButtonDefaults.buttonColors(containerColor = buttons),
                            border = BorderStroke(1.dp, Color.Black)
                        ) {
                            Text("Confirm Trade", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

