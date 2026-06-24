package com.q1.qatania.viewmodel.game

import android.util.Log
import androidx.lifecycle.ViewModel
import com.q1.qatania.MainApplication
import com.q1.qatania.model.dto.MessageDTO
import com.q1.qatania.model.dto.MessageType
import com.q1.qatania.model.gameboard.TileType
import com.q1.qatania.repository.PlayerInfoRepository
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class GameViewModel : ViewModel() {

    val playerInfoRepository = PlayerInfoRepository.getInstance()

    fun cheat(lobbyId: String, tileType: TileType) {
        Log.d("GameViewModel", "Cheating attempt with tile type $tileType")
        val message = buildJsonObject {
            put("resource", tileType.toString())
        }

        val messageDTO = MessageDTO(
            type = MessageType.CHEAT_ATTEMPT,
            lobbyId = lobbyId,
            player = playerInfoRepository.getPlayerId(),
            message = message
        )

        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun rollDice(lobbyId: String) {
        val messageDTO = MessageDTO(
            type = MessageType.ROLL_DICE,
            lobbyId = lobbyId,
            player = playerInfoRepository.getPlayerId(),
        )

        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

    fun handleEndTurnClick(lobbyId: String) {
        val messageDTO = MessageDTO(
            type = MessageType.END_TURN,
            lobbyId = lobbyId,
            player = playerInfoRepository.getPlayerId(),
        )

        MainApplication.getInstance().getWebSocketClient().sendMessage(messageDTO)
    }

}