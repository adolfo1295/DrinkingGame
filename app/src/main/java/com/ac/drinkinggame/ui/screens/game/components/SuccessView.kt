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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ac.drinkinggame.R
import com.ac.drinkinggame.domain.model.GameCard
import com.ac.drinkinggame.domain.model.Player
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
