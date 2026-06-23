package com.q1.qatania.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.q1.qatania.model.navigation.NavigationEvent
import com.q1.qatania.repository.LobbyRepository
import com.q1.qatania.repository.PlayerInfoRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MenuViewModel() : ViewModel() {

    private val lobbyRepository: LobbyRepository = LobbyRepository.getInstance()
    private val playerInfoRepository: PlayerInfoRepository = PlayerInfoRepository.getInstance()

    //Navigation channel to catch navigation events
    private val _navigationChannel = Channel<NavigationEvent>(Channel.BUFFERED)
    val navigationEvents = _navigationChannel.receiveAsFlow()

    init {
        observeLobbyState()
    }

    private fun observeLobbyState() {
        viewModelScope.launch {
            lobbyRepository.lobbyState.collect { lobbyState ->

                Log.v("MenuViewModel", "Updated lobbyState: $lobbyState")

                if (lobbyState?.lobbyId.isNullOrEmpty()) {
                    //Lobby closed
                    _navigationChannel.send(NavigationEvent.ToJoinGameScreen)
                    return@collect
                }

                val hasGameStarted = lobbyState.gameStarted;
                if (hasGameStarted) {
                    //Send player to game screen
                    _navigationChannel.send(NavigationEvent.ToGameScreen(lobbyState.lobbyId))
                    return@collect
                }

                //Send player to lobby screen
                _navigationChannel.send(NavigationEvent.ToLobbyScreen(lobbyState.lobbyId))

            }
        }
    }

    fun joinLobby(lobbyId: String) {
        val playerId = playerInfoRepository.getPlayerId();
        lobbyRepository.joinLobby(
            lobbyId = lobbyId,
            playerId = playerId
        )
    }
}