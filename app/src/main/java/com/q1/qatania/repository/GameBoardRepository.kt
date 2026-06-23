package com.q1.qatania.repository

import android.util.Log
import com.q1.qatania.model.dto.MessageDTO
import com.q1.qatania.model.dto.MessageType
import com.q1.qatania.model.gameboard.GameBoardModel
import com.q1.qatania.util.jsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class GameBoardRepository : AbstractRepository() {

    private val _gameboardFlow = MutableStateFlow<GameBoardModel?>(null)
    val gameboardState = _gameboardFlow.asStateFlow()

    companion object {
        @Volatile
        private var INSTANCE: GameBoardRepository? = null
        fun getInstance(): GameBoardRepository =
            //Ensure thread safety
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: GameBoardRepository().also { INSTANCE = it }
            }
    }

    override fun handleMessage(messageDTO: MessageDTO) {
        Log.v("GameBoardRepository", "Received $messageDTO")
        when (messageDTO.type) {
            MessageType.GAME_BOARD_JSON,
            MessageType.PLACE_SETTLEMENT,
            MessageType.PLACE_ROAD,
            MessageType.GAME_STARTED,
            MessageType.NEXT_TURN,
            MessageType.UPGRADE_SETTLEMENT -> handleGameboardUpdate(messageDTO)

            else -> {}
        }
    }

    private fun handleGameboardUpdate(messageDTO: MessageDTO) {
        val message = messageDTO.message ?: run {
            Log.e("GameBoardRepository", "Message is null")
            return
        }

        if (!message.containsKey("gameboard")) {
            Log.e("GameBoardRepository", "Message is missing gameboard")
            return;
        }

        val gameboardJsonString = jsonParser.encodeToString(message["gameboard"])
        val gameboard: GameBoardModel =
            jsonParser.decodeFromString<GameBoardModel>(gameboardJsonString)
        Log.v("GameBoardRepository", "Updating gameboard with $gameboard")
        _gameboardFlow.update { gameboard }
    }


}