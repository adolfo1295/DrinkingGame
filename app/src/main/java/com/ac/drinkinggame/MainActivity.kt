package com.ac.drinkinggame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.ac.drinkinggame.ui.navigation.Screen
import com.ac.drinkinggame.ui.screens.game.CategorySelectionView
import com.ac.drinkinggame.ui.screens.game.GameScreen
import com.ac.drinkinggame.ui.screens.game.GameViewModel
import com.ac.drinkinggame.ui.theme.DrinkingGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrinkingGameTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(Screen.CategorySelection)
    val activity = LocalActivity.current

    NavDisplay(
        backStack = backStack,
        onBack = { 
            if (backStack.size > 1) {
                backStack.removeAt(backStack.size - 1)
            } else {
                activity?.finish()
            }
        },
        // Quitamos los decoradores que causan conflicto en esta versión alpha
        transitionSpec = {
            (slideInHorizontally { it } + fadeIn()) togetherWith 
            (slideOutHorizontally { -it } + fadeOut())
        },
        popTransitionSpec = {
            (slideInHorizontally { -it } + fadeIn()) togetherWith 
            (slideOutHorizontally { it } + fadeOut())
        },
        entryProvider = entryProvider {
            entry<Screen.CategorySelection> {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CategorySelectionView(
                        onCategorySelected = { category ->
                            backStack.add(Screen.Game(category))
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
            
            entry<Screen.Game> { key ->
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Usamos un ID único por sesión para forzar un ViewModel fresco en cada juego
                    // Esto soluciona el glitch visual sin depender de los decoradores experimentales
                    val gameSessionId = remember(key) { "game_${key.category.name}_${System.currentTimeMillis()}" }
                    val viewModel: GameViewModel = viewModel(key = gameSessionId)
                    
                    LaunchedEffect(key.category) {
                        viewModel.onIntent(com.ac.drinkinggame.ui.screens.game.GameIntent.SelectCategory(key.category))
                    }
                    
                    GameScreen(
                        viewModel = viewModel,
                        onBack = { 
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.size - 1)
                            }
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    )
}
