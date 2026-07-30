package com.q1.qatania.view.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.q1.qatania.repository.GameRepository
import com.q1.qatania.util.getDiceImage

@Composable
fun DiceResultPopup(
    modifier: Modifier = Modifier,
    diceState: GameRepository.DiceState,
    onDismiss: () -> Unit
) {

    val total = diceState.dice1 + diceState.dice2;

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onDismiss()
                },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(32.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "${diceState.rollingPlayerUsername} rolled:",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Image(
                            painter = painterResource(id = getDiceImage(diceState.dice1)),
                            contentDescription = "Dice 1",
                            modifier = Modifier.size(64.dp)
                        )
                        Image(
                            painter = painterResource(id = getDiceImage(diceState.dice2)),
                            contentDescription = "Dice 2",
                            modifier = Modifier.size(64.dp)
                        )
                    }

                    Text(
                        text = "Total: $total",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}