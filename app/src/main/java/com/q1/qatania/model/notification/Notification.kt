package com.q1.qatania.model.notification

import kotlin.time.Clock

data class Notification(
    val message: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis()
)
