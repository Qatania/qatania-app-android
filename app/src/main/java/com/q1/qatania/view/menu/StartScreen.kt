package com.q1.qatania.view.menu

import android.content.res.Configuration
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.q1.qatania.theme.buttons
import com.q1.qatania.theme.title

@Composable
fun StartScreen(
    onCreateGameClick: () -> Unit,
    onJoinGameClick: () -> Unit
) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "Qatania",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = title,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 72.dp)
        )


        Column(
            modifier = Modifier
                .align(if (isLandscape) Alignment.BottomCenter else Alignment.Center)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Button(
                onClick = { onCreateGameClick() },
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttons),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .width(320.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = "CREATE GAME",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "OR",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onJoinGameClick() },
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttons),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .width(320.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = "JOIN GAME",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
        }
    }

}