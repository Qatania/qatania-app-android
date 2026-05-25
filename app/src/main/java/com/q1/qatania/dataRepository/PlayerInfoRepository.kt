package com.q1.qatania.dataRepository

import com.q1.qatania.model.player.PlayerModel
import com.q1.qatania.util.jsonParser
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class PlayerInfoRepository() {
    fun playerFlow(): Flow<PlayerModel> = flow {
        var temp = 2
        while(true) {
            val message: String = _simulateServerMessage(temp++)

            emit(jsonParser.decodeFromString(message))
            delay(10000)
        }
    }

    private fun _simulateServerMessage(value: Int): String {
        return """{
            "id": "test",
            "username": "ultimateWinner",
            "color": "#00FF00",
            "isHost": true,
            "isReady": false,
            "isActivePlayer": false,
            "canRollDice": false,
            "isSetupRound": false,
            "victoryPoints": 4,
            "resources": {
            "WHEAT": 6,
            "SHEEP": 0,
            "WOOD": 5,
            "CLAY": $value,
            "ORE": 3
        }
        }"""
    }
}