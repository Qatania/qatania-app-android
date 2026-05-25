package com.q1.qatania.util

import androidx.compose.runtime.Composable
import com.q1.qatania.R
import com.q1.qatania.model.gameboard.TileType


fun getResourceIcon(tileType: TileType): Int {
    return when (tileType) {
        TileType.WOOD -> R.drawable.wood_icon
        TileType.CLAY -> R.drawable.clay_icon
        TileType.SHEEP -> R.drawable.sheep_icon
        TileType.WHEAT -> R.drawable.wheat_icon
        TileType.ORE -> R.drawable.ore_icon
        TileType.WASTE -> R.drawable.clay_icon
    }
}