package com.q1.qatania.repository

import android.util.Log
import com.q1.qatania.MainApplication
import com.q1.qatania.model.dto.MessageDTO
import com.q1.qatania.model.dto.MessageType
import com.q1.qatania.model.lobby.LobbyState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LobbyRepository private constructor() : AbstractRepository() {

    private val _lobbyFlow = MutableStateFlow<LobbyState?>(null);
    val lobbyState = _lobbyFlow.asStateFlow()

    companion object {
        @Volatile
        private var INSTANCE: LobbyRepository? = null
        fun getInstance(): LobbyRepository =
            //Ensure thread safety
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: LobbyRepository().also { INSTANCE = it }
            }
    }

    fun joinLobby(lobbyId: String, playerId: String) {
        val messageDTO = MessageDTO(
            type = MessageType.JOIN_LOBBY,
            player = playerId,
            lobbyId = lobbyId
        )
        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    override fun handleMessage(messageDTO: MessageDTO) {
        /*
            TODO: Handle lobby messages
                * LOBBY_CREATED
                * SET_READY
                * LOBBY_LIST
                * PLAYER_JOINED
                * LOBBY_UPDATED
                * LOBBY_CLOSED
                * GAME_STARTED
         */
        val type = messageDTO.type;

        when (type) {
            MessageType.PLAYER_JOINED -> handlePlayerJoined(messageDTO)
            MessageType.LOBBY_UPDATED -> handleLobbyUpdated(messageDTO)
            MessageType.LOBBY_CLOSED -> handleLobbyClosed(messageDTO)
            else -> {}
        }

    }


    private fun handlePlayerJoined(messageDTO: MessageDTO) {
        if (_lobbyFlow.value == null) {
            //Initial join to lobby, set lobby id
            val lobbyId: String? = messageDTO.lobbyId;

            if (lobbyId.isNullOrBlank()) {
                Log.d("LobbyRepository", "Received lobbyId is empty, so skipping")
                return
            }

            _lobbyFlow.value = LobbyState(lobbyId, messageDTO.players ?: emptyMap())
            Log.d("LobbyRepository", "Joined Lobby $lobbyId")
            return;
        }
    }

    private fun handleLobbyUpdated(messageDTO: MessageDTO) {
        val lobbyId: String? = messageDTO.lobbyId;
        if (lobbyId.isNullOrBlank()) {
            Log.d("LobbyRepository", "Received lobbyId is empty, so skipping")
            return
        }

        _lobbyFlow.value = LobbyState(lobbyId, messageDTO.players ?: emptyMap())
        Log.d("LobbyRepository", "Updated Lobby $lobbyId")
    }

    private fun handleLobbyClosed(messageDTO: MessageDTO) {
        val lobbyId = _lobbyFlow.value;
        Log.d("LobbyRepository", "Lobby $lobbyId was closed")
        _lobbyFlow.value = null;
    }

}