package com.q1.qatania.model.gameboard

import kotlinx.serialization.Serializable

@Serializable
data class PortTransform(val x: Double, val y: Double, val rotation: Double)