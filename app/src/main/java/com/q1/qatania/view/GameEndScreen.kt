package com.q1.qatania.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.q1.qatania.repository.GameRepository
import com.q1.qatania.theme.buttons

@Composable
fun GameEndScreen(
    modifier: Modifier = Modifier,
    selfId: String,
    gameEndState: GameRepository.GameEndState,
    onReturnToMenu: () -> Unit
) {
    val leaderboard = gameEndState.leaderboard
    val winner =
        leaderboard.firstOrNull { it.id == gameEndState.winnerId } ?: leaderboard.firstOrNull()
    val self = leaderboard.firstOrNull { it.id == selfId }
    val isWinner = selfId == gameEndState.winnerId

    val medalColors = listOf(
        Color(0xFFFFD700),
        Color(0xFFC0C0C0),
        Color(0xFFCD7F32),
    )

    Dialog(
        onDismissRequest = onReturnToMenu,
        properties = DialogProperties(usePlatformDefaultWidth = false)

    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .heightIn(max = 560.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    if (isWinner) {
                        Text(
                            text = "VICTORY!",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Congratulations, ${winner?.username ?: "you"}!",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "DEFEAT",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(8.dp))
                        val funnyMessage = when {
                            (self?.victoryPoints
                                ?: 0) >= 8 -> "So close! You were just a step away."

                            (self?.victoryPoints
                                ?: 0) >= 5 -> "Not your best performance, better luck next time."

                            else -> "At least you built a nice long road?"
                        }
                        Text(
                            text = funnyMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    leaderboard.forEachIndexed { index, player ->
                        val rankLabel = when (index) {
                            0 -> "1st"
                            1 -> "2nd"
                            2 -> "3rd"
                            else -> "${index + 1}th"
                        }
                        val badgeColor =
                            medalColors.getOrNull(index) ?: MaterialTheme.colorScheme.surfaceVariant
                        val badgeTextColor =
                            if (index < medalColors.size) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant

                        Row(
                            modifier = Modifier
                                .padding(vertical = 6.dp)
                                .fillMaxWidth()
                                .height(56.dp)
                                .border(
                                    1.5.dp,
                                    MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(10.dp)
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(56.dp)
                                    .fillMaxHeight()
                                    .background(
                                        badgeColor,
                                        RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = rankLabel,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeTextColor
                                )
                            }
                            Text(
                                text = player.username ?: "Unknown",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp)
                            )
                            Text(
                                text = "${player.victoryPoints} VP",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = onReturnToMenu,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttons,
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Return to menu", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
