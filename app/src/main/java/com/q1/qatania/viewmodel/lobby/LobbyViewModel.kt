package com.q1.qatania.viewmodel.lobby

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.q1.qatania.model.lobby.LobbyState
import com.q1.qatania.model.player.PlayerModel
import com.q1.qatania.repository.LobbyRepository
import com.q1.qatania.repository.PlayerInfoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class LobbyViewModel : ViewModel() {

    private val lobbyRepository = LobbyRepository.getInstance();

    private val playerInfoRepository = PlayerInfoRepository.getInstance();

    val lobbyState: StateFlow<LobbyState?> = lobbyRepository.lobbyState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val playerState: StateFlow<PlayerModel?> = lobbyRepository.lobbyState.map {
        it?.players[playerInfoRepository.getPlayerId()]
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val players: StateFlow<List<PlayerModel>> = lobbyRepository.lobbyState.map { lobbyState ->
        val playerId: String? = playerInfoRepository.getPlayerIdOrNull()
        val players: List<PlayerModel> = lobbyState?.players?.values?.toList() ?: emptyList()
        players.sortedByDescending { it.id == playerId }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setUsername(newUsername: String) {
        val lobbyId: String? = lobbyState.value?.lobbyId;

        if (lobbyId.isNullOrBlank()) {
            Log.e("LobbyViewModel", "Cannot update username, lobbyId is null");
            return;
        }

        lobbyRepository.setUsername(
            lobbyId = lobbyId,
            playerId = playerInfoRepository.getPlayerId(),
            newUsername = newUsername
        )
    }

    fun toggleReady() {
        val lobbyId: String? = lobbyState.value?.lobbyId;

        if (lobbyId.isNullOrBlank()) {
            Log.e("LobbyViewModel", "Cannot set ready, lobbyId is null");
            return;
        }

        lobbyRepository.toggleReady(
            lobbyId = lobbyId,
            playerId = playerInfoRepository.getPlayerId(),
        )
    }

    fun startGame() {
        val lobbyId: String? = lobbyState.value?.lobbyId;

        if (lobbyId.isNullOrBlank()) {
            Log.e("LobbyViewModel", "Cannot set ready, lobbyId is null");
            return;
        }

        lobbyRepository.startGame(
            lobbyId = lobbyId,
            hostPlayerId = playerInfoRepository.getPlayerId(),
        )
    }
}