package com.q1.qatania.model.gameboard

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Road (
    val id: Int,
    val owner: String?,
    val color: String?,
    val coordinates: List<Double>,
    val rotationAngle: Double
)