package com.q1.qatania.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.q1.qatania.MainApplication
import com.q1.qatania.model.messageDTO.MessageDTO
import com.q1.qatania.model.messageDTO.MessageType
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