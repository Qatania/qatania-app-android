package com.q1.qatania.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.q1.qatania.model.gameboard.TileType
import com.q1.qatania.model.player.PlayerModel
import com.q1.qatania.theme.catanLightContrast
import com.q1.qatania.util.getResourceIcon

@Composable
fun ResourceBar(
    modifier: Modifier,
    player: PlayerModel?,
    onCheatAttempt: (TileType) -> Unit
) {
    val displayOrder = listOf(
        TileType.WOOD,
        TileType.CLAY,
        TileType.SHEEP,
        TileType.WHEAT,
        TileType.ORE
    )

    val resources = player?.resources

    FlowRow( // breakline on overflow
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        displayOrder.forEach { tileType ->
            val count = resources?.get(tileType) ?: 0
            ResourceItem(
                tileType = tileType,
                count = count,
                onCheatAttempt = onCheatAttempt,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
private fun ResourceItem(
    tileType: TileType,
    count: Int,
    onCheatAttempt: (TileType) -> Unit,
    modifier: Modifier = Modifier
) {
    val iconRes = getResourceIcon(tileType)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        modifier = modifier
            .background(catanLightContrast.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
            .padding(10.dp)

    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = tileType.name,
            modifier = Modifier
                .size(20.dp)
                .pointerInput(tileType) {
                    detectTapGestures(
                        onDoubleTap = {
                            onCheatAttempt(tileType)
                        }
                    )
                },

            colorFilter = ColorFilter.tint(Color.Black)

        )
        Text(
            text = count.toString(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}