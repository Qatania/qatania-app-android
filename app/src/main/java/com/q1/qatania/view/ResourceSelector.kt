package com.q1.qatania.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.q1.qatania.model.gameboard.TileType
import com.q1.qatania.util.getResourceIcon

@Composable
fun ResourceSelector(
    resource: TileType,
    count: Int,
    current: Int?,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onDecrement, enabled = count > 0) {
            Icon(
                Icons.Default.RemoveCircle,
                "Decrement",
                tint = if (count > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.38f
                )
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painterResource(id = getResourceIcon(resource)),
                contentDescription = resource.name,
                Modifier.size(24.dp)
            )
            Text(
                if (current != null) "$count ($current)" else "$count",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(60.dp)
            )
        }

        IconButton(onClick = onIncrement) {
            Icon(Icons.Default.AddCircle, "Increment")
        }
    }
}