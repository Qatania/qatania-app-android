package com.q1.qatania.view.game


import android.content.res.Configuration
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.LabelOff
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.filament.ColorGrading
import com.google.android.filament.Skybox
import com.google.android.filament.ToneMapper
import com.q1.qatania.model.gameboard.Port
import com.q1.qatania.model.gameboard.PortTransform
import com.q1.qatania.model.gameboard.PortVisuals
import com.q1.qatania.model.gameboard.Road
import com.q1.qatania.model.gameboard.Robber
import com.q1.qatania.model.gameboard.SettlementPosition
import com.q1.qatania.model.gameboard.Tile
import com.q1.qatania.model.gameboard.TileType
import com.q1.qatania.model.player.PlayerModel
import com.q1.qatania.repository.GameRepository
import com.q1.qatania.theme.buttons
import com.q1.qatania.theme.gameBackground
import com.q1.qatania.util.ShakeDetector
import com.q1.qatania.util.hexToFloat4
import com.q1.qatania.viewmodel.game.GameBoardViewModel
import com.q1.qatania.viewmodel.game.GameViewModel
import com.q1.qatania.viewmodel.lobby.LobbyViewModel
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.SceneScope
import io.github.sceneview.SceneView
import io.github.sceneview.createEnvironment
import io.github.sceneview.gesture.CameraGestureDetector
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.material.setBaseColorFactor
import io.github.sceneview.math.Direction
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.colorOf
import io.github.sceneview.math.toLinearSpace
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.SphereNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberView
import kotlinx.coroutines.delay

private const val LABEL_HOVER_HEIGHT = 0.75f

private const val LABEL_WIDTH = 0.5f
private const val LABEL_ASPECT_RATIO = 4f

