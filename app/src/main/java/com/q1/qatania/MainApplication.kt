package com.q1.qatania

import android.app.Application
import android.util.Log
import com.q1.qatania.model.dto.MessageDTO
import com.q1.qatania.repository.GameBoardRepository
import com.q1.qatania.repository.NotificationRepository
import com.q1.qatania.repository.PlayerInfoRepository
import com.q1.qatania.ws.WebSocketClient
import com.q1.qatania.ws.WebSocketListenerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MainApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private lateinit var webSocketClient: WebSocketClient
    private lateinit var webSocketListener: WebSocketListenerImpl

    private val mutableMessageFlow = MutableSharedFlow<MessageDTO?>()
    val messageFlow: SharedFlow<MessageDTO?> = mutableMessageFlow

    private val mutableErrorFlow = MutableSharedFlow<String?>()
    val errorFlow: SharedFlow<String?> = mutableErrorFlow

    companion object {
        @Volatile // Ensure visibility across threads
        private lateinit var instance: MainApplication
        fun getInstance(): MainApplication = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.v("MainApplication", "Created instance, initializing repositories")
        NotificationRepository.getInstance()
        PlayerInfoRepository.getInstance()
        GameBoardRepository.getInstance()

        Log.v("MainApplication", "Establishing web socket connection...")
        webSocketClient = WebSocketClient(BuildConfig.SERVER_URL)
        webSocketListener = WebSocketListenerImpl(
            onMessageReceived = ::handleServerMessage,
            onError = ::handleErrorMessage
        )
        webSocketClient.connect(webSocketListener)
    }

    private fun handleServerMessage(messageDTO: MessageDTO?) {
        Log.v("MainApplication", "Received latest message from WebSocket: $messageDTO")
        applicationScope.launch {
            mutableMessageFlow.emit(messageDTO)
        }
    }

    private fun handleErrorMessage(error: String?) {
        Log.e("MainApplication", "Received error message from WebSocket: $error")
        applicationScope.launch {
            mutableErrorFlow.emit(error)
        }

    }

    fun getWebSocketClient(): WebSocketClient {
        check(::webSocketClient.isInitialized) { "WebSocketClient accessed before initialization in onCreate" }
        return webSocketClient
    }

    fun shutdown() {
        webSocketClient.close()
    }

    fun clearErrorState() {
        applicationScope.launch {
            mutableErrorFlow.emit(null)
        }
    }
}