package com.q1.qatania.viewmodel.game

import android.util.Log
import androidx.lifecycle.ViewModel
import com.q1.qatania.MainApplication
import com.q1.qatania.model.dto.MessageDTO
import com.q1.qatania.model.dto.MessageType
import com.q1.qatania.model.gameboard.BuildingType
import com.q1.qatania.model.gameboard.Road
import com.q1.qatania.model.gameboard.SettlementPosition
import com.q1.qatania.model.gameboard.TileType
import com.q1.qatania.repository.GameRepository
import com.q1.qatania.repository.GameRepository.DiceState
import com.q1.qatania.repository.PlayerInfoRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put

class GameViewModel : ViewModel() {

    val playerInfoRepository = PlayerInfoRepository.getInstance()
    val gameRepository = GameRepository.getInstance()
    val self = playerInfoRepository.getPlayerId()

    fun cheat(lobbyId: String, tileType: TileType) {
        Log.d("GameViewModel", "Cheating attempt with tile type $tileType")
        val message = buildJsonObject {
            put("resource", tileType.toString())
        }

        val messageDTO = MessageDTO(
            type = MessageType.CHEAT_ATTEMPT,
            lobbyId = lobbyId,
            player = self,
            message = message
        )

        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun report(lobbyId: String, playerToId: String) {
        val playerFromId = self
        Log.d("GameViewModel", "$playerFromId is reporting $playerToId")

        val messageDTO = MessageDTO(
            type = MessageType.REPORT_PLAYER,
            player = playerFromId,
            lobbyId = lobbyId,
            message = buildJsonObject {
                put("reportedId", playerToId)
            }
        )

        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun rollDice(lobbyId: String) {
        val messageDTO = MessageDTO(
            type = MessageType.ROLL_DICE,
            lobbyId = lobbyId,
            player = self,
        )

        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun handleEndTurnClick(lobbyId: String) {
        val messageDTO = MessageDTO(
            type = MessageType.END_TURN,
            lobbyId = lobbyId,
            player = self,
        )

        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun handleRoadTap(lobbyId: String, road: Road) {
        Log.d("GameViewModel", "Tapped road $road")
        val message = buildJsonObject { put("roadId", road.id) }
        val messageDTO = MessageDTO(
            type = MessageType.PLACE_ROAD,
            lobbyId = lobbyId,
            player = self,
            message = message
        )

        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun handleSettlementTap(lobbyId: String, settlementPosition: SettlementPosition) {
        Log.d("GameViewModel", "Tapped settlement position $settlementPosition")

        val isUpgrade: Boolean = settlementPosition.building?.type == BuildingType.Settlement
        val type: MessageType =
            if (isUpgrade) MessageType.UPGRADE_SETTLEMENT else MessageType.PLACE_SETTLEMENT

        val message = buildJsonObject {
            put("settlementPositionId", settlementPosition.id)

        }

        val messageDTO = MessageDTO(
            type = type,
            lobbyId = lobbyId,
            player = self,
            message = message
        )

        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }


    val diceState: StateFlow<DiceState?> = gameRepository.diceFlow
    fun clearDiceState() {
        gameRepository.clearDiceState()
    }

    fun submitBankTrade(tradeRequest: Pair<Map<TileType, Int>, Map<TileType, Int>>, lobbyId: String){
        val message = buildJsonObject {
            put("offeredResources", Json.encodeToJsonElement(tradeRequest.first))
            put("targetResources", Json.encodeToJsonElement(tradeRequest.second))
        }

        val messageDTO = MessageDTO(
            type = MessageType.TRADE_WITH_BANK,
            lobbyId = lobbyId,
            player = self,
            message = message
        )
        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }
}