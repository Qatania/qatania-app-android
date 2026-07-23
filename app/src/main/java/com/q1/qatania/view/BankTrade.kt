package com.q1.qatania.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.q1.qatania.model.gameboard.TileType
import com.q1.qatania.model.player.PlayerModel

@Composable
fun BankTrade(
    player: PlayerModel?,
    onSubmit: (Pair<Map<TileType, Int>, Map<TileType, Int>>) -> Unit,
    onCancel: () -> Unit
) {
    val resourceOrder = listOf(TileType.WOOD, TileType.CLAY, TileType.SHEEP, TileType.WHEAT, TileType.ORE)
    var tradeOffer by remember { mutableStateOf(Pair<Map<TileType, Int>, Map<TileType, Int>>(emptyMap(), emptyMap())) }
    val validTrade = tradeOffer.first.isNotEmpty() && tradeOffer.second.isNotEmpty()
    val onUpdateOffer: (TileType, Int) -> Unit = {resource, increase ->
        val currentMap = tradeOffer.first.toMutableMap()
        val current = tradeOffer.first.getOrDefault(resource, 0)
        val availableResources = player?.resources?.getOrDefault(resource, 0)
        val next = (current + increase).coerceIn(0, availableResources)
        currentMap[resource] = next
        tradeOffer = Pair(currentMap, tradeOffer.second)
    }
    val onUpdateTarget: (TileType, Int) -> Unit = {resource, increase ->
        val currentMap = tradeOffer.second.toMutableMap()
        val current = tradeOffer.second.getOrDefault(resource, 0)
        var next = current + increase
        if (next < 0) next = 0
        currentMap[resource] = next
        tradeOffer = Pair(tradeOffer.first, currentMap)
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .background(Color.Gray),
    ) {
        Text("Trade with Bank", style = MaterialTheme.typography.headlineSmall, color = Color.White)
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .weight(1f)
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
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
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
                    tint = Color.White,
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
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
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
        Spacer(Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { onCancel(); tradeOffer = Pair(emptyMap(), emptyMap()) }) {
                Text("Back", color = Color.White)
            }
            Button(onClick = { onSubmit(tradeOffer); tradeOffer = Pair(emptyMap(), emptyMap())}, enabled = validTrade) {
                Text("Confirm Trade", color = Color.White)
            }
        }
    }
}

