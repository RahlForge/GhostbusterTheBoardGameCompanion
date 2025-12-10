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
import com.ghostbusters.companion.domain.model.CharacterAbility
import com.ghostbusters.companion.domain.model.Level
import com.ghostbusters.companion.ui.components.GhostTrapSection
import com.ghostbusters.companion.ui.components.ProtonStreamTokens
import com.ghostbusters.companion.ui.components.ActionSlimeTokens
import com.ghostbusters.companion.ui.components.XpTracker
import com.ghostbusters.companion.ui.viewmodels.CharacterSheetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterSheetScreen(
    onNavigateBack: () -> Unit,
    viewModel: CharacterSheetViewModel = hiltViewModel()
) {
    val character by viewModel.character.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(character?.characterName?.getDisplayName() ?: "Character")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        character?.let { char ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Character header with portrait placeholder
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(char.characterName.getProtonStreamColor().hex).copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Portrait placeholder
                        Surface(
                            modifier = Modifier.size(80.dp),
                            color = Color(char.characterName.getProtonStreamColor().hex)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                Text(
                                    text = "👻",
                                    style = MaterialTheme.typography.displayMedium
                                )
                            }
                        }
                        
                        Column {
                            Text(
                                text = char.characterName.getDisplayName(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Level ${viewModel.getCurrentLevel().ordinal + 1}",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(char.characterName.getProtonStreamColor().hex)
                            )
                        }
                    }
                }
                
                // XP Tracker
                XpTracker(
                    currentXp = char.xp,
                    onXpChange = { viewModel.setXp(it) }
                )
                
                // Abilities
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Abilities",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        val abilities = CharacterAbility.getDefaultAbilities(char.characterName)
                        val currentLevel = viewModel.getCurrentLevel()
                        
                        abilities.forEach { ability ->
                            val isUnlocked = currentLevel >= ability.level
                            
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isUnlocked) 
                                        MaterialTheme.colorScheme.primaryContainer 
                                    else 
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Level ${ability.level.ordinal + 1}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isUnlocked) 
                                            MaterialTheme.colorScheme.onPrimaryContainer 
                                        else 
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                    if (isUnlocked) {
                                        Text(
                                            text = ability.description,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    } else {
                                        Text(
                                            text = "🔒 Locked",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Proton Streams
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ProtonStreamTokens(
                            color = Color(char.characterName.getProtonStreamColor().hex),
                            usedStreams = (0..4).map { viewModel.isProtonStreamUsed(it) },
                            onToggle = { viewModel.toggleProtonStream(it) }
                        )
                    }
                }
                
                // Actions / Slime
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ActionSlimeTokens(
                            actionCount = viewModel.getMaxActions(),
                            usedActions = (0 until viewModel.getMaxActions()).map { viewModel.isActionUsed(it) },
                            onToggle = { viewModel.toggleAction(it) }
                        )
                    }
                }
                
                // Ghost Trap
                GhostTrapSection(
                    trappedGhosts = char.trappedGhosts,
                    onTrapGhost = { rating -> viewModel.trapGhost(rating) },
                    onRemoveGhost = { ghostId -> viewModel.removeGhost(ghostId) }
                )
                
                // Ghost Trap Token (GB2 only)
                // This would need game type info - we'll add it later when we pass that data
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

