package com.q1.qatania.repository

import android.util.Log
import com.q1.qatania.model.messageDTO.MessageDTO
import com.q1.qatania.model.messageDTO.MessageType
import com.q1.qatania.model.player.PlayerModel
import com.q1.qatania.util.jsonParser
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive


class PlayerInfoRepository : AbstractRepository() {

    fun playerFlow(): Flow<Map<String, PlayerModel>> = flow {
        var temp = 2
        while (true) {
            val message: String = _simulateServerMessage(temp++)

            val messageDTO = jsonParser.decodeFromString<MessageDTO>(message)
            emit(messageDTO.players ?: emptyMap())
            delay(10000)
        }
    }

    private fun _simulateServerMessage(value: Int): String {
        return """
                {
              "type": "PLAYER_RESOURCE_UPDATE",
              "player": "test",
              "lobbyId": "lobby1"
              "players": {
                "test": {
                  "id": "test",
                  "username": "ultimateWinner",
                  "color": "#00FF00",
                  "isHost": true,
                  "isReady": false,
                  "isActivePlayer": true,
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
                },
                "test2": {
                  "id": "test2",
                  "username": "ultimateLoser",
                  "color": "#0000FF",
                  "isHost": false,
                  "isReady": false,
                  "isActivePlayer": false,
                  "canRollDice": false,
                  "isSetupRound": false,
                  "victoryPoints": 0,
                  "resources": {
                    "WHEAT": 6,
                    "SHEEP": 0,
                    "WOOD": 5,
                    "CLAY": 2,
                    "ORE": 3
                  }
                }
              },
              "message": {}
            }
        """.trimIndent()
    }

    override fun handleMessage(messageDTO: MessageDTO) {
        val type: MessageType = messageDTO.type
        val message: JsonObject? = messageDTO.message

        if (type == MessageType.CONNECTION_SUCCESSFUL && message != null) {
            message["playerId"]?.let {
                val playerId = it.jsonPrimitive.content
                Log.d("PlayerInfoRepository", "Received player ID: $playerId")
                //TODO: Do something with player ID, maybe store in view model?
            }
        }
    }
}