package com.q1.qatania.model

data class Road (
    val id: Int,
    val owner: String?,
    val color: String?,
    val coordinates: List<Double>,
    val rotationAngle: Double
)