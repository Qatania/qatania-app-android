package com.q1.qatania.model.notification

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.ui.graphics.Color
import com.q1.qatania.theme.alertSnackbar
import com.q1.qatania.theme.infoSnackbar

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
            NotificationType.INFO -> infoSnackbar
            NotificationType.ALERT -> alertSnackbar
        }
}