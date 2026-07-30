package com.q1.qatania.repository

import android.util.Log
import com.q1.qatania.MainApplication
import com.q1.qatania.model.dto.MessageDTO
import com.q1.qatania.model.dto.MessageType
import com.q1.qatania.model.gameboard.TileType
import com.q1.qatania.model.player.PlayerModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class GameRepository private constructor() : AbstractRepository() {

    private val _diceFlow = MutableStateFlow<DiceState?>(null);
    val diceFlow = _diceFlow.asStateFlow()

    private val _gameEndFlow = MutableStateFlow<GameEndState?>(null)
    val gameEndFlow = _gameEndFlow.asStateFlow()

    data class DiceState(
        val rollingPlayerUsername: String?,
        val dice1: Int = 1,
        val dice2: Int = 1,
    )

    data class GameEndState(
        val winnerId: String?,
        val leaderboard: List<PlayerModel>,
    )

    companion object {
        @Volatile
        private var INSTANCE: GameRepository? = null
        fun getInstance(): GameRepository =
            //Ensure thread safety
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: GameRepository().also { INSTANCE = it }
            }
    }

    override fun handleMessage(messageDTO: MessageDTO) {
        val type = messageDTO.type;

        when (type) {
            MessageType.DICE_RESULT -> handleDiceResult(messageDTO)
            MessageType.GAME_WON -> handleGameWon(messageDTO)

            else -> {}
        }

    }

    private fun handleDiceResult(messageDTO: MessageDTO) {
        val dice1 = messageDTO.message?.get("dice1")?.jsonPrimitive?.intOrNull ?: 0
        val dice2 = messageDTO.message?.get("dice2")?.jsonPrimitive?.intOrNull ?: 0
        val playerName = messageDTO.message?.get("rollingUsername")?.jsonPrimitive?.contentOrNull
            ?: messageDTO.players?.get(messageDTO.player)?.username
            ?: "Unknown Player"

        Log.d("GameRepository", "Dice result for $playerName: $dice1, $dice2")

        _diceFlow.value = DiceState(
            rollingPlayerUsername = playerName,
            dice1 = dice1,
            dice2 = dice2
        )

    }

    fun clearDiceState() {
        _diceFlow.value = null
    }

    private fun handleGameWon(messageDTO: MessageDTO) {
        val leaderboard =
            messageDTO.players?.values?.sortedByDescending { it.victoryPoints } ?: emptyList()

        Log.d("GameRepository", "Game won by ${messageDTO.player}, leaderboard: $leaderboard")

        _gameEndFlow.value = GameEndState(
            winnerId = messageDTO.player,
            leaderboard = leaderboard
        )
    }

    fun clearGameEndState() {
        _gameEndFlow.value = null
    }

    fun buildRoad(playerId: String, lobbyId: String, roadId: Int) {
        val message = buildJsonObject { put("roadId", roadId) }
        val messageDTO = MessageDTO(
            type = MessageType.PLACE_ROAD,
            lobbyId = lobbyId,
            player = playerId,
            message = message
        )
        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun buildSettlement(playerId: String, lobbyId: String, settlementPositionId: Int) {
        val message = buildJsonObject {
            put("settlementPositionId", settlementPositionId)
        }

        val messageDTO = MessageDTO(
            type = MessageType.PLACE_SETTLEMENT,
            lobbyId = lobbyId,
            player = playerId,
            message = message
        )

        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun upgradeSettlement(playerId: String, lobbyId: String, settlementPositionId: Int) {
        val message = buildJsonObject {
            put("settlementPositionId", settlementPositionId)
        }

        val messageDTO = MessageDTO(
            type = MessageType.UPGRADE_SETTLEMENT,
            lobbyId = lobbyId,
            player = playerId,
            message = message
        )

        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun submitBankTrade(
        playerId: String,
        lobbyId: String,
        tradeRequest: Pair<Map<TileType, Int>, Map<TileType, Int>>
    ) {
        val message = buildJsonObject {
            put("offeredResources", Json.encodeToJsonElement(tradeRequest.first))
            put("targetResources", Json.encodeToJsonElement(tradeRequest.second))
        }

        val messageDTO = MessageDTO(
            type = MessageType.TRADE_WITH_BANK,
            lobbyId = lobbyId,
            player = playerId,
            message = message
        )
        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun placeRobber(playerId: String, lobbyId: String, tileId: Int) {
        val messageDTO = MessageDTO(
            type = MessageType.PLACE_ROBBER,
            lobbyId = lobbyId,
            player = playerId,
            message = buildJsonObject { put("tileId", tileId) }
        )
        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun endTurn(playerId: String, lobbyId: String) {
        val messageDTO = MessageDTO(
            type = MessageType.END_TURN,
            lobbyId = lobbyId,
            player = playerId,
        )

        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun rollDice(playerId: String, lobbyId: String) {
        val messageDTO = MessageDTO(
            type = MessageType.ROLL_DICE,
            lobbyId = lobbyId,
            player = playerId,
        )

        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun reportPlayer(playerId: String, lobbyId: String, reportedPlayerId: String) {
        val messageDTO = MessageDTO(
            type = MessageType.REPORT_PLAYER,
            player = playerId,
            lobbyId = lobbyId,
            message = buildJsonObject {
                put("reportedId", reportedPlayerId)
            }
        )

        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun cheat(playerId: String, lobbyId: String, tileType: TileType) {
        val message = buildJsonObject {
            put("resource", tileType.toString())
        }

        val messageDTO = MessageDTO(
            type = MessageType.CHEAT_ATTEMPT,
            lobbyId = lobbyId,
            player = playerId,
            message = message
        )

        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }


}