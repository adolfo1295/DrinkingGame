package com.ac.drinkinggame.ui.screens.game

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ac.drinkinggame.domain.model.GameCard
import com.ac.drinkinggame.ui.theme.*
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
        label = "game_content"
      ) { targetState ->
        when (targetState) {
          GameState.Loading -> LoadingView()
          is GameState.Success -> SuccessView(
            card = targetState.currentCard,
            onNext = { viewModel.onIntent(GameIntent.NextCard) }
          )

          GameState.Empty -> EmptyView(onRestart = onBack)
          is GameState.Error -> ErrorView(targetState.message)
        }
      }
    }
  }
}

@Composable
private fun SuccessView(card: GameCard, onNext: () -> Unit) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(vertical = 32.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = RoundedCornerShape(32.dp),
      elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        val title: String
        val color: Color
        val mainText: String
        val description: String
        val penalty: Int

        when (card) {
          is GameCard.Trivia -> {
            title = "TRIVIA"
            color = SabiondoPrimary
            mainText = card.question
            description = card.options?.joinToString("\n") ?: ""
            penalty = card.penalty
          }

          is GameCard.Challenge -> {
            title = "RETO"
            color = LocoPrimary
            mainText = card.title
            description = card.description
            penalty = card.penalty
          }

          is GameCard.Rule -> {
            title = "REGLA"
            color = FamiliarPrimary
            mainText = card.title
            description = card.rule
            penalty = 0
          }
        }

        Surface(
          color = color.copy(alpha = 0.2f),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = title,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color = color,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
          text = mainText,
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center,
          color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = description,
          style = MaterialTheme.typography.bodyLarge,
          textAlign = TextAlign.Center,
          color = Color.White.copy(alpha = 0.8f)
        )

        if (penalty > 0) {
          Spacer(modifier = Modifier.height(48.dp))
          Text(
            text = "$penalty",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Black,
            color = color
          )
          Text(
            text = "TRAGOS DE CASTIGO",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = color.copy(alpha = 0.8f)
          )
        }
      }
    }

    Button(
      onClick = onNext,
      modifier = Modifier
        .fillMaxWidth()
        .height(64.dp),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
      Text("¡ENTENDIDO!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
private fun LoadingView() {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    CircularProgressIndicator(color = SabiondoPrimary)
    Spacer(modifier = Modifier.height(16.dp))
    Text("Cargando cartas...", color = Color.White)
  }
}

@Composable
private fun EmptyView(onRestart: () -> Unit) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text("🎊", fontSize = 80.sp)
    Text(
      "¡Fin de la partida!",
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )
    Spacer(modifier = Modifier.height(32.dp))
    Button(onClick = onRestart) {
      Text("Volver al inicio")
    }
  }
}

@Composable
private fun ErrorView(message: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text("Ocurrió un error", color = MaterialTheme.colorScheme.error)
    Text(message, color = Color.White, textAlign = TextAlign.Center)
  }
}
