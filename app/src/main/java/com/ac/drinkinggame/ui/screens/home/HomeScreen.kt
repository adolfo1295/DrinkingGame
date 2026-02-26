package com.ac.drinkinggame.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.domain.model.Player
import com.ac.drinkinggame.ui.theme.SabiondoPrimary
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onCategorySelected: (String) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showPlayersDialog by remember { mutableStateOf(false) }
    var showPremiumDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Drinking Game",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            
            IconButton(
                onClick = { showPlayersDialog = true },
                modifier = Modifier
                    .size(56.dp)
                    .testTag("players_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Person, 
                    contentDescription = "Jugadores", 
                    tint = Color.White,
                    modifier = Modifier.size(32.dp) // Aumentamos el tamaño del icono interno
                )
            }
        }

        Text(
            text = "Escoge un modo de juego",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 32.dp).align(Alignment.Start)
        )

        when (val s = state) {
            HomeState.Loading -> CircularProgressIndicator(color = SabiondoPrimary)
            is HomeState.Success -> {
                if (s.players.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            "Añade al menos un jugador para empezar",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(s.categories) { category ->
                        CategoryCard(
                            category = category, 
                            onClick = { 
                                if (s.players.isNotEmpty()) {
                                    if (category.isPremium) {
                                        showPremiumDialog = true
                                    } else {
                                        onCategorySelected(category.id)
                                    }
                                }
                            }
                        )
                    }
                }

                if (showPlayersDialog) {
                    PlayersDialog(
                        players = s.players,
                        onAddPlayer = viewModel::addPlayer,
                        onRemovePlayer = viewModel::removePlayer,
                        onDismiss = { showPlayersDialog = false }
                    )
                }

                if (showPremiumDialog) {
                    PremiumPaywallDialog(onDismiss = { showPremiumDialog = false })
                }
            }
            is HomeState.Error -> Text(s.message, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun PremiumPaywallDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🚀 ¡Pásate a Premium!") },
        text = { 
            Text("Desbloquea todos los modos de juego, cartas exclusivas y juega sin límites con tus amigos.") 
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Ver Planes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Ahora no")
            }
        }
    )
}

@Composable
fun PlayersDialog(
    players: List<Player>,
    onAddPlayer: (String) -> Unit,
    onRemovePlayer: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newPlayerName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Participantes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                    TextField(
                        value = newPlayerName,
                        onValueChange = { newPlayerName = it },
                        placeholder = { Text("Nombre") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("player_input"),
                        singleLine = true
                    )
                    IconButton(
                        onClick = { 
                            if (newPlayerName.isNotBlank()) {
                                onAddPlayer(newPlayerName)
                                newPlayerName = ""
                            }
                        },
                        modifier = Modifier.testTag("add_player_confirm")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir")
                    }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(players) { player ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(player.name, style = MaterialTheme.typography.bodyLarge)
                            IconButton(onClick = { onRemovePlayer(player.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
                            }
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text("Listo")
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(category: Category, onClick: () -> Unit) {
    val borderColor = if (category.isPremium) Color(0xFFFFD700) else Color.Transparent
    val containerColor = if (category.isPremium) 
        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f) 
    else 
        MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(24.dp),
        border = if (category.isPremium) BorderStroke(1.dp, borderColor) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (category.isPremium) 0.dp else 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (category.isPremium) {
                Surface(
                    color = Color(0xFFFFD700),
                    shape = RoundedCornerShape(bottomStart = 12.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "🔒",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = category.name,
                    color = if (category.isPremium) Color.White.copy(alpha = 0.6f) else Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
