package com.q1.qatania.model.gameboard

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class SettlementPosition (
    val id: Int,
    val building: Building?,
    val coordinates: List<Double>
)