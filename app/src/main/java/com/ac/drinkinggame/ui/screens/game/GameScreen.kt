package com.ac.drinkinggame.ui.screens.game

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ac.drinkinggame.ui.screens.game.components.EmptyView
import com.ac.drinkinggame.ui.screens.game.components.ErrorView
import com.ac.drinkinggame.ui.screens.game.components.LoadingView
import com.ac.drinkinggame.ui.screens.game.components.SuccessView
import com.ac.drinkinggame.ui.theme.SabiondoPrimary
import org.koin.androidx.compose.koinViewModel

@Composable
fun getAuraAccentColor(styleKey: String?): Color {
  return when (styleKey) {
    "AURA_CYAN_GLOW" -> Color(0xFF00E5FF)
    "AURA_NEON_PURPLE" -> Color(0xFF8B5CF6)
    "AURA_ORANGE_GLOW" -> Color(0xFFE65100)
    "AURA_NEON_PINK" -> Color(0xFFC2185B)
    "AURA_TOXIC_GREEN" -> Color(0xFF00C853)
    "AURA_NIGHT_BLUE" -> Color(0xFF1565C0)
    "AURA_GOLDEN_AMBER" -> Color(0xFFFFA000)
    else -> SabiondoPrimary
  }
}

@Composable
fun getCategoryColor(styleKey: String?): Color {
  val baseBackground = MaterialTheme.colorScheme.background
  val accentColor = getAuraAccentColor(styleKey)
  return accentColor.copy(alpha = 0.12f).compositeOver(baseBackground)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
  categoryId: String,
  onBack: () -> Unit,
  viewModel: GameViewModel = koinViewModel(),
  initialStyleKey: String? = null, // Recibido por navegación
  modifier: Modifier = Modifier
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  
  // Extraemos la configuración visual del estado actual o de la navegación
  val successData = state as? GameState.Success
  val currentStyleKey = successData?.styleKey ?: initialStyleKey
  
  // Si tenemos un styleKey (ya sea inicial o de éxito), habilitamos la tintura
  // Nota: Podríamos pasar también el flag por navegación si fuera necesario, 
  // pero por ahora inferimos que si hay estilo, queremos el diseño nuevo.
  val useNewGameUi = successData?.useNewGameUi ?: (initialStyleKey != null)
  
  val sessionAccentColor = getAuraAccentColor(currentStyleKey)
  val backgroundColor = if (useNewGameUi) {
    getCategoryColor(currentStyleKey)
  } else {
    MaterialTheme.colorScheme.background
  }

  LaunchedEffect(categoryId) {
    viewModel.onIntent(GameIntent.LoadCards(categoryId))
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
      )
    },
    containerColor = backgroundColor,
    modifier = modifier
  ) { padding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      AnimatedContent(
        targetState = state,
        contentKey = { 
          // Al usar sessionKey del propio estado Success, garantizamos atomicidad absoluta
          if (it is GameState.Success) it.sessionKey else it::class 
        },
        label = "game_content"
      ) { targetState ->
        when (targetState) {
          GameState.Loading -> LoadingView(
            color = if (useNewGameUi) sessionAccentColor else SabiondoPrimary
          )
          is GameState.Success -> SuccessView(
            card = targetState.currentCard,
            player = targetState.currentPlayer,
            onNext = { viewModel.onIntent(GameIntent.NextCard) },
            categoryId = categoryId,
            useNewGameUi = targetState.useNewGameUi,
            sessionAccentColor = sessionAccentColor,
            sessionKey = targetState.sessionKey
          )
          GameState.Empty -> EmptyView(onRestart = onBack)
          is GameState.Error -> ErrorView(targetState.message)
        }
      }
    }
  }
}
