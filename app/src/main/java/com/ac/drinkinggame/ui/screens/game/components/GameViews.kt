package com.ac.drinkinggame.ui.screens.game.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import com.ac.drinkinggame.ui.theme.SabiondoPrimary

@Composable
fun LoadingView() {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    CircularProgressIndicator(color = SabiondoPrimary)
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = stringResource(R.string.game_loading_cards),
      color = Color.White
    )
  }
}

@Composable
fun EmptyView(onRestart: () -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text("🍻", fontSize = 120.sp)
    Spacer(modifier = Modifier.height(32.dp))
    Text(
      text = stringResource(R.string.game_empty_title),
      style = MaterialTheme.typography.displayMedium,
      fontWeight = FontWeight.Black,
      color = Color.White,
      textAlign = TextAlign.Center,
      lineHeight = 48.sp,
      letterSpacing = (-1).sp
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
      text = stringResource(R.string.game_empty_subtitle),
      style = MaterialTheme.typography.titleLarge,
      color = Color.White.copy(alpha = 0.6f),
      textAlign = TextAlign.Center,
      fontWeight = FontWeight.Medium
    )
    Spacer(modifier = Modifier.height(64.dp))
    Button(
      onClick = onRestart,
      modifier = Modifier
        .fillMaxWidth()
        .height(72.dp),
      shape = RoundedCornerShape(24.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = SabiondoPrimary,
        contentColor = Color.Black
      ),
      elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
      Text(
        text = stringResource(R.string.game_empty_button_back),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp
      )
    }
  }
}

@Composable
fun ErrorView(message: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = stringResource(R.string.game_error_title),
      color = MaterialTheme.colorScheme.error
    )
    Text(
      text = message,
      color = Color.White,
      textAlign = TextAlign.Center
    )
  }
}
