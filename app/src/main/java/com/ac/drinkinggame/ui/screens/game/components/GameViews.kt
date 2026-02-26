package com.ac.drinkinggame.ui.screens.game.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ac.drinkinggame.ui.theme.SabiondoPrimary

@Composable
fun LoadingView() {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    CircularProgressIndicator(color = SabiondoPrimary)
    Spacer(modifier = Modifier.height(16.dp))
    Text("Cargando cartas...", color = Color.White)
  }
}

@Composable
fun EmptyView(onRestart: () -> Unit) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text("🎊", fontSize = 120.sp)
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      "¡FIN DE LA PARTIDA!",
      style = MaterialTheme.typography.displaySmall,
      fontWeight = FontWeight.Black,
      color = Color.White,
      textAlign = TextAlign.Center
    )
    Text(
      "Han sobrevivido una noche más.",
      style = MaterialTheme.typography.bodyLarge,
      color = Color.White.copy(alpha = 0.6f)
    )
    Spacer(modifier = Modifier.height(48.dp))
    Button(
      onClick = onRestart,
      modifier = Modifier
        .fillMaxWidth()
        .height(64.dp),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(containerColor = Color.White)
    ) {
      Text("VOLVER AL INICIO", color = Color.Black, fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
fun ErrorView(message: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text("Ocurrió un error", color = MaterialTheme.colorScheme.error)
    Text(message, color = Color.White, textAlign = TextAlign.Center)
  }
}
