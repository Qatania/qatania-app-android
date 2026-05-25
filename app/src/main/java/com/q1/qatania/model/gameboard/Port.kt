package com.q1.qatania.model.gameboard

import kotlinx.serialization.Serializable

@Serializable
data class Port (
    val inputResourceAmount: Int,
    val portType: PortType,
    val resource: TileType? = null,
    val portVisuals: PortVisuals
)