package com.q1.qatania.view


import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.q1.qatania.model.player.PlayerModel
import com.q1.qatania.util.hexToFloat4
import com.q1.qatania.viewmodel.game.GameBoardViewModel
import com.q1.qatania.viewmodel.game.GameViewModel
import com.q1.qatania.viewmodel.lobby.LobbyViewModel
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.SceneView
import io.github.sceneview.gesture.CameraGestureDetector
import io.github.sceneview.material.setBaseColorFactor
import io.github.sceneview.math.Position
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberModelLoader

@Composable
fun GameScene(
    gameBoardViewModel: GameBoardViewModel = viewModel(),
    lobbyViewModel: LobbyViewModel = viewModel(),
    gameViewModel: GameViewModel = viewModel()
) {
    // --- GameboardViewModel ---
    val gameBoard by gameBoardViewModel.gameboardState.collectAsState(initial = null)
    val lobbyState by lobbyViewModel.lobbyState.collectAsState(initial = null)
    val playerState by lobbyViewModel.playerState.collectAsState(initial = null)

    val players: Map<String, PlayerModel> = lobbyState?.players ?: emptyMap()
    val lobbyId: String = lobbyState?.lobbyId ?: ""

    val tiles = gameBoard?.tiles
    val settlementPositions = gameBoard?.settlementPositions
    val roads = gameBoard?.roads
    // ---

    // --- 3D Model ---
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val envLoader = rememberEnvironmentLoader(engine)

    val homePos = Float3(0f, 10f, 0.1f)
    val targetPos = Float3(0f, 0f, 0f)

    var resetCounter by remember { mutableIntStateOf(0) }

    val cameraManipulator = key(resetCounter) {
        rememberCameraManipulator(
            creator = {
                CameraGestureDetector.DefaultCameraManipulator(
                    orbitHomePosition = homePos,
                    targetPosition = targetPos,
                    pinchZoomSpeed = 0.9f,
                    pinchZoomDamping = 0.7f
                )
            }
        )
    }

    val cameraNode = rememberCameraNode(engine)
    // ---

    Scaffold(
        topBar = {
            PlayerBar(
                players = players,
                onCheatAttempt = { gameViewModel.cheat(lobbyId, it) }
            )
        },
        floatingActionButton = {
            if (playerState?.isActivePlayer == true) {
                if (playerState?.isSetupRound == false && playerState?.canRollDice == true) {
                    FloatingActionButton(
                        onClick = { gameViewModel.rollDice(lobbyId) },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Casino,
                            contentDescription = "Roll dice"
                        )
                    }
                } else {
                    FloatingActionButton(
                        onClick = { gameViewModel.handleEndTurnClick(lobbyId) },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DoubleArrow,
                            contentDescription = "End turn"
                        )
                    }
                }

            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SceneView(
                modifier = Modifier.fillMaxSize(),
                engine = engine,
                cameraNode = cameraNode,
                cameraManipulator = cameraManipulator

            ) {
                Log.d(
                    "GameScene",
                    "Spawning Board"
                )
                tiles?.forEach { tile ->
                    rememberModelInstance(modelLoader, tile.type.path)?.let {
                        ModelNode(
                            modelInstance = it,
                            scaleToUnits = 1.0f,
                            autoAnimate = true,
                            position = Float3(
                                tile.coordinates[0].toFloat(),
                                0f,
                                tile.coordinates[1].toFloat()
                            )
                        )
                    }
                }

                settlementPositions?.forEach { settlementPosition ->

                    val position = Float3(
                        settlementPosition.coordinates[0].toFloat(),
                        0.05f,
                        settlementPosition.coordinates[1].toFloat()
                    )

                    if (settlementPosition.building != null) {
                        rememberModelInstance(
                            modelLoader,
                            settlementPosition.building.type.path
                        )?.let { modelInstance ->

                            modelInstance.materialInstances.forEach { materialInstance ->
                                materialInstance.setBaseColorFactor(hexToFloat4(settlementPosition.building.color))
                            }

                            ModelNode(
                                modelInstance = modelInstance,
                                scaleToUnits = 0.1f,
                                autoAnimate = true,
                                position = position,
                            )
                        }
                    } else {
                        SphereNode(
                            radius = 0.1f,
                            position = position,
                            apply = {
                                isTouchable = true
                                onSingleTapConfirmed = {
                                    gameViewModel.handleSettlementTap(lobbyId, settlementPosition)
                                    true; //-> Means tap event is consumed and should not be propagated
                                }
                            }
                        )
                    }
                }

                roads?.forEach { road ->

                    val roadPosition = Float3(
                        road.coordinates[0].toFloat(),
                        0.05f,
                        road.coordinates[1].toFloat()
                    )

                    val roadRotation = Float3(
                        0f,
                        //-60f,
                        (Math.toDegrees(road.rotationAngle).toFloat() - 90) * 2,
                        // 90 -> 0 || 270 -> 180 // ==> -90 (+90)
                        // 150 -> 120 || -30 -> -60 // ==> -30 (+150)
                        // 30 -> -120 || -150 -> -300 // ==> -150 (+30)
                        0f
                    )

                    if (road.owner != null) {
                        rememberModelInstance(
                            modelLoader,
                            "models/road.glb"
                        )?.let { modelInstance ->

                            modelInstance.materialInstances.forEach { materialInstance ->
                                materialInstance.setBaseColorFactor(hexToFloat4(road.color))
                            }

                            ModelNode(
                                modelInstance = modelInstance,
                                scaleToUnits = 0.1f,
                                autoAnimate = true,
                                position = roadPosition,
                                rotation = roadRotation
                            )
                        }
                    } else {
                        CubeNode(
                            size = Position(0.1f, 0.1f, 0.3f),
                            position = roadPosition,
                            rotation = roadRotation,
                            apply = {
                                isTouchable = true
                                onSingleTapConfirmed = {
                                    gameViewModel.handleRoadTap(lobbyId, road)
                                    true; //-> Means tap event is consumed and should not be propagated
                                }
                            }
                        )
                    }
                }
                /*LightNode(
                type = LightManager.Type.POINT,
                intensity = 100_000f,
                color = io.github.sceneview.math.Color(1f, 0.95f, 0.9f),
                direction = io.github.sceneview.math.Direction(0f, -1f, 0f),
                position = io.github.sceneview.math.Position(0f, 5f, 0f)
            )*/
            }

            // Reset Button
            Button(
                onClick = {
                    resetCounter++
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text("Reset")
            }

            ResourceBar(
                modifier = Modifier.align(Alignment.TopStart),
                player = playerState,
                onCheatAttempt = { gameViewModel.cheat(lobbyId, it) }
            )
        }
    }
}