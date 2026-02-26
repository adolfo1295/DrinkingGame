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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
  onNext: () -> Unit
) {
  val rotation = remember { Animatable(0f) }
  var displayedCard by remember { mutableStateOf(card) }

  LaunchedEffect(card.id) {
    if (card.id != displayedCard.id) {
      rotation.animateTo(
        targetValue = 180f,
        animationSpec = tween(durationMillis = 500)
      )
      displayedCard = card
      rotation.snapTo(0f)
    }
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Turno del Jugador
    player?.let {
      Card(
        colors = CardDefaults.cardColors(containerColor = SabiondoPrimary.copy(alpha = 0.15f)),
        shape = CircleShape,
        border = BorderStroke(1.dp, SabiondoPrimary.copy(alpha = 0.3f))
      ) {
        Text(
          text = "Turno de: ${it.name}",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Black,
          color = SabiondoPrimary,
          modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .testTag("player_name")
        )
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    // La Carta 3D
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
        .graphicsLayer {
          rotationY = rotation.value
          cameraDistance = 12f * density
        },
      colors = CardDefaults.cardColors(containerColor = NightclubCard),
      shape = RoundedCornerShape(40.dp),
      border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
      elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
    ) {
      val isFlipped = rotation.value > 90f

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .graphicsLayer {
            if (isFlipped) rotationY = 180f
          }
      ) {
        if (!isFlipped) {
          CardContent(card = displayedCard)
        } else {
          CardContent(card = card)
        }
      }
    }

    Spacer(modifier = Modifier.weight(1.2f))

    // Botón de Acción con bloqueo durante animación
    Button(
      onClick = onNext,
      enabled = !rotation.isRunning, // BLOQUEO: Evita clics rápidos mientras gira
      modifier = Modifier
        .fillMaxWidth()
        .height(72.dp)
        .testTag("next_card_button"),
      shape = RoundedCornerShape(20.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
      )
    ) {
      Text(
        text = if (rotation.isRunning) "BARAJANDO..." else "¡ENTENDIDO!",
        fontSize = 20.sp,
        fontWeight = FontWeight.Black
      )
    }
  }
}

@Composable
private fun CardContent(card: GameCard) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    val (title, color, mainText, description, penalty) = when (card) {
      is GameCard.Trivia -> {
        listOf(
          "TRIVIA",
          SabiondoPrimary,
          card.question,
          card.options?.joinToString("\n") ?: "",
          card.penalty
        )
      }

      is GameCard.Challenge -> {
        listOf("RETO", LocoPrimary, card.title, card.description, card.penalty)
      }

      is GameCard.Rule -> {
        listOf("REGLA", FamiliarPrimary, card.title, card.rule, 0)
      }
    }.let { list ->
      listOf(
        list[0] as String,
        list[1] as Color,
        list[2] as String,
        list[3] as String,
        list[4] as Int
      )
    }

    Surface(
      color = color as Color,
      shape = CircleShape
    ) {
      Text(
        text = title as String,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
        color = Color.Black,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Black
      )
    }

    Spacer(modifier = Modifier.height(32.dp))

    Text(
      text = mainText as String,
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.ExtraBold,
      textAlign = TextAlign.Center,
      color = Color.White,
      lineHeight = 36.sp
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = description as String,
      style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
      textAlign = TextAlign.Center,
      color = Color.White.copy(alpha = 0.7f),
      lineHeight = 28.sp
    )

    if ((penalty as Int) > 0) {
      Spacer(modifier = Modifier.height(40.dp))
      Text(
        text = "$penalty",
        style = MaterialTheme.typography.displayLarge,
        fontWeight = FontWeight.Black,
        color = color
      )
      Text(
        text = "TRAGOS",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Black,
        color = color.copy(alpha = 0.7f),
        letterSpacing = 4.sp
      )
    }
  }
}
