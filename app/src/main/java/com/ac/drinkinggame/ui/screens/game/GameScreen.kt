package com.ac.drinkinggame.ui.screens.game

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ac.drinkinggame.ui.screens.game.components.EmptyView
import com.ac.drinkinggame.ui.screens.game.components.ErrorView
import com.ac.drinkinggame.ui.screens.game.components.LoadingView
import com.ac.drinkinggame.ui.screens.game.components.SuccessView
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
  categoryId: String,
  onBack: () -> Unit,
  viewModel: GameViewModel = koinViewModel(),
  modifier: Modifier = Modifier
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(categoryId) {
    viewModel.onIntent(GameIntent.LoadCards(categoryId))
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Atrás",
              tint = Color.White
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
      )
    },
    containerColor = MaterialTheme.colorScheme.background,
    modifier = modifier
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      AnimatedContent(
        targetState = state,
        transitionSpec = {
          (slideInHorizontally { it } + fadeIn()) togetherWith
            (slideOutHorizontally { -it } + fadeOut())
        },
        contentKey = { it::class }, // Usamos la clase como key para que Success sea estable
        label = "game_content"
      ) { targetState ->

        when (targetState) {
          GameState.Loading -> LoadingView()
          is GameState.Success -> SuccessView(
            card = targetState.currentCard,
            player = targetState.currentPlayer,
            onNext = { viewModel.onIntent(GameIntent.NextCard) }
          )

          GameState.Empty -> EmptyView(onRestart = onBack)
          is GameState.Error -> ErrorView(targetState.message)
        }
      }
    }
  }
}
