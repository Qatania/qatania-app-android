package com.q1.qatania.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.q1.qatania.model.gameboard.TileType
import com.q1.qatania.model.player.PlayerModel
import com.q1.qatania.util.getResourceImage

@Composable
fun PlayerBar(
    players: Map<String, PlayerModel>,
    self: String,
    onCheatAttempt: (TileType) -> Unit,
    onReport: (String) -> Unit
) {

    val listState = rememberLazyListState()
    var expandedPlayerId by remember { mutableStateOf<String?>(null) }

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(items = players.values.toList()) { player ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PlayerCard(
                    player = player,
                    onClick = {
                        expandedPlayerId = if (expandedPlayerId == player.id) null else player.id
                    }
                )

                AnimatedVisibility(
                    visible = expandedPlayerId == player.id,
                    enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                    exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
                ) {
                    PlayerResourcePopup(
                        player = player,
                        self = self,
                        modifier = Modifier.width(260.dp),
                        onCheatAttempt = onCheatAttempt,
                        onReport = onReport
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerCard(
    player: PlayerModel,
    onClick: () -> Unit
) {
    val borderColor = Color(player.color.toColorInt())
    val vpTextColor = Color.Black

    Box(
        modifier = Modifier
            .height(40.dp)
            .width(260.dp)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Transparent)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    if (player.isActivePlayer) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    player.username?.let {
                        Text(
                            text = it,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(borderColor)
            )
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .fillMaxHeight()
                    .background(borderColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${player.victoryPoints} VP",
                    color = vpTextColor
                )
            }
        }
    }
}

@Composable
fun PlayerResourcePopup(
    player: PlayerModel,
    self: String,
    modifier: Modifier = Modifier,
    onCheatAttempt: (TileType) -> Unit,
    onReport: (String) -> Unit
) {
    val resources = player.resources

    val displayOrder = listOf(
        TileType.WOOD,
        TileType.CLAY,
        TileType.SHEEP,
        TileType.WHEAT,
        TileType.ORE
    )

    Card(
        colors = CardDefaults.cardColors(),
        modifier = modifier.padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            displayOrder.forEach { type ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(
                            id = getResourceImage(type)
                        ),
                        contentDescription = type.name,
                        modifier = Modifier
                            .size(36.dp)
                            .pointerInput(type) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        onCheatAttempt(type)
                                    }
                                )
                            }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = resources[type]?.toString() ?: "0",
                        color = Color.Black
                    )
                }
            }
            if (player.id != self) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = {
                            onReport(player.id)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Report,
                            contentDescription = "Report",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}