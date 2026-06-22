package com.q1.qatania.model.lobby

import com.q1.qatania.model.player.PlayerModel

data class LobbyState(
    val lobbyId: String,
    val players: Map<String, PlayerModel>,
    val gameStarted: Boolean,
)
