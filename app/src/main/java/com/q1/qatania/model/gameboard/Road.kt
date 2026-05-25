package com.q1.qatania.model.gameboard

import kotlinx.serialization.Serializable

@Serializable
data class Road (
    val id: Int,
    val owner: String?,
    val color: String?,
    val coordinates: List<Double>,
    val rotationAngle: Double
)