@Composable
fun GameScene(
    gameBoardViewModel: GameBoardViewModel = viewModel(),
    lobbyViewModel: LobbyViewModel = viewModel(),
    gameViewModel: GameViewModel = viewModel(),
    onReturnToMenu: () -> Unit = {}
) {
    // --- GameboardViewModel ---
    val gameBoard by gameBoardViewModel.gameboardState.collectAsState(initial = null)
    val lobbyState by lobbyViewModel.lobbyState.collectAsState(initial = null)
    val playerState by lobbyViewModel.playerState.collectAsState(initial = null)

    val players: Map<String, PlayerModel> = lobbyState?.players ?: emptyMap()
    val lobbyId: String = lobbyState?.lobbyId ?: ""
    val self: String = gameViewModel.self

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val buildModeYOffset = 0.5f
    val tiles = gameBoard?.tiles
    val settlementPositions = gameBoard?.settlementPositions
    val roads = gameBoard?.roads
    val ports = gameBoard?.ports

    val diceState by gameViewModel.diceState.collectAsState()
    var showDicePopup by remember { mutableStateOf(false) }

    var showBankTradePopup by remember { mutableStateOf(false) }
    var showRobberPopup by remember { mutableStateOf(false) }

    val gameEndState by gameViewModel.gameEndState.collectAsState()

    fun playerCanPlaceRobber(diceState: GameRepository.DiceState): Boolean {
        val total = diceState.dice1 + diceState.dice2
        return total == 7 && playerState?.needsToMoveRobber == true
    }

    LaunchedEffect(diceState) {
        if (diceState != null) {
            showDicePopup = true
            delay(3000)
            showDicePopup = false
            showRobberPopup = playerCanPlaceRobber(diceState!!)
            gameViewModel.clearDiceState()
        }
    }
    // ---

    // --- 3D Model ---
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val envLoader = rememberEnvironmentLoader(engine)

    //Need so that the skybox matches the background of top and bottom bar
    val view = rememberView(engine)
    DisposableEffect(view) {
        val colorGrading = ColorGrading.Builder()
            .toneMapper(ToneMapper.Linear())
            .build(engine)
        view.colorGrading = colorGrading
        view.bloomOptions = view.bloomOptions.apply { enabled = false }
        onDispose { engine.destroyColorGrading(colorGrading) }
    }

    val environment = rememberEnvironment(envLoader, isOpaque = true, key = gameBackground) {
        createEnvironment(envLoader, isOpaque = true).copy(
            skybox = Skybox.Builder()
                .color(colorOf(gameBackground).toLinearSpace().toFloatArray())
                .build(engine)
        )
    }

    val homePos = Float3(0f, 10f, 0.1f)
    val targetPos = Float3(0f, 0f, 0f)

    var resetCounter by remember { mutableIntStateOf(0) }
    var buildModeActivated: Boolean by rememberSaveable { mutableStateOf(false) };
    var numbersVisible: Boolean by rememberSaveable { mutableStateOf(true) }

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
            Box(modifier = Modifier.background(gameBackground)) {
                PlayerBar(
                    players = players,
                    self = self,
                    onCheatAttempt = { gameViewModel.cheat(lobbyId, it) },
                    onReport = { gameViewModel.report(lobbyId, it) }
                )
            }
        },
        bottomBar = {
            ResourceBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gameBackground),
                player = playerState,
                onCheatAttempt = { gameViewModel.cheat(lobbyId, it) }
            )
        },
        floatingActionButton = {
            if (playerState?.isActivePlayer == true) {
                if (playerState?.isSetupRound == false && playerState?.canRollDice == true) {
                    FloatingActionButton(
                        onClick = { gameViewModel.rollDice(lobbyId) },
                        containerColor = buttons,
                        contentColor = Color.White,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Casino,
                            contentDescription = "Roll dice"
                        )
                    }

                    ShakeDetector {
                        if (!showDicePopup) {
                            gameViewModel.rollDice(lobbyId)
                        }
                    }

                } else {
                    FloatingActionButton(
                        onClick = {
                            buildModeActivated = false
                            gameViewModel.handleEndTurnClick(lobbyId)
                        },
                        containerColor = buttons,
                        contentColor = Color.White
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
                .background(gameBackground)
        ) {
            SceneView(
                modifier = Modifier.fillMaxSize(),
                engine = engine,
                view = view,
                environment = environment,
                cameraNode = cameraNode,
                cameraManipulator = cameraManipulator,
                isOpaque = true,
                autoCenterContent = false //prevent re-centering and therefore re-creation of the gameboard

            ) {
                Log.d(
                    "GameScene",
                    "Spawning Board"
                )


                tiles?.forEach { tile ->
                    key(tile.id) {
                        TileNode(
                            tile = tile,
                            modelLoader = modelLoader,
                            onTileClick = {
                                gameViewModel.onTileClick(
                                    lobbyId,
                                    tile.id
                                )
                            } // Pass the handler
                        )
                    }
                }

                BoardLabelsNode(
                    tiles = tiles,
                    ports = ports,
                    isVisible = numbersVisible,
                    cameraPositionProvider = { cameraNode.worldPosition }
                )

                gameBoard?.robber?.let { robber ->
                    RobberNode(robber, modelLoader)
                }

                settlementPositions?.forEach { settlementPosition ->
                    key(settlementPosition.id) {
                        SettlementPositionNode(
                            settlementPosition = settlementPosition,
                            modelLoader = modelLoader,
                            buildModeActivated = buildModeActivated,
                            buildModeYOffset = buildModeYOffset,
                            lobbyId = lobbyId,
                            gameViewModel = gameViewModel
                        )
                    }
                }

                roads?.forEach { road ->
                    key(road.id) {
                        RoadNode(
                            road = road,
                            modelLoader = modelLoader,
                            buildModeActivated = buildModeActivated,
                            buildModeYOffset = buildModeYOffset,
                            lobbyId = lobbyId,
                            gameViewModel = gameViewModel
                        )
                    }
                }

                ports?.forEach { port ->

                    val portVisuals: PortVisuals = port.portVisuals
                    val port1Position = Float3(
                        portVisuals.buildingSite1Position[0].toFloat(), 0.05f,
                        portVisuals.buildingSite1Position[1].toFloat()
                    )
                    val port2Position = Float3(
                        portVisuals.buildingSite2Position[0].toFloat(), 0.05f,
                        portVisuals.buildingSite2Position[1].toFloat()
                    )

                    val portTransform: PortTransform = portVisuals.portTransform

                    PortNode(
                        shorePosition = port1Position,
                        portTransform = portTransform,
                        modelLoader = modelLoader
                    )
                    PortNode(
                        shorePosition = port2Position,
                        portTransform = portTransform,
                        modelLoader = modelLoader
                    )
                    PortNode(portTransform = portTransform)
                }
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(if (isLandscape) Alignment.TopStart else Alignment.TopEnd)
                    .zIndex(5.0f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Reset Button
                Button(
                    onClick = {
                        resetCounter++
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = buttons),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Cameraswitch,
                        contentDescription = "Reset Board zoom & tilt"
                    )
                }

                // Toggle Numbers Button
                Button(
                    onClick = {
                        numbersVisible = !numbersVisible
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = buttons),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Icon(
                        imageVector = if (numbersVisible) Icons.AutoMirrored.Filled.Label else Icons.AutoMirrored.Filled.LabelOff,
                        contentDescription = "Toggle Numbers"
                    )
                }

                if (playerState?.isActivePlayer == true) {
                    // Toggle Build Mode Button
                    Button(
                        onClick = {
                            buildModeActivated = !buildModeActivated
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = buttons),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {

                        Icon(
                            imageVector = if (buildModeActivated) Icons.Filled.Build else Icons.Outlined.Build,
                            contentDescription = "Toggle Build Mode"
                        )
                    }

                    Button(
                        onClick = {
                            showBankTradePopup = true
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = buttons),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SwapHoriz,
                            contentDescription = "Trade Resources"
                        )
                    }
                }
            }
        }

    }

    if (showDicePopup && diceState != null) {
        DiceResultPopup(
            diceState = diceState!!,
            onDismiss = {
                showDicePopup = false;
                showRobberPopup = playerCanPlaceRobber(diceState!!)
                gameViewModel.clearDiceState()
            }
        )
    }

    if (!showDicePopup && showRobberPopup) {
        RobberPopup(
            onDismiss = { showRobberPopup = false }
        )
    }

    if (showBankTradePopup) {
        BankTrade(
            player = players[self],
            onSubmit = { tradeOffer ->
                gameViewModel.submitBankTrade(
                    tradeOffer,
                    lobbyId
                );
                showBankTradePopup = false
            },
            onCancel = { showBankTradePopup = false }
        )
    }

    if (gameEndState != null) {
        GameEndScreen(
            selfId = self,
            gameEndState = gameEndState!!,
            onReturnToMenu = {
                gameViewModel.clearGameEndState()
                onReturnToMenu()
            }
        )
    }
}

