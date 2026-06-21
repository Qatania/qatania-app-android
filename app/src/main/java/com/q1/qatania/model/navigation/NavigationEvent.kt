package com.q1.qatania.model.navigation

sealed interface NavigationEvent {

    //Use data class to pass values on navigation in contrary to normal enums
    data class ToLobbyScreen(val lobbyId: String) : NavigationEvent
    data object ToJoinGameScreen : NavigationEvent


}