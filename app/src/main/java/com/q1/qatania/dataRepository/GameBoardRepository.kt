package com.q1.qatania.dataRepository

import android.content.Context
import com.q1.qatania.model.GameBoardModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json


class GameBoardRepository(
    private val context: Context
) {
    fun gameboardFlow(): Flow<GameBoardModel> = flow {
        var i = 0
        while(true) {
            // if message from server with gameboard
            val message: String = _simulateServerMessage(i++)


            emit(Json.decodeFromString(message))
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
}