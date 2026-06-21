package com.q1.qatania.viewmodel.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.q1.qatania.model.lobby.LobbyState
import com.q1.qatania.repository.LobbyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class LobbyViewModel : ViewModel() {

    private val lobbyRepository = LobbyRepository.Companion.getInstance();

    val lobbyState: StateFlow<LobbyState?> = lobbyRepository.lobbyState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = null
    )
}