package com.q1.qatania.model

data class SettlementPosition (
    val id: Int,
    val building: Building?,
    val coordinates: List<Double>
)