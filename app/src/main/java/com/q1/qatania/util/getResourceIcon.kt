package com.q1.qatania.util

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

fun getResourceImage(tileType: TileType): Int {
    return when (tileType) {
        TileType.WOOD -> R.drawable.ressource_card_wood
        TileType.CLAY -> R.drawable.ressource_card_clay
        TileType.SHEEP -> R.drawable.ressource_card_wool
        TileType.WHEAT -> R.drawable.ressource_card_wheat
        TileType.ORE -> R.drawable.ressource_card_ore
        TileType.WASTE -> R.drawable.ressource_card_clay
    }
}