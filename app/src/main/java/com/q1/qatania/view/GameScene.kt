package com.q1.qatania.view


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.filament.LightManager
import com.q1.qatania.dataRepository.GameBoardRepository
import com.q1.qatania.model.Tile
import com.q1.qatania.viewmodel.GameBoardViewModel
import com.q1.qatania.viewmodel.GameBoardViewModelFactory
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.SceneView
import io.github.sceneview.createDefaultCameraManipulator
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
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


    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val envLoader = rememberEnvironmentLoader(engine)

    SceneView(
        modifier = Modifier.fillMaxSize(),
        engine = engine,
        cameraNode = rememberCameraNode(engine),
        cameraManipulator = rememberCameraManipulator(
            orbitHomePosition = Float3(0f, 10f, 0.1f),
            targetPosition = Float3(0f, 0f, 0f)
        )

    ) {


        tiles?.forEach { tile ->
            /*
            TileNode(tile = tile, modelLoader = modelLoader)
            */
            rememberModelInstance(modelLoader, tile.type.path)?.let {
                ModelNode(modelInstance = it,
                    scaleToUnits = 1.0f,
                    autoAnimate = true, position = Float3(
                    tile.coordinates[0].toFloat(),
                    0f,
                    tile.coordinates[1].toFloat()
                )
                )
            }

            android.util.Log.d(
                "GameScene",
                "Spawning TileNode for tile: ${tile.id}, ${tile.type}, ${tile.coordinates}"
            )

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


@Composable
fun TileNode(tile: Tile, modelLoader: ModelLoader) {
    rememberModelInstance(modelLoader, tile.type.path)?.let {
        ModelNode(modelInstance = it, scaleToUnits = 0.5f, autoAnimate = true)
    }
    android.util.Log.d(
        "GameScene",
        "Spawning TileNode for tile: ${tile.id}, path should be: ${tile.type.path}"
    )
}


