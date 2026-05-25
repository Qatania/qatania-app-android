package com.q1.qatania.model.messageDTO

import com.q1.qatania.model.player.PlayerModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class MessageDTO(
    val type: MessageType,
    val player: String? = null,
    val lobbyId: String? = null,
    val players: Map<String, PlayerModel>? = null,
    val message: JsonObject? = null
) {
}