package com.q1.qatania.repository

import android.util.Log
import com.q1.qatania.model.dto.MessageDTO
import com.q1.qatania.model.dto.MessageType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

class GameRepository private constructor() : AbstractRepository() {

    private val _diceFlow = MutableStateFlow<DiceState?>(null);
    val diceFlow = _diceFlow.asStateFlow()

    data class DiceState(
        val rollingPlayerUsername: String?,
        val dice1: Int = 1,
        val dice2: Int = 1,
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
            MessageType.DICE_RESULT -> handleDiceResult(messageDTO)

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

        _diceFlow.value =  DiceState(
            rollingPlayerUsername = playerName,
            dice1 = dice1,
            dice2 = dice2
        )

    }

    fun clearDiceState() {
        _diceFlow.value = null
    }

}