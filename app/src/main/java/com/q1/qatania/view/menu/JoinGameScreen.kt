package com.q1.qatania.view.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.q1.qatania.theme.catanButtons

@Composable
fun JoinGameScreen(
    onJoinClick: (String) -> Unit,
    onBrowseClick: () -> Unit
) {

    var lobbyId: String by rememberSaveable { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        //TODO: Create Link to Lobby List

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            OutlinedTextField(
                value = lobbyId,
                onValueChange = { lobbyId = it },
                placeholder = { Text("Enter Lobby Id") },
                singleLine = true,
                shape = RoundedCornerShape(40.dp),
                modifier = Modifier
                    .width(320.dp)
                    .height(60.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = catanButtons,
                    unfocusedBorderColor = catanButtons,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black,
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { onJoinClick(lobbyId) },
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = catanButtons),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .width(320.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = "JOIN",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Text(
                text = "OR",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onBrowseClick() },
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = catanButtons),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .width(320.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = "BROWSE",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

        }

    }

}