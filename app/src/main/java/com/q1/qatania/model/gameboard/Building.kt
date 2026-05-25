package com.q1.qatania.model.gameboard

import kotlinx.serialization.Serializable

@Serializable
data class Building (
    val owner: String,
    val color: String,
    val type: BuildingType
)