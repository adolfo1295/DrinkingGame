package com.ac.drinkinggame.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ac.drinkinggame.R
import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.ui.components.CategoryCard
import com.ac.drinkinggame.ui.components.PlayersDialog
import com.ac.drinkinggame.ui.theme.SabiondoPrimary
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
  onCategorySelected: (String) -> Unit,
  viewModel: HomeViewModel = koinViewModel()
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  var showPlayersDialog by remember { mutableStateOf(false) }
  var selectedPremiumCategory by remember { mutableStateOf<Category?>(null) }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = stringResource(R.string.home_title),
          style = MaterialTheme.typography.displaySmall,
          fontWeight = FontWeight.Black,
          color = Color.White,
          letterSpacing = (-1).sp
        )

        IconButton(
          onClick = { showPlayersDialog = true },
          modifier = Modifier
            .size(56.dp)
            .background(Color.White.copy(alpha = 0.1f), CircleShape)
            .testTag("players_button")
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(30.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = stringResource(R.string.home_subtitle),
        style = MaterialTheme.typography.titleMedium,
        color = Color.White.copy(alpha = 0.6f),
        modifier = Modifier.align(Alignment.Start)
      )

      Spacer(modifier = Modifier.height(32.dp))

      when (val s = state) {
        HomeState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator(color = SabiondoPrimary)
        }

        is HomeState.Success -> {
          if (s.players.isEmpty()) {
            PlayersAlert()
          }

          LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            itemsIndexed(s.categories) { index, category ->
              var visible by remember { mutableStateOf(false) }
              LaunchedEffect(Unit) {
                visible = true
              }

              AnimatedVisibility(
                visible = visible,
                enter = slideInVertically { it / 2 } + fadeIn(),
                modifier = Modifier.padding(top = (index / 2 * 10).dp)
              ) {
                CategoryCard(
                  category = category,
                  onClick = {
                    if (s.players.isNotEmpty()) {
                      if (category.isPremium) {
                        selectedPremiumCategory = category
                      } else {
                        onCategorySelected(category.id)
                      }
                    }
                  }
                )
              }
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

          selectedPremiumCategory?.let { category ->
            PremiumPaywallDialog(
              category = category,
              onDismiss = { selectedPremiumCategory = null }
            )
          }
        }

        is HomeState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text(s.message, color = MaterialTheme.colorScheme.error)
        }
      }
    }
  }
}

@Composable
private fun PlayersAlert() {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 32.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
    ),
    shape = RoundedCornerShape(20.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
  ) {
    Row(
      modifier = Modifier.padding(20.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Icon(
        Icons.Default.Warning,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.error,
        modifier = Modifier.size(28.dp)
      )
      Text(
        text = stringResource(R.string.home_players_required),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onErrorContainer
      )
    }
  }
}

@Composable
fun PremiumPaywallDialog(category: Category, onDismiss: () -> Unit) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(R.string.premium_coming_soon_title)) },
    text = {
      Text(stringResource(R.string.premium_coming_soon_message, category.name))
    },
    confirmButton = {
      Button(onClick = onDismiss) {
        Text(stringResource(R.string.premium_coming_soon_button))
      }
    }
  )
}
