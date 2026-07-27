package com.q1.qatania.repository

import com.q1.qatania.MainApplication
import com.q1.qatania.model.dto.MessageDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class AbstractRepository {

    private val scope = CoroutineScope(SupervisorJob())

    init {
        /*
        Subscribe to the message state in MainApplication, which gets updated with each message
        received by the websocket. The handleMessage method must be implemented by each repository
        independently
        */
        scope.launch {
            val mainApplication = MainApplication.getInstance()

            mainApplication.messageFlow
                .filterNotNull()
                .onEach {
                    handleMessage(it)
                }
                .launchIn(this)
        }
    }


    abstract fun handleMessage(messageDTO: MessageDTO)

}