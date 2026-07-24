package com.q1.qatania.repository

import android.util.Log
import com.q1.qatania.MainApplication
import com.q1.qatania.model.dto.MessageDTO
import com.q1.qatania.model.dto.MessageType
import com.q1.qatania.model.gameboard.GameBoardModel
import com.q1.qatania.model.lobby.LobbyInfo
import com.q1.qatania.model.lobby.LobbyState
import com.q1.qatania.model.player.PlayerModel
import com.q1.qatania.util.jsonParser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class LobbyRepository private constructor() : AbstractRepository() {

    private val _lobbyFlow = MutableStateFlow<LobbyState?>(null);
    val lobbyState = _lobbyFlow.asStateFlow()

    private val _lobbiesFlow = MutableStateFlow<List<LobbyInfo>>(emptyList())
    val lobbiesState = _lobbiesFlow.asStateFlow()

    private var leftLobbyId: String? = null

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
        leftLobbyId = null
        val messageDTO = MessageDTO(
            type = MessageType.JOIN_LOBBY,
            player = playerId,
            lobbyId = lobbyId
        )
        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun createLobby(playerId: String) {
        leftLobbyId = null
        val messageDTO = MessageDTO(
            type = MessageType.CREATE_LOBBY,
            player = playerId,
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

    fun leaveLobby(lobbyId: String, playerId: String) {
        val messageDTO = MessageDTO(
            type = MessageType.LEAVE_LOBBY,
            player = playerId,
            lobbyId = lobbyId
        )
        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)

        leftLobbyId = lobbyId
        _lobbyFlow.value = null
        Log.d("LobbyRepository", "Left lobby $lobbyId")
    }

    fun getLobbies(playerId: String) {
        val messageDTO = MessageDTO(
            type = MessageType.GET_LOBBIES,
            player = playerId
        )
        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    override fun handleMessage(messageDTO: MessageDTO) {
        val type = messageDTO.type;

        when (type) {
            MessageType.LOBBY_CREATED,
            MessageType.PLAYER_JOINED,
            MessageType.LOBBY_UPDATED -> handleLobbyUpdated(messageDTO)
            MessageType.LOBBY_CLOSED -> handleLobbyClosed(messageDTO)
            MessageType.GAME_STARTED,
            MessageType.GAME_BOARD_JSON,
            MessageType.PLACE_SETTLEMENT,
            MessageType.PLACE_ROAD,
            MessageType.NEXT_TURN,
            MessageType.DICE_RESULT,
            MessageType.PLAYER_RESOURCE_UPDATE,
            MessageType.UPGRADE_SETTLEMENT -> handlePlayerResourceUpdate(messageDTO)
            MessageType.LOBBY_LIST -> handleLobbyList(messageDTO)

            else -> {}
        }

    }

    private fun handleLobbyList(messageDTO: MessageDTO) {
        val message = messageDTO.message ?: run {
            Log.e("LobbyRepository", "Message is null")
            return
        }

        if (!message.containsKey("lobbies")) {
            Log.e("LobbyRepository", "Message is missing lobbies list")
            return;
        }

        val lobbyListJsonString = jsonParser.encodeToString(message["lobbies"])
        val lobbiesList: List<LobbyInfo> =
            jsonParser.decodeFromString<List<LobbyInfo>>(lobbyListJsonString)

        _lobbiesFlow.value = lobbiesList
    }

    private fun handleLobbyUpdated(messageDTO: MessageDTO) {
        val lobbyId: String = messageDTO.lobbyId ?: ""
        val players = messageDTO.players ?: emptyMap()

        if (!isRelevantLobbyUpdate(lobbyId, players)) {
            return
        }

        val previousState: LobbyState? = _lobbyFlow.value
        _lobbyFlow.value = LobbyState(
            lobbyId,
            players,
            previousState?.gameStarted ?: false
        )
        Log.d("LobbyRepository", "Updated Lobby $lobbyId")
    }

    private fun handleLobbyClosed(messageDTO: MessageDTO) {
        val lobbyId: String? = messageDTO.lobbyId
        val currentLobbyId: String? = _lobbyFlow.value?.lobbyId

        if (currentLobbyId != null && currentLobbyId != lobbyId) {
            Log.d("LobbyRepository", "Lobby $lobbyId was closed, but we are in $currentLobbyId")
            return
        }

        Log.d("LobbyRepository", "Lobby $lobbyId was closed")
        _lobbyFlow.value = null
    }

    private fun handlePlayerResourceUpdate(messageDTO: MessageDTO) {
        val lobbyId: String = messageDTO.lobbyId ?: ""
        val players = messageDTO.players ?: emptyMap()

        if (!isRelevantLobbyUpdate(lobbyId, players)) {
            return
        }

        _lobbyFlow.value = LobbyState(lobbyId, players, true)
    }

    private fun isRelevantLobbyUpdate(lobbyId: String, players: Map<String, PlayerModel>): Boolean {
        if (lobbyId.isBlank()) {
            Log.d("LobbyRepository", "Received lobbyId is empty, so skipping")
            return false
        }

        if (lobbyId == leftLobbyId) {
            Log.d("LobbyRepository", "Received update of left lobby $lobbyId, so skipping")
            return false
        }

        val currentLobbyId: String? = _lobbyFlow.value?.lobbyId
        if (currentLobbyId != null && currentLobbyId != lobbyId) {
            Log.d("LobbyRepository", "Received update of lobby $lobbyId while in $currentLobbyId")
            return false
        }

        val playerId: String? = PlayerInfoRepository.getInstance().getPlayerIdOrNull()
        if (playerId != null && !players.containsKey(playerId)) {
            Log.d("LobbyRepository", "Player is not part of lobby $lobbyId, so skipping")
            return false
        }

        return true
    }

}