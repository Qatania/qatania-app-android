package com.q1.qatania.model

data class Port (
    val inputResourceAmount: Int,
    val portType: PortType,
    val resource: TileType,
    val portVisuals: PortVisuals
)