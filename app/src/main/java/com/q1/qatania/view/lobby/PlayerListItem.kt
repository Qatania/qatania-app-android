package com.q1.qatania.view.lobby

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.q1.qatania.R
import com.q1.qatania.model.player.PlayerModel
import com.q1.qatania.theme.listItemBackground
import com.q1.qatania.theme.notReadyState
import com.q1.qatania.theme.success

@Composable
fun PlayerListItem(
    player: PlayerModel,
    isPlayer: Boolean,
    onToggleReadyClick: () -> Unit,
    onChangeUsername: (username: String) -> Unit,
) {

    var showUsernameDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(listItemBackground)
            .border(1.dp, Color.Black)
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = R.drawable.player_icon128),
            contentDescription = "player icon",
            modifier = Modifier
                .size(48.dp)
                .border(4.dp, Color(player.color.toColorInt()), RectangleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        val usernameText = (if (isPlayer) "(YOU) " else "") + player.username!!
        Text(
            text = usernameText,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.5f)
        )

        if (isPlayer) {
            IconButton(
                onClick = { showUsernameDialog = true },
                modifier = Modifier
                    .size(36.dp)
                    .border(BorderStroke(1.dp, Color.Black), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Change username",
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = onToggleReadyClick,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = if (player.isReady) success else success.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
                    .border(BorderStroke(1.dp, Color.Black), CircleShape)
            ) {
                Icon(
                    imageVector = if (player.isReady) Icons.Filled.Check else Icons.Outlined.Check,
                    contentDescription = if (player.isReady) "Ready" else "Not ready",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        } else {
            Icon(
                imageVector = if (player.isReady) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                contentDescription = if (player.isReady) "Ready" else "Not ready",
                tint = if (player.isReady) success else notReadyState,
                modifier = Modifier.size(32.dp)
            )
        }
    }

    if (showUsernameDialog) {
        UsernameInputDialog(
            onDismissRequest = {
                showUsernameDialog = false
            },
            onConfirm = { newUsername ->
                onChangeUsername(newUsername)
                showUsernameDialog = false
            },
            onCancel = {
                showUsernameDialog = false
            },
            username = player.username!!
        )
    }
}