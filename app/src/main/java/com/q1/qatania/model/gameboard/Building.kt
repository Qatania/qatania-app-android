package com.q1.qatania.model.gameboard

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Building (
    val owner: String,
    val color: String,
    val type: BuildingType
)