@Composable
private fun SceneScope.RoadNode(
    road: Road,
    modelLoader: ModelLoader,
    buildModeActivated: Boolean,
    buildModeYOffset: Float,
    lobbyId: String,
    gameViewModel: GameViewModel
) {
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
                scaleToUnits = 0.4f,
                autoAnimate = true,
                position = roadPosition,
                rotation = roadRotation
            )
        }
    }

    //Keeps the reference to the CubeNode below so that its
    //attributes can be updated later on in SideEffect
    val nodeRef = remember { NodeRef<CubeNode>() }
    val color: String = road.color ?: "#FFFFFF"

    CubeNode(
        size = Position(0.1f, 0.1f, 0.4f),
        position = roadPosition.copy(y = buildModeYOffset),
        rotation = roadRotation,
        materialInstance = remember(color) {
            materialLoader.createColorInstance(hexToFloat4(color))
        },
        apply = {
            nodeRef.node = this
            isTouchable = true
            isHittable = true
            onSingleTapConfirmed = {
                Log.d(
                    "GameScene",
                    "Detected Tab on road ${road.id} with buildMode = $buildModeActivated"
                )
                gameViewModel.handleRoadTap(lobbyId, road)
                true; //-> Means tap event is consumed and should not be propagated
            }
        }
    )

    //Needed to update visibility when buildMode is toggled
    SideEffect {
        nodeRef.node?.let {
            it.isVisible = buildModeActivated
            it.isHittable = buildModeActivated
        }
    }

}

