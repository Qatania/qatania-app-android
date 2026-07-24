package com.q1.qatania.view.lobby

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.q1.qatania.model.player.PlayerModel
import com.q1.qatania.theme.catanButtons
import com.q1.qatania.viewmodel.lobby.LobbyViewModel

@Composable
fun LobbyScreen(
    modifier: Modifier = Modifier,
    viewModel: LobbyViewModel = viewModel(),
    onLeaveClick: () -> Unit = {},
) {
    val lobbyState by viewModel.lobbyState.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val players: List<PlayerModel> by viewModel.players.collectAsStateWithLifecycle()
    
    BackHandler { onLeaveClick() }

    Box(modifier = modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Lobby ID: ${lobbyState?.lobbyId}",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 32.sp),
            color = catanButtons
        )

        Spacer(modifier = Modifier.height(8.dp))

        val title: String =
            if (players.size > 1) "Players Ready: ${players.filter { it.isReady }.size} / ${players.size}"
            else "Need at least 2 players to start"
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (playerState?.isHost == true) {
            val allPlayersReady: Boolean = players.size > 1 && players.all { it.isReady }

            Button(
                onClick = { viewModel.startGame() },
                enabled = allPlayersReady,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF008000),
                    contentColor = Color.White,
                ),
                modifier = Modifier
                    .border(BorderStroke(1.dp, Color.Black), CircleShape)
                    .width(150.dp)
                    .height(40.dp)
            ) {
                Text(text = "Start Game")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        //Show player info
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(
                12.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            items(
                items = players.toList(),
                key = { p -> p.id },
            ) { player ->
                PlayerListItem(
                    player = player,
                    isPlayer = playerState?.id == player.id,
                    onChangeUsername = { viewModel.setUsername(it) },
                    onToggleReadyClick = { viewModel.toggleReady() }
                )
            }

        }


    }

        IconButton(
            onClick = onLeaveClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 32.dp, start = 16.dp)
                .size(40.dp)
                .background(catanButtons, CircleShape)
                .border(BorderStroke(1.dp, Color.Black), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }
    }

}