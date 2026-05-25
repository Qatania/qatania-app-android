package com.q1.qatania.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.q1.qatania.R
import com.q1.qatania.model.gameboard.TileType
import com.q1.qatania.util.getResourceIcon
import com.q1.qatania.viewmodel.GameBoardViewModel
import com.q1.qatania.viewmodel.GameBoardViewModelFactory
import com.q1.qatania.viewmodel.PlayerInfoViewModel

@Composable
fun ResourceBar(modifier: Modifier, playerInfoViewModel: PlayerInfoViewModel) {
    val displayOrder = listOf(
        TileType.WOOD,
        TileType.CLAY,
        TileType.SHEEP,
        TileType.WHEAT,
        TileType.ORE
    )

    val player = playerInfoViewModel.boardFlow.collectAsState(
        initial = null
    )
    val resources = player.value?.resources

    val onCheatAttempt: (resourceType: TileType) -> Unit = { resourceType ->
        playerInfoViewModel.cheat(resourceType)
    }


    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.LightGray, shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        displayOrder.forEach { tileType ->
            val count = resources?.get(tileType) ?: 0
            ResourceItem(tileType = tileType, count = count, onCheatAttempt = onCheatAttempt)
        }
    }
}


@Composable
private fun ResourceItem(
    tileType: TileType,
    count: Int,
    onCheatAttempt: (TileType) -> Unit
) {
    val iconRes = getResourceIcon(tileType)

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = tileType.name,
            modifier = Modifier
                .size(30.dp)
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
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}