@Composable
private fun SceneScope.SettlementPositionNode(
    settlementPosition: SettlementPosition,
    modelLoader: ModelLoader,
    buildModeActivated: Boolean,
    buildModeYOffset: Float,
    lobbyId: String,
    gameViewModel: GameViewModel
) {
    val position = Float3(
        settlementPosition.coordinates[0].toFloat(),
        0.05f,
        settlementPosition.coordinates[1].toFloat()
    )

    val building = settlementPosition.building;

    if (building != null) {
        //So that the upgraded settlement to city gets rendered accordingly
        key(building.type) {
            rememberModelInstance(
                modelLoader,
                building.type.path
            )?.let { modelInstance ->

                modelInstance.materialInstances.forEach { materialInstance ->
                    materialInstance.setBaseColorFactor(
                        hexToFloat4(building.color)
                    )
                }

                ModelNode(
                    modelInstance = modelInstance,
                    scaleToUnits = 0.3f,
                    autoAnimate = true,
                    position = position,
                )
            }
        }
    }


    //Keeps the reference to the SphereNode below so that its
    //attributes can be updated later on in SideEffect
    val nodeRef = remember { NodeRef<SphereNode>() }

    val settlementAction: (MotionEvent) -> Boolean = {
        gameViewModel.handleSettlementTap(
            lobbyId,
            settlementPosition
        )
        true; //-> Means tap event is consumed and should not be propagated
    }

    val color: String = building?.color ?: "#FFFFFF"

    SphereNode(
        radius = 0.1f,
        position = position.copy(y = buildModeYOffset),
        materialInstance = remember(color) {
            materialLoader.createColorInstance(hexToFloat4(color))
        },
        apply = {
            nodeRef.node = this
            isTouchable = true
            isHittable = true
            onSingleTapConfirmed = settlementAction
        }
    )

    //Needed to update visibility when buildMode is toggled
    SideEffect {
        nodeRef.node?.let {
            it.isVisible = buildModeActivated
            it.isHittable = buildModeActivated
            //Setting settlement action again here so that the closure gets the correct state
            it.onSingleTapConfirmed = settlementAction
        }
    }

}

@Composable
private fun SceneScope.TileNode(
    tile: Tile,
    modelLoader: ModelLoader,
    onTileClick: () -> Unit
) {
    val tilePosition = Float3(
        tile.coordinates[0].toFloat(),
        0f,
        tile.coordinates[1].toFloat()
    )

    rememberModelInstance(modelLoader, tile.type.path)?.let {
        //Forest asset is too small, so size is correctly
        val scale: Float = if (tile.type == TileType.WOOD) 1.5f else 1.0f

        ModelNode(
            modelInstance = it,
            scaleToUnits = scale,
            autoAnimate = true,
            position = tilePosition,
            apply = {
                // Make the tile clickable
                isTouchable = true
                isHittable = true
                onSingleTapConfirmed = {
                    onTileClick()
                    true
                }
            }
        )
    }

}

