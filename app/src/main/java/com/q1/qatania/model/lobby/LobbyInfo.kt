package com.q1.qatania.model.lobby

import kotlinx.serialization.Serializable

@Serializable
data class LobbyInfo(
    val id: String,
    val hostPlayer: String,
    val playerCount: Int
)
