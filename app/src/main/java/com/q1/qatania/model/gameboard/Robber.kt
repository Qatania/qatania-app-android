package com.q1.qatania.model.gameboard

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Robber(
    val tile_ID: Int,
    val coordinates: List<Double>
)