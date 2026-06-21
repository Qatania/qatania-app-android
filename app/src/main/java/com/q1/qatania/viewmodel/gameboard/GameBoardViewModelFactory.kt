package com.q1.qatania.viewmodel.gameboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.q1.qatania.repository.GameBoardRepository

class GameBoardViewModelFactory(
    private val repository: GameBoardRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameBoardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameBoardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}