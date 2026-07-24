package com.q1.qatania.repository

import android.util.Log
import com.q1.qatania.model.dto.MessageDTO
import com.q1.qatania.model.dto.MessageType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive


class PlayerInfoRepository private constructor() : AbstractRepository() {

    private lateinit var playerId: String;

    companion object {
        @Volatile
        private var INSTANCE: PlayerInfoRepository = PlayerInfoRepository()
        fun getInstance(): PlayerInfoRepository = INSTANCE
    }

    fun getPlayerId(): String {
        return playerId
    }

    fun getPlayerIdOrNull(): String? {
        return if (::playerId.isInitialized) playerId else null
    }

    override fun handleMessage(messageDTO: MessageDTO) {
        when (messageDTO.type) {
            MessageType.CONNECTION_SUCCESSFUL -> handleInitialConnection(messageDTO)
            else -> {}
        }
    }

    private fun handleInitialConnection(messageDTO: MessageDTO) {
        val message: JsonObject? = messageDTO.message;

        if (message == null || !message.containsKey("playerId")) {
            Log.e("PlayerInfoRepository", "Initial connection message has not playerId")
            return;
        }

        message["playerId"]?.let {
            playerId = it.jsonPrimitive.content
            Log.d("PlayerInfoRepository", "Received player ID: $playerId")
        }
    }
}