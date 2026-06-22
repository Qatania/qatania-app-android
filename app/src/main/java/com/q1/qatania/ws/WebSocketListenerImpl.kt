package com.q1.qatania.ws

import android.util.Log
import com.q1.qatania.model.dto.MessageDTO
import com.q1.qatania.util.jsonParser
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketListenerImpl(
    val onMessageReceived: (MessageDTO) -> Unit,
    val onError: (String) -> Unit
) : WebSocketListener() {


    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("WebSocketListener", "Established connection, response: $response")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("WebSocketListener", "Received raw message: $text")

        val messageDTO = jsonParser.decodeFromString<MessageDTO>(text)
        Log.v("WebSocketListener", "Decoded message: $messageDTO")
        onMessageReceived(messageDTO)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("WebSocketListener", "Closing: $code $reason")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("WebSocketListener", "Closed: $code $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("WebSocketListener", "Connection failure. Response: $response", t)
        onError("Connecting to server failed")
    }
}