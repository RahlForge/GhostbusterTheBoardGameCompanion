package com.ghostbusters.companion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ghostbusters.companion.ui.Screen
import com.ghostbusters.companion.ui.screens.CharacterSheetScreen
import com.ghostbusters.companion.ui.screens.GameDetailScreen
import com.ghostbusters.companion.ui.screens.GameListScreen
import com.ghostbusters.companion.ui.screens.GameSetupScreen
import com.ghostbusters.companion.ui.theme.GhostbustersCompanionTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GhostbustersCompanionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GhostbustersApp()
                }
            }
        }
    }
}

@Composable
fun GhostbustersApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.GameList.route
    ) {
        composable(Screen.GameList.route) {
            GameListScreen(
                onNavigateToSetup = {
                    navController.navigate(Screen.GameSetup.route)
                },
                onNavigateToGame = { gameId ->
                    navController.navigate(Screen.GameDetail.createRoute(gameId))
                }
            )
        }

        composable(Screen.GameSetup.route) {
            GameSetupScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onGameCreated = { gameId ->
                    navController.navigate(Screen.GameDetail.createRoute(gameId)) {
                        popUpTo(Screen.GameList.route)
                    }
                }
            )
        }

        composable(
            route = Screen.GameDetail.route,
            arguments = listOf(
                navArgument("gameId") { type = NavType.LongType }
            )
        ) {
            GameDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCharacter = { characterId ->
                    navController.navigate(Screen.CharacterSheet.createRoute(characterId))
                }
            )
        }

        composable(
            route = Screen.CharacterSheet.route,
            arguments = listOf(
                navArgument("characterId") { type = NavType.LongType }
            )
        ) {
            CharacterSheetScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

