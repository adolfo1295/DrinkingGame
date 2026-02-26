package com.ac.drinkinggame.ui.screens.game

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.domain.model.GameCard
import com.ac.drinkinggame.ui.theme.*

@Composable
fun CategorySelectionView(
  onCategorySelected: (Category) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(32.dp))
    Text(
      text = "Escoge tu destino",
      style = MaterialTheme.typography.headlineLarge,
      fontWeight = FontWeight.Black,
      color = Color.White
    )
    Text(
      text = "Prepárate para la acción",
      style = MaterialTheme.typography.bodyLarge,
      color = Color.White.copy(alpha = 0.7f),
      modifier = Modifier.padding(bottom = 32.dp)
    )

    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(Category.entries.toTypedArray()) { category ->
        CategoryCard(category = category, onClick = { onCategorySelected(category) })
      }
    }
  }
}

@Composable
private fun CategoryCard(category: Category, onClick: () -> Unit) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "scale")

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .height(180.dp)
      .graphicsLayer(scaleX = scale, scaleY = scale)
      .clickable(
        interactionSource = interactionSource,
        indication = LocalIndication.current,
        onClick = onClick
      ),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    shape = RoundedCornerShape(24.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      Box(
        modifier = Modifier
          .size(80.dp)
          .align(Alignment.TopEnd)
          .background(
            Brush.radialGradient(
              colors = listOf(category.color.copy(alpha = 0.3f), Color.Transparent)
            )
          )
      )

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Text(category.icon, fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = category.title,
          color = Color.White,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.ExtraBold,
          textAlign = TextAlign.Center
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
  viewModel: GameViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

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
    // Animamos el cambio de contenido interno (Loading -> Success -> Empty)
    AnimatedContent(
      targetState = state,
      transitionSpec = {
        fadeIn() togetherWith fadeOut()
      },
      label = "game_content"
    ) { targetState ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        when (targetState) {
          GameState.CategorySelection -> {}
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
      // Animamos el cambio de texto e información dentro de la carta
      AnimatedContent(
        targetState = card,
        transitionSpec = {
          (slideInVertically { it / 2 } + fadeIn()) togetherWith
                  (slideOutVertically { -it / 2 } + fadeOut())
        },
        label = "card_content"
      ) { targetCard ->
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          val (title, color) = when (targetCard) {
            is GameCard.Challenge -> (if (targetCard.isLocoMode) "RETO LOCO" else "RETO FAMILIAR") to (if (targetCard.isLocoMode) LocoPrimary else FamiliarPrimary)
            is GameCard.Rule -> "NUEVA REGLA" to Color(0xFF66BB6A)
            is GameCard.Trivia -> "TRIVIA" to SabiondoPrimary
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

          val mainText = when (targetCard) {
            is GameCard.Challenge -> targetCard.title
            is GameCard.Rule -> targetCard.title
            is GameCard.Trivia -> targetCard.question
          }

          Text(
            text = mainText,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.White
          )

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = targetCard.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.White.copy(alpha = 0.8f)
          )

          Spacer(modifier = Modifier.height(48.dp))

          Text(
            text = "${targetCard.penalty}",
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
    Text("Barajando...", color = Color.White)
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
      "¡Sobreviviste!",
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )
    Spacer(modifier = Modifier.height(32.dp))
    Button(onClick = onRestart) {
      Text("Volver a empezar")
    }
  }
}

@Composable
private fun ErrorView(message: String) {
  Text("Error: $message", color = MaterialTheme.colorScheme.error)
}
