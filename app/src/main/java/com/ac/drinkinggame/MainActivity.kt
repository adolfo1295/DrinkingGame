package com.ac.drinkinggame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.ac.drinkinggame.ui.navigation.Screen
import com.ac.drinkinggame.ui.screens.game.GameScreen
import com.ac.drinkinggame.ui.screens.home.HomeScreen
import com.ac.drinkinggame.ui.theme.DrinkingGameTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
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
        HomeScreen(
          onCategorySelected = { categoryId, styleKey ->
            backStack.add(Screen.Game(categoryId, styleKey))
          }
        )
      }

      entry<Screen.Game> { key ->
        GameScreen(
          categoryId = key.categoryId,
          initialStyleKey = key.styleKey,
          onBack = {
            if (backStack.size > 1) {
              backStack.removeAt(backStack.size - 1)
            }
          }
        )
      }
    }
  )
}
