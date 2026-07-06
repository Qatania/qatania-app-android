package com.q1.qatania.view.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.q1.qatania.model.lobby.LobbyInfo
import com.q1.qatania.theme.catanClay
import com.q1.qatania.theme.catanGold

@Composable
fun LobbyBrowserScreen(
    lobbies: List<LobbyInfo> = emptyList(),
    onRefreshClick: () -> Unit,
    onJoinLobbyClick: (String) -> Unit,
) {

    LaunchedEffect(Unit) {
        onRefreshClick()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(catanClay)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 40.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Lobby Browser",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 32.sp),
                color = catanGold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                stickyHeader {

                    if (lobbies.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 0.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Start
                        ) {

                            Spacer(modifier = Modifier.size(48.dp))

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "Lobby ID",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1.5f)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "Host",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1.5f)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "Player Count",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1.5f)
                            )

                            Spacer(modifier = Modifier
                                .height(40.dp)
                                .width(ButtonDefaults.MinWidth))
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Currently no open lobbies",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }

                if (lobbies.isNotEmpty()) {
                    items(lobbies) { lobby ->
                        LobbyListItem(
                            lobby = lobby,
                            onJoinLobbyClick = onJoinLobbyClick
                        )
                    }
                }

            }
        }

        IconButton(
            onClick = onRefreshClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp, end = 16.dp)
                .size(40.dp)
                .background(catanGold, CircleShape)
                .border(BorderStroke(1.dp, Color.Black), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = "Refresh",
                tint = Color.Black
            )
        }
    }
}