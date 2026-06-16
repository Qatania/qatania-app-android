package com.q1.qatania

import android.app.Application
import android.util.Log
import com.q1.qatania.model.messageDTO.MessageDTO
import com.q1.qatania.ws.WebSocketClient
import com.q1.qatania.ws.WebSocketListenerImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainApplication : Application() {

    private lateinit var webSocketClient: WebSocketClient
    private lateinit var webSocketListener: WebSocketListenerImpl

    private val mutableMessageState = MutableStateFlow<MessageDTO?>(null)
    val messageState = mutableMessageState.asStateFlow()

    companion object {
        @Volatile // Ensure visibility across threads
        private lateinit var instance: MainApplication
        fun getInstance(): MainApplication = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.v("MainApplication", "Created instance")
        Log.v("MainApplication", "Establishing web socket connection...")
        webSocketClient = WebSocketClient(BuildConfig.SERVER_URL)
        webSocketListener = WebSocketListenerImpl { handleServerMessage(it) }
        webSocketClient.connect(webSocketListener)
    }

    private fun handleServerMessage(messageDTO: MessageDTO?) {
        Log.v("MainApplication", "Received latest message from WebSocket: $messageDTO")
        mutableMessageState.update { messageDTO }
    }

    fun getWebSocketClient(): WebSocketClient {
        check(::webSocketClient.isInitialized) { "WebSocketClient accessed before initialization in onCreate" }
        return webSocketClient
    }

    fun onDestroy() {
        webSocketClient.close()
    }
}