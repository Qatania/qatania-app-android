package com.q1.qatania.model.gameboard

import kotlinx.serialization.Serializable

@Serializable
data class PortVisuals (
    val portTransform: PortTransform,
    val settlementPosition1Id: Int,
    val settlementPosition2Id: Int,
    val buildingSite1Position: List<Double>,
    val buildingSite2Position: List<Double>,
)