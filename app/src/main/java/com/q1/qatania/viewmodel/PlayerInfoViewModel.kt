package com.q1.qatania.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.q1.qatania.repository.PlayerInfoRepository
import com.q1.qatania.model.gameboard.TileType
import com.q1.qatania.model.player.PlayerModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PlayerInfoViewModel(
    val id: String = "test"
) : ViewModel() {

    private val repository = PlayerInfoRepository()

    val playersMapFlow = repository.playerFlow()

    val playerFlow: StateFlow<PlayerModel?> = playersMapFlow
        .map { players -> players[id] }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun cheat(resourceType: TileType) {
        Log.d(
            "Cheating", "$resourceType"
        )
    }
}