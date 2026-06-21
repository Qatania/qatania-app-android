package com.q1.qatania.view.lobby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.q1.qatania.util.jsonParser
import com.q1.qatania.viewmodel.lobby.LobbyViewModel

@Composable
fun LobbyScreen(
    modifier: Modifier = Modifier,
    viewModel: LobbyViewModel = viewModel()
) {
    val lobbyState by viewModel.lobbyState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Lobby ${lobbyState?.lobbyId}")

        Spacer(modifier = Modifier.height(12.dp))

        //Show player info
        Text(jsonParser.encodeToString(lobbyState?.players))

    }

}