@Composable
private fun SceneScope.BoardLabelsNode(
    tiles: List<Tile>?,
    ports: List<Port>?,
    isVisible: Boolean,
    cameraPositionProvider: () -> Position
) {
    Node(isVisible = isVisible) {
        tiles?.forEach { tile ->
            key(tile.id) {
                TileNumberLabel(
                    tile = tile,
                    cameraPositionProvider = cameraPositionProvider
                )
            }
        }

        ports?.forEachIndexed { index, port ->
            key(index) {
                PortRateLabel(
                    port = port,
                    cameraPositionProvider = cameraPositionProvider
                )
            }
        }
    }
}

@Composable
private fun SceneScope.TileNumberLabel(
    tile: Tile,
    cameraPositionProvider: () -> Position
) {
    if (tile.value == 0) return

    val numberColor = if (tile.value == 6 || tile.value == 8) {
        Color.Red.toArgb()
    } else {
        Color.White.toArgb()
    }

    TextNode(
        text = tile.value.toString(),
        fontSize = 96f,
        textColor = numberColor,
        widthMeters = LABEL_WIDTH,
        heightMeters = LABEL_WIDTH / LABEL_ASPECT_RATIO,
        position = Float3(
            tile.coordinates[0].toFloat(),
            LABEL_HOVER_HEIGHT,
            tile.coordinates[1].toFloat()
        ),
        cameraPositionProvider = cameraPositionProvider,
    )
}

@Composable
private fun SceneScope.PortRateLabel(
    port: Port,
    cameraPositionProvider: () -> Position
) {
    val resource = port.resource?.name ?: "?"
    val portTransform = port.portVisuals.portTransform

    TextNode(
        text = "$resource (${port.inputResourceAmount}:1)",
        fontSize = 56f,
        widthMeters = LABEL_WIDTH,
        heightMeters = LABEL_WIDTH / LABEL_ASPECT_RATIO,
        position = Float3(
            portTransform.x.toFloat(),
            LABEL_HOVER_HEIGHT,
            portTransform.y.toFloat()
        ),
        cameraPositionProvider = cameraPositionProvider
    )
}

@Composable
private fun SceneScope.PortNode(
    portTransform: PortTransform
) {
    val markerHeight = 0.3f

    CylinderNode(
        radius = 0.15f,
        height = markerHeight,
        materialInstance = materialLoader.createColorInstance(Color(0xFF6B4A2F)),
        position = Float3(
            portTransform.x.toFloat(),
            markerHeight / 2 - 0.05f, // move up to 0 and then back down to be "flush" with the tiles
            portTransform.y.toFloat()
        )
    )
}

@Composable
private fun SceneScope.PortNode(
    shorePosition: Float3,
    portTransform: PortTransform,
    modelLoader: ModelLoader,
) {

    val dx = portTransform.x.toFloat() - shorePosition.x
    val dz = portTransform.y.toFloat() - shorePosition.z

    rememberModelInstance(
        modelLoader,
        "models/port.glb"
    )?.let { modelInstance ->

        val nodeRef = remember { NodeRef<ModelNode>() }

        ModelNode(
            modelInstance = modelInstance,
            scaleToUnits = 0.3f,
            autoAnimate = true,
            position = shorePosition,
            apply = {
                nodeRef.node = this
            }
        )

        SideEffect {
            nodeRef.node?.lookTowards(lookDirection = Direction(dx, shorePosition.y, dz))
        }
    }
}

@Composable
private fun SceneScope.RobberNode(
    robber: Robber,
    modelLoader: ModelLoader
) {
    if (robber.coordinates.size < 2) return;

    val robberPosition = Float3(
        robber.coordinates[0].toFloat(),
        0.3f, // Make it be ontop of everything.
        robber.coordinates[1].toFloat()
    )
    rememberModelInstance(modelLoader, "models/robber.glb")?.let { modelInstance ->

        ModelNode(
            modelInstance = modelInstance,
            scaleToUnits = 0.5f,
            autoAnimate = true,
            position = robberPosition,
            rotation = Rotation(x = 0f, y = 0f, z = 0f)
        )
    }
}


private class NodeRef<T> {
    var node: T? = null
}