package com.ghostbusters.companion.ui.screens

 import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ghostbusters.companion.domain.model.CharacterName
import com.ghostbusters.companion.domain.model.ExpansionType
import com.ghostbusters.companion.domain.model.GameType
import com.ghostbusters.companion.ui.viewmodels.GameSetupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSetupScreen(
    onNavigateBack: () -> Unit,
    onGameCreated: (Long) -> Unit,
    viewModel: GameSetupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Game") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Game Name
            OutlinedTextField(
                value = state.gameName,
                onValueChange = { viewModel.updateGameName(it) },
                label = { Text("Game Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Game Type Selection
            Text(
                text = "Select Game",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GameType.entries.forEach { gameType ->
                    FilterChip(
                        selected = state.gameType == gameType,
                        onClick = { viewModel.selectGameType(gameType) },
                        label = { Text(gameType.name.replace("_", " ")) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Expansions (only for GB2)
            if (state.gameType == GameType.GHOSTBUSTERS_II) {
                Text(
                    text = "Expansions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExpansionType.entries.forEach { expansion ->
                        FilterChip(
                            selected = state.selectedExpansions.contains(expansion),
                            onClick = { viewModel.toggleExpansion(expansion) },
                            label = { Text(expansion.name.replace("_", " ")) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            
            // Character Selection
            Text(
                text = "Select Characters (1-4)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            val availableCharacters = viewModel.getAvailableCharacters()
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                availableCharacters.forEach { character ->
                    CharacterChip(
                        character = character,
                        isSelected = state.selectedCharacters.contains(character),
                        onClick = { viewModel.toggleCharacter(character) },
                        isEnabled = state.selectedCharacters.size < 4 || state.selectedCharacters.contains(character)
                    )
                }
            }
            
            // Error message
            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Create button
            Button(
                onClick = { viewModel.createGame(onGameCreated) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isCreating && state.gameName.isNotBlank() && state.selectedCharacters.isNotEmpty()
            ) {
                if (state.isCreating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create Game")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterChip(
    character: CharacterName,
    isSelected: Boolean,
    onClick: () -> Unit,
    isEnabled: Boolean
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        enabled = isEnabled,
        label = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(character.getDisplayName())
                Text(
                    text = "●",
                    color = Color(character.getProtonStreamColor().hex),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
