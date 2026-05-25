package com.q1.qatania.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.q1.qatania.dataRepository.PlayerInfoRepository
import com.q1.qatania.model.gameboard.TileType

class PlayerInfoViewModel() : ViewModel() {
    private val repository = PlayerInfoRepository()

    val boardFlow = repository.playerFlow()

    fun cheat(resourceType: TileType){
        Log.d(
            "Cheating",
            "$resourceType"
        )
    }
}