package com.q1.qatania.model

data class GameBoardModel(
    val tiles: List<Tile>,
    val settlementPositions: List<SettlementPosition>,
    val roads: List<Road>,
    val ports: List<Port>,
    val ringsOfBoard: Int,
    val sizeOfHex: Int
)
