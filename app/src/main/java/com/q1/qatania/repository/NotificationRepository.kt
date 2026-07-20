package com.q1.qatania.repository

import com.q1.qatania.model.dto.MessageDTO
import com.q1.qatania.model.dto.MessageType
import com.q1.qatania.model.notification.Notification
import com.q1.qatania.model.notification.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationRepository : AbstractRepository() {

    private val _notificationFlow = MutableStateFlow<Notification?>(null);
    val notificationState = _notificationFlow.asStateFlow()

    companion object {
        @Volatile
        private var INSTANCE: NotificationRepository? = null
        fun getInstance(): NotificationRepository =
            //Ensure thread safety
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: NotificationRepository().also { INSTANCE = it }
            }
    }


    override fun handleMessage(messageDTO: MessageDTO) {
        when (messageDTO.type) {
            MessageType.NEXT_TURN -> handleNextTurnMessage(messageDTO)
            MessageType.ERROR -> handleErrorMessage(messageDTO)
            MessageType.ALERT -> handleAlertMessage(messageDTO)
            else -> {}
        }
    }

    private fun handleNextTurnMessage(messageDTO: MessageDTO) {
        val players = messageDTO.players ?: emptyMap();
        val nextPlayer = players.values.filter({ it.isActivePlayer })
        if (nextPlayer.isNotEmpty()) {
            val username = nextPlayer[0].username;
            val text = "It's player $username's turn"
            _notificationFlow.value = Notification(text, NotificationType.INFO)
        }
    }

    private fun handleAlertMessage(messageDTO: MessageDTO) {
        val message = messageDTO.message
        if (message != null && message.containsKey("message") && message.containsKey("severity")) {
            val text = message["message"]?.toString()
            val severity = message["severity"]?.toString()
            val type: NotificationType = when (severity) {
                "success" -> NotificationType.SUCCESS
                "error" -> NotificationType.ERROR
                else -> NotificationType.INFO
            }
            _notificationFlow.value = Notification("$text", type)
        }
    }

    private fun handleErrorMessage(messageDTO: MessageDTO) {
        val message = messageDTO.message
        if (message != null && message.containsKey("error")) {
            val text = message["error"]?.toString()
            _notificationFlow.value = Notification("$text", NotificationType.ERROR)
        }
    }

    fun clearNotificationState() {
        _notificationFlow.value = null
    }
}