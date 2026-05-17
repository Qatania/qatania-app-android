package com.q1.qatania.model

import kotlinx.serialization.Serializable

@Serializable
data class SettlementPosition (
    val id: Int,
    val building: Building?,
    val coordinates: List<Double>
)