package com.q1.qatania.model.gameboard

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Tile(
    val id: Int,
    val type: TileType,
    val value: Int,
    val coordinates: List<Double>
)