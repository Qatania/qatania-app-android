package com.q1.qatania.repository

import android.content.Context
import com.q1.qatania.model.gameboard.GameBoardModel
import com.q1.qatania.model.messageDTO.MessageDTO
import com.q1.qatania.util.jsonParser
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class GameBoardRepository(
    private val context: Context
) : AbstractRepository() {
    fun gameboardFlow(): Flow<GameBoardModel> = flow {
        var i = 0
        while (true) {
            // if message from server with gameboard
            val message: String = _simulateServerMessage(i++)

            emit(jsonParser.decodeFromString(message))
            delay(10000)
        }
    }

    private fun _simulateServerMessage(i: Int): String {
        val name = if (i % 2 == 0 || true) "exampleboard.json" else "secondBoard.json"
        val json = context.assets
            .open(name)
            .bufferedReader()
            .use { it.readText() }
        return json
    }

    override fun handleMessage(messageDTO: MessageDTO) {
        /*
        TODO: Update GameBoard when receiving following message types
                MessageType.GAME_BOARD_JSON,
                MessageType.PLACE_SETTLEMENT,
                MessageType.PLACE_ROAD,
                MessageType.GAME_STARTED,
                MessageType.NEXT_TURN,
                MessageType.UPGRADE_SETTLEMENT
         */
    }
}