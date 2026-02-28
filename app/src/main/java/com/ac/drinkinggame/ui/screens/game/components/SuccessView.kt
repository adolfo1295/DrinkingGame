package com.ac.drinkinggame.ui.screens.game.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ac.drinkinggame.R
import com.ac.drinkinggame.domain.model.GameCard
import com.ac.drinkinggame.domain.model.Player
import com.ac.drinkinggame.ui.theme.FamiliarPrimary
import com.ac.drinkinggame.ui.theme.LocoPrimary
import com.ac.drinkinggame.ui.theme.NightclubCard
import com.ac.drinkinggame.ui.theme.SabiondoPrimary

@Composable
fun SuccessView(
  card: GameCard,
  player: Player?,
  onNext: () -> Unit,
  categoryId: String,
  useNewGameUi: Boolean = false,
  sessionAccentColor: Color = SabiondoPrimary
) {
  val rotation = remember { Animatable(0f) }
  var displayedCard by remember { mutableStateOf(card) }
  var isFirstLoad by remember { mutableStateOf(true) }
  var currentCategory by remember { mutableStateOf(categoryId) }

  // En el modo clásico, unificamos al azul de la marca para mayor consistencia.
  // En el modo nuevo, usamos el color dinámico de la sesión de Supabase.
  val finalAccentColor = if (useNewGameUi) {
    sessionAccentColor
  } else {
    SabiondoPrimary
  }

  if (currentCategory != categoryId) {
    currentCategory = categoryId
    isFirstLoad = true
    displayedCard = card
  }

  LaunchedEffect(card.id) {
    if (!isFirstLoad && card.id != displayedCard.id) {
      rotation.animateTo(
        targetValue = 180f,
        animationSpec = tween(durationMillis = 500)
      )
      displayedCard = card
      rotation.snapTo(0f)
    } else {
      displayedCard = card
      isFirstLoad = false
    }
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    player?.let {
      Card(
        colors = CardDefaults.cardColors(
          containerColor = if (useNewGameUi) finalAccentColor.copy(alpha = 0.1f) else SabiondoPrimary.copy(alpha = 0.15f)
        ),
        shape = CircleShape,
        border = if (useNewGameUi) BorderStroke(1.5.dp, finalAccentColor.copy(alpha = 0.4f)) else null
      ) {
        Text(
          text = stringResource(R.string.game_player_turn, it.name).uppercase(),
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Black,
          color = if (useNewGameUi) finalAccentColor else SabiondoPrimary,
          modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .testTag("player_name")
        )
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    if (useNewGameUi) {
      NeonGameCard(
        card = if (rotation.value > 90f) card else displayedCard,
        rotation = rotation.value,
        accentColor = finalAccentColor
      )
    } else {
      ClassicGameCard(
        card = if (rotation.value > 90f) card else displayedCard,
        rotation = rotation.value,
        accentColor = finalAccentColor
      )
    }

    Spacer(modifier = Modifier.weight(1.2f))

    Button(
      onClick = onNext,
      enabled = !rotation.isRunning,
      modifier = Modifier
        .fillMaxWidth()
        .height(72.dp)
        .testTag("next_card_button"),
      shape = RoundedCornerShape(24.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = if (useNewGameUi) finalAccentColor else MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        disabledContainerColor = if (useNewGameUi) finalAccentColor.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        disabledContentColor = Color.White.copy(alpha = 0.5f)
      )
    ) {
      Text(
        text = if (rotation.isRunning) {
          stringResource(R.string.game_card_shuffling)
        } else {
          stringResource(R.string.game_card_understood)
        }.uppercase(),
        fontSize = 18.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp
      )
    }
  }
}

@Composable
private fun NeonGameCard(
  card: GameCard,
  rotation: Float,
  accentColor: Color
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp)
      .graphicsLayer {
        rotationY = rotation
        cameraDistance = 15f * density
      },
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2230)),
    shape = RoundedCornerShape(32.dp),
    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
  ) {
    val isFlipped = rotation > 90f
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .graphicsLayer { if (isFlipped) rotationY = 180f }
    ) {
      CardContent(card = card, accentColor = accentColor, isNeon = true)
    }
  }
}

@Composable
private fun ClassicGameCard(
  card: GameCard,
  rotation: Float,
  accentColor: Color
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 16.dp)
      .graphicsLayer {
        rotationY = rotation
        cameraDistance = 12f * density
      },
    colors = CardDefaults.cardColors(containerColor = NightclubCard),
    shape = RoundedCornerShape(40.dp),
    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
  ) {
    val isFlipped = rotation > 90f
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .graphicsLayer { if (isFlipped) rotationY = 180f }
    ) {
      CardContent(card = card, accentColor = accentColor, isNeon = false)
    }
  }
}

@Composable
private fun CardContent(card: GameCard, accentColor: Color, isNeon: Boolean) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    val (title, mainText, description, penalty) = when (card) {
      is GameCard.Trivia -> listOf("TRIVIA", card.question, card.options?.joinToString("\n") ?: "", card.penalty)
      is GameCard.Challenge -> listOf("RETO", card.title, card.description, card.penalty)
      is GameCard.Rule -> listOf("REGLA", card.title, card.rule, 0)
    }.let { list ->
      listOf(list[0] as String, list[1] as String, list[2] as String, list[3] as Int)
    }

    Surface(
      color = accentColor,
      shape = if (isNeon) RoundedCornerShape(8.dp) else CircleShape
    ) {
      Text(
        text = title as String,
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
        color = Color.Black,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Black
      )
    }

    Spacer(modifier = Modifier.height(32.dp))

    Text(
      text = mainText as String,
      style = if (isNeon) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.ExtraBold,
      textAlign = TextAlign.Center,
      color = Color.White,
      lineHeight = if (isNeon) 32.sp else 36.sp
    )

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = description as String,
      style = if (isNeon) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.titleLarge,
      textAlign = TextAlign.Center,
      color = Color.White.copy(alpha = 0.7f),
      lineHeight = 24.sp,
      fontWeight = if (isNeon) FontWeight.Medium else FontWeight.Normal
    )

    if ((penalty as Int) > 0) {
      Spacer(modifier = Modifier.height(56.dp))
      if (isNeon) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(90.dp)) {
          Surface(
            modifier = Modifier.fillMaxSize(),
            color = accentColor.copy(alpha = 0.1f),
            shape = CircleShape,
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.2f))
          ) {}
          Text(
            text = "$penalty",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Black,
            color = accentColor
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = stringResource(R.string.game_penalty_label).uppercase(),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Black,
          color = accentColor.copy(alpha = 0.8f),
          letterSpacing = 4.sp
        )
      } else {
        Text(
          text = "$penalty",
          style = MaterialTheme.typography.displayLarge,
          fontWeight = FontWeight.Black,
          color = accentColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = stringResource(R.string.game_penalty_label).uppercase(),
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.ExtraBold,
          color = accentColor.copy(alpha = 0.7f),
          letterSpacing = 4.sp
        )
      }
    }
  }
}
