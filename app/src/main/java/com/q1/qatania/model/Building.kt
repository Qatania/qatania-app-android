package com.q1.qatania.model

import kotlinx.serialization.Serializable

@Serializable
data class Building (
    val owner: String,
    val color: String,
    val type: BuildingType
)