package com.q1.qatania.model.notification

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.ui.graphics.Color
import com.q1.qatania.theme.darkBlue
import com.q1.qatania.theme.darkOrange

class ColoredSnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    override val withDismissAction: Boolean = false,
    val type: NotificationType
) : SnackbarVisuals {

    val color: Color
        get() = when (type) {
            NotificationType.SUCCESS -> Color.Green
            NotificationType.ERROR -> Color.Red
            NotificationType.INFO -> darkBlue
            NotificationType.ALERT -> darkOrange
        }
}