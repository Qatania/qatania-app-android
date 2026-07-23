package com.q1.qatania

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.q1.qatania.model.navigation.NavigationEvent
import com.q1.qatania.model.notification.ColoredSnackbarVisuals
import com.q1.qatania.view.GameScene
import com.q1.qatania.view.menu.JoinGameScreen
import com.q1.qatania.view.lobby.LobbyScreen
import com.q1.qatania.view.menu.LobbyBrowserScreen
import com.q1.qatania.view.menu.StartScreen
import com.q1.qatania.viewmodel.MenuViewModel
import com.q1.qatania.viewmodel.NotificationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }
            val menuViewModel: MenuViewModel = viewModel()
            val notificationViewModel: NotificationViewModel = viewModel()

            val lobbies by menuViewModel.lobbiesState.collectAsState(initial = emptyList())

            //Collect navigation events
            var lastNavigatedRoute by remember { mutableStateOf<String?>(null) }
            LaunchedEffect("navigation") {
                menuViewModel.navigationEvents.collect { event ->
                    val route: String = when (event) {
                        is NavigationEvent.ToStartScreen -> "main"
                        is NavigationEvent.ToLobbyBrowseScreen -> "lobbies"
                        is NavigationEvent.ToLobbyScreen -> "lobby/${event.lobbyId}"
                        is NavigationEvent.ToJoinGameScreen -> "join"
                        is NavigationEvent.ToGameScreen -> "game/${event.lobbyId}"
                    }

                    if (route == lastNavigatedRoute) {
                        Log.v("MainActivity", "Received duplicated navigation event, skipping")
                        return@collect
                    }

                    lastNavigatedRoute = route
                    Log.v("MainActivity", "Navigating to $route")
                    navController.navigate(route)
                }

            }

            //Collect notifications
            LaunchedEffect("notification") {
                notificationViewModel.notifications.collect { notification ->
                    snackbarHostState.showSnackbar(
                        ColoredSnackbarVisuals(
                            message = notification.message,
                            type = notification.type,
                            withDismissAction = true,
                        )
                    )
                }
            }


            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) {
                        val snackbarVisuals = (it.visuals as? ColoredSnackbarVisuals)
                        Snackbar(
                            snackbarData = it,
                            containerColor = snackbarVisuals?.color ?: Color.Gray,
                            contentColor = Color.White,
                            actionColor = Color.White
                        )
                    }
                },
            ) { innerPadding ->


                Box(modifier = Modifier.padding(innerPadding)) {
                    NavHost(
                        navController = navController,
                        startDestination = "main"
                    ) {

                        composable("main") {
                            StartScreen(
                                onCreateGameClick = { menuViewModel.createLobby() },
                                onJoinGameClick = { menuViewModel.navigateToJoinScreen() }
                            )
                        }

                        //Join Game screen
                        composable("join") {
                            JoinGameScreen(
                                onJoinClick = { lobbyId ->
                                    menuViewModel.joinLobby(lobbyId)
                                },
                                onBrowseClick = {
                                    menuViewModel.navigateToLobbyBrowser()
                                }
                            )
                        }

                        composable("lobbies") {
                            LobbyBrowserScreen(
                                lobbies = lobbies ?: emptyList(),
                                onRefreshClick = { menuViewModel.getLobbies() },
                                onJoinLobbyClick = { menuViewModel.joinLobby(it) }
                            )
                        }

                        //Lobby Screen
                        composable(
                            route = "lobby/{lobbyId}",
                            arguments = listOf(
                                navArgument("lobbyId") { type = NavType.StringType },
                            )
                        ) { backStackEntry ->
                            LobbyScreen()
                        }

                        //Game Scene
                        composable(
                            route = "game/{lobbyId}",
                            arguments = listOf(
                                navArgument("lobbyId") { type = NavType.StringType },
                            )
                        ) { backStackEntry ->
                            GameScene(onReturnToMenu = { menuViewModel.returnToMenu() })
                        }
                    }
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
