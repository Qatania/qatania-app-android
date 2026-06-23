package com.q1.qatania.repository

import android.util.Log
import com.q1.qatania.MainApplication
import com.q1.qatania.model.dto.MessageDTO
import com.q1.qatania.model.dto.MessageType
import com.q1.qatania.model.lobby.LobbyState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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

    fun setUsername(lobbyId: String, playerId: String, newUsername: String) {
        val message = buildJsonObject {
            put("username", newUsername)
        }

        val messageDTO = MessageDTO(
            type = MessageType.SET_USERNAME,
            player = playerId,
            lobbyId = lobbyId,
            message = message
        )
        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun toggleReady(lobbyId: String, playerId: String) {
        val messageDTO = MessageDTO(
            type = MessageType.SET_READY,
            player = playerId,
            lobbyId = lobbyId,
        )
        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun startGame(lobbyId: String, hostPlayerId: String) {
        val messageDTO = MessageDTO(
            type = MessageType.START_GAME,
            player = hostPlayerId,
            lobbyId = lobbyId,
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
            MessageType.GAME_STARTED,
            MessageType.GAME_BOARD_JSON,
            MessageType.PLACE_SETTLEMENT,
            MessageType.PLACE_ROAD,
            MessageType.NEXT_TURN,
            MessageType.UPGRADE_SETTLEMENT -> handlePlayerResourceUpdate(messageDTO)

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

            val previousState: LobbyState? = _lobbyFlow.value
            _lobbyFlow.value = LobbyState(
                lobbyId,
                messageDTO.players ?: emptyMap(),
                previousState?.gameStarted ?: false
            )
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

        val previousState: LobbyState? = _lobbyFlow.value
        _lobbyFlow.value = LobbyState(
            lobbyId,
            messageDTO.players ?: emptyMap(),
            previousState?.gameStarted ?: false
        )
        Log.d("LobbyRepository", "Updated Lobby $lobbyId")
    }

    private fun handleLobbyClosed(messageDTO: MessageDTO) {
        Log.d("LobbyRepository", "Lobby ${messageDTO.lobbyId} was closed")
        _lobbyFlow.value = null;
    }

    private fun handlePlayerResourceUpdate(messageDTO: MessageDTO) {
        val lobbyId: String? = messageDTO.lobbyId;
        if (lobbyId.isNullOrBlank()) {
            Log.d("LobbyRepository", "Received lobbyId is empty, so skipping")
            return
        }

        _lobbyFlow.value = LobbyState(lobbyId, messageDTO.players ?: emptyMap(), true)
    }

}