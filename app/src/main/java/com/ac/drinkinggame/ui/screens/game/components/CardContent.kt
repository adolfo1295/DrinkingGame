package com.ac.drinkinggame.ui.screens.game.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ac.drinkinggame.R
import com.ac.drinkinggame.domain.model.GameCard

private data class CardDisplayData(
  val title: String,
  val mainText: String,
  val description: String,
  val penalty: Int
)

@Composable
fun CardContent(
  card: GameCard,
  accentColor: Color,
  isNeon: Boolean,
  modifier: Modifier = Modifier
) {
  val displayData = when (card) {
    is GameCard.Trivia -> CardDisplayData("TRIVIA", card.question, card.options?.joinToString("\n") ?: "", card.penalty)
    is GameCard.Challenge -> CardDisplayData("RETO", card.title, card.description, card.penalty)
    is GameCard.Rule -> CardDisplayData("REGLA", card.title, card.rule, 0)
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Surface(
      color = accentColor,
      shape = if (isNeon) RoundedCornerShape(8.dp) else CircleShape
    ) {
      Text(
        text = displayData.title,
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
        color = Color.Black,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Black
      )
    }

    Spacer(modifier = Modifier.height(32.dp))

    Text(
      text = displayData.mainText,
      style = if (isNeon) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.ExtraBold,
      textAlign = TextAlign.Center,
      color = Color.White,
      lineHeight = if (isNeon) 32.sp else 36.sp
    )

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = displayData.description,
      style = if (isNeon) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.titleLarge,
      textAlign = TextAlign.Center,
      color = Color.White.copy(alpha = 0.7f),
      lineHeight = 24.sp,
      fontWeight = if (isNeon) FontWeight.Medium else FontWeight.Normal
    )

    if (displayData.penalty > 0) {
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
            text = "${displayData.penalty}",
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
          text = "${displayData.penalty}",
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
