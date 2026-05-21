package com.q1.qatania.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.q1.qatania.dataRepository.GameBoardRepository
import kotlinx.coroutines.flow.forEach

class GameBoardViewModel(
    private val repository: GameBoardRepository
) : ViewModel() {
    val boardFlow = repository.gameboardFlow()
}