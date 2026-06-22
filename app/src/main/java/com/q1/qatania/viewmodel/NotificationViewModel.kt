package com.q1.qatania.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.q1.qatania.MainApplication
import com.q1.qatania.model.notification.Notification
import com.q1.qatania.model.notification.NotificationType
import com.q1.qatania.repository.NotificationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NotificationViewModel() : ViewModel() {

    val notificationRepository = NotificationRepository.getInstance()

    private val _notificationChannel = Channel<Notification>(Channel.BUFFERED)
    val notifications = _notificationChannel.receiveAsFlow()

    init {
        observeWebSocketState()
        observeNotificationRepository()
    }

    private fun observeNotificationRepository() {
        viewModelScope.launch {
            notificationRepository.notificationState.filterNotNull().collect {
                _notificationChannel.send(it)
            }
        }
    }

    private fun observeWebSocketState() {
        viewModelScope.launch {
            MainApplication.getInstance().errorState.filterNotNull().collect {
                _notificationChannel.send(Notification(it, NotificationType.ERROR))
                //Clear error state after emitting
                MainApplication.getInstance().clearErrorState()
            }

        }
    }
}