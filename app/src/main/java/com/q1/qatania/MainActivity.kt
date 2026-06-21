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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.q1.qatania.model.navigation.NavigationEvent
import com.q1.qatania.view.GameScene
import com.q1.qatania.view.JoinGameScreen
import com.q1.qatania.view.lobby.LobbyScreen
import com.q1.qatania.viewmodel.MenuViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                val navController = rememberNavController()
                val menuViewModel: MenuViewModel = viewModel()

                LaunchedEffect(Unit) {
                    menuViewModel.navigationEvents.collect { event ->
                        when (event) {
                            is NavigationEvent.ToLobbyScreen -> navController.navigate("lobby/${event.lobbyId}")
                            is NavigationEvent.ToJoinGameScreen -> navController.navigate("join")
                        }
                    }
                }

                Box(modifier = Modifier.padding(innerPadding)) {
                    NavHost(
                        navController = navController,
                        startDestination = "join"
                    ) {

                        //Join Game screen
                        composable("join") {
                            JoinGameScreen(
                                onJoinClick = { lobbyId ->
                                    menuViewModel.joinLobby(lobbyId)
                                }
                            )

                        }

                        //Lobby Screen
                        composable(
                            route = "lobby/{lobbyId}",
                            arguments = listOf(
                                navArgument("lobbyId") { type = NavType.StringType },
                            )
                        ) { backStackEntry ->
                            val lobbyId: String? = backStackEntry.arguments?.getString("lobbyId")
                            Log.d("MainActivity", "Navigated to lobby screen in lobby $lobbyId")
                            LobbyScreen()
                        }

                        //Game Scene
                        composable(
                            route = "game/{lobbyId}",
                            arguments = listOf(
                                navArgument("lobbyId") { type = NavType.StringType },
                            )
                        ) { backStackEntry ->
                            val lobbyId: String? = backStackEntry.arguments?.getString("lobbyId")
                            Log.d("MainActivity", "Navigated to game screen in lobby $lobbyId")
                            GameScene(navController)
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
