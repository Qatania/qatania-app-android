package com.q1.qatania

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.q1.qatania.view.GameScene

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //(application as MainApplication).getWebSocketClient().sendMessage(MessageDTO(MessageType.CREATE_LOBBY))

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    /*SceneView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        rememberModelInstance(modelLoader, "models/mesa.glb")?.let {
                            ModelNode(modelInstance = it, scaleToUnits = 0.5f, autoAnimate = true)
                        }
                    }*/
                    GameScene()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val mainApplication = MainApplication.getInstance();
        mainApplication.onDestroy()
    }
}
