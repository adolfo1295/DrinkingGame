package com.ac.drinkinggame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ac.drinkinggame.domain.model.Player
import com.ac.drinkinggame.ui.theme.NightclubCard
import com.ac.drinkinggame.ui.theme.NightclubSurface
import com.ac.drinkinggame.ui.theme.SabiondoPrimary

@Composable
fun PlayersDialog(
  players: List<Player>,
  onAddPlayer: (String) -> Unit,
  onRemovePlayer: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var newPlayerName by remember { mutableStateOf("") }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .fillMaxHeight(0.8f),
      shape = RoundedCornerShape(32.dp),
      colors = CardDefaults.cardColors(containerColor = NightclubCard),
      border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(28.dp)
      ) {
        Text(
          "Participantes",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Black,
          color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedTextField(
            value = newPlayerName,
            onValueChange = { newPlayerName = it },
            placeholder = { Text("Nombre del jugador") },
            modifier = Modifier
              .weight(1f)
              .testTag("player_input"),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
              unfocusedTextColor = Color.White,
              focusedTextColor = Color.White,
              unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
              focusedBorderColor = SabiondoPrimary
            )
          )

          IconButton(
            onClick = {
              if (newPlayerName.isNotBlank()) {
                onAddPlayer(newPlayerName)
                newPlayerName = ""
              }
            },
            modifier = Modifier
              .size(56.dp)
              .background(SabiondoPrimary, RoundedCornerShape(16.dp))
              .testTag("add_player_confirm")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(players) { player ->
            Surface(
              modifier = Modifier.fillMaxWidth(),
              color = NightclubSurface,
              shape = RoundedCornerShape(16.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  player.name,
                  style = MaterialTheme.typography.titleMedium,
                  color = Color.White,
                  fontWeight = FontWeight.Medium
                )
                IconButton(
                  onClick = { onRemovePlayer(player.id) },
                  modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f), CircleShape)
                ) {
                  Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
          Text("LISTO", fontWeight = FontWeight.Black, color = Color.Black)
        }
      }
    }
  }
}
