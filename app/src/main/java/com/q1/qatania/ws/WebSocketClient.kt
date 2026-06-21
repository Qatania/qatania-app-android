package com.q1.qatania.ws

import android.util.Log
import com.q1.qatania.model.dto.MessageDTO
import com.q1.qatania.util.jsonParser
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

open class WebSocketClient(private val serverUrl: String) {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()
    private var webSocket: WebSocket? = null
    open fun connect(listener: WebSocketListener) {
        Log.d("WebSocketClient", "Connecting to $serverUrl")
        val request = Request.Builder().url(serverUrl).build()
        webSocket = client.newWebSocket(request, listener)
    }


    fun isConnected(): Boolean {
        return webSocket != null
    }

    fun sendMessage(messageDTO: MessageDTO): Boolean {
        val socket = webSocket // Capture current socket instance
        if (socket == null) {
            Log.e("WebSocketClient", "Cannot send message, WebSocket is null.")
            return false
        }
        return try {
            val jsonMessage = jsonParser.encodeToString(messageDTO)
            Log.d("WebSocketClient", "Sending: $jsonMessage")
            socket.send(jsonMessage)
        } catch (e: Exception) {
            Log.e("WebSocketClient", "Error encoding/sending message", e)
            false
        }
    }

    fun close() {
        webSocket?.close(1000, "Client closed connection")
        webSocket = null
    }
}