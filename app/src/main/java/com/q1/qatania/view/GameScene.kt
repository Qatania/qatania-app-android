package com.q1.qatania.view


import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.filament.Colors
import com.q1.qatania.dataRepository.GameBoardRepository
import com.q1.qatania.util.hexToFloat4
import com.q1.qatania.viewmodel.GameBoardViewModel
import com.q1.qatania.viewmodel.GameBoardViewModelFactory
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Float4
import io.github.sceneview.SceneView
import io.github.sceneview.gesture.CameraGestureDetector
import io.github.sceneview.material.setBaseColorFactor
import io.github.sceneview.material.setColor
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberModelLoader

@Composable
fun GameScene() {
    val context = LocalContext.current
    val repository = remember {
        GameBoardRepository(context.applicationContext)
    }
    val gameBoardViewModel: GameBoardViewModel =
        viewModel(factory = GameBoardViewModelFactory(repository))
    val currentGameBoard = gameBoardViewModel.boardFlow.collectAsState(
        initial = null
    )

    val gameBoard = currentGameBoard.value
    val tiles = gameBoard?.tiles
    val settlementPositions = gameBoard?.settlementPositions
    val roads = gameBoard?.roads


    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val envLoader = rememberEnvironmentLoader(engine)

    val homePos = Float3(0f, 10f, 0.1f)
    val targetPos = Float3(0f, 0f, 0f)

    val cameraManipulator = rememberCameraManipulator(
        creator = {
            CameraGestureDetector.DefaultCameraManipulator(
                orbitHomePosition = homePos,
                targetPosition = targetPos,
                pinchZoomSpeed = 0.9f,
                pinchZoomDamping = 0.7f
            )
        }
    )

    val cameraNode = rememberCameraNode(engine)

    SceneView(
        modifier = Modifier.fillMaxSize(),
        engine = engine,
        cameraNode = cameraNode,
        cameraManipulator = cameraManipulator

    ) {

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
            Log.d(
                "GameScene",
                "Spawning TileNode for tile: ${tile.id}, ${tile.type}, ${tile.coordinates}"
            )
        }

        settlementPositions?.forEach { settlementPosition ->
            if (settlementPosition.building != null) {
                rememberModelInstance(modelLoader, settlementPosition.building.type.path)?.let { modelInstance ->

                    modelInstance.materialInstances.forEach { materialInstance ->
                        materialInstance.setBaseColorFactor(hexToFloat4(settlementPosition.building.color))
                    }

                    ModelNode(
                        modelInstance = modelInstance,
                        scaleToUnits = 0.1f,
                        autoAnimate = true,
                        position = Float3(
                            settlementPosition.coordinates[0].toFloat(),
                            0.05f,
                            settlementPosition.coordinates[1].toFloat()
                        ),
                    )
                }
            }
        }

        roads?.forEach { road ->
            if (road.owner != null){
                rememberModelInstance(modelLoader, "models/road.glb")?.let { modelInstance ->

                    modelInstance.materialInstances.forEach { materialInstance ->
                        materialInstance.setBaseColorFactor(hexToFloat4(road.color))
                    }

                    ModelNode(
                        modelInstance = modelInstance,
                        scaleToUnits = 0.1f,
                        autoAnimate = true,
                        position = Float3(
                            road.coordinates[0].toFloat(),
                            0.05f,
                            road.coordinates[1].toFloat()
                        ),
                        rotation = Float3(
                            0f,
                            //-60f,
                            (Math.toDegrees(road.rotationAngle).toFloat()-90) * 2,
                                // 90 -> 0 || 270 -> 180 // ==> -90 (+90)
                                // 150 -> 120 || -30 -> -60 // ==> -30 (+150)
                                // 30 -> -120 || -150 -> -300 // ==> -150 (+30)
                            0f
                        )
                    )
                }
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
}