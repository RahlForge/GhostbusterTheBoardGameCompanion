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
    val gameInstance by viewModel.gameInstance.collectAsState()

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

                // Character Stats
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val stats = viewModel.getCharacterStats()
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text(
                                text = "Move/Drive",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${stats.move}/${stats.drive}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text(
                                text = "Line of Sight",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${stats.lineOfSight}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
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

                        val abilities = CharacterAbility.getAbilitiesForCharacter(
                            char.characterName,
                            gameInstance?.gameType ?: com.ghostbusters.companion.domain.model.GameType.GHOSTBUSTERS
                        )
                        val currentLevel = viewModel.getCurrentLevel()
                        val hasActionsAvailable = viewModel.hasActionsAvailable()

                        abilities.forEach { ability ->
                            val isUnlocked = currentLevel >= ability.level
                            val isTappable = isUnlocked &&
                                ability.abilityType == com.ghostbusters.companion.domain.model.AbilityType.TAPPABLE &&
                                (!ability.requiresAction || hasActionsAvailable)

                            Card(
                                modifier = if (isTappable) {
                                    Modifier.clickable {
                                        viewModel.useAbility(ability)
                                    }
                                } else {
                                    Modifier
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        !isUnlocked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                        isTappable -> MaterialTheme.colorScheme.secondaryContainer
                                        else -> MaterialTheme.colorScheme.primaryContainer
                                    }
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Level ${ability.level.ordinal + 1}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (isUnlocked)
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                        )

                                        // Show ability type badge
                                        if (isUnlocked && ability.abilityType != com.ghostbusters.companion.domain.model.AbilityType.PASSIVE) {
                                            Surface(
                                                color = when (ability.abilityType) {
                                                    com.ghostbusters.companion.domain.model.AbilityType.TAPPABLE -> MaterialTheme.colorScheme.secondary
                                                    com.ghostbusters.companion.domain.model.AbilityType.TEAM_PASSIVE -> MaterialTheme.colorScheme.tertiary
                                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                                },
                                                shape = MaterialTheme.shapes.small
                                            ) {
                                                Text(
                                                    text = when (ability.abilityType) {
                                                        com.ghostbusters.companion.domain.model.AbilityType.TAPPABLE -> "TAP"
                                                        com.ghostbusters.companion.domain.model.AbilityType.TEAM_PASSIVE -> "TEAM"
                                                        com.ghostbusters.companion.domain.model.AbilityType.TRIGGERED_BY_DICE -> "DICE"
                                                        else -> ""
                                                    },
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    color = MaterialTheme.colorScheme.onSecondary
                                                )
                                            }
                                        }
                                    }

                                    if (isUnlocked) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = ability.description,
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        if (isTappable) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "👆 Tap to use",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.height(4.dp))
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
                            gameType = gameInstance?.gameType ?: com.ghostbusters.companion.domain.model.GameType.GHOSTBUSTERS,
                            onToggle = { viewModel.toggleAction(it) }
                        )
                    }
                }

                // Ghost Trap
                GhostTrapSection(
                    trappedGhosts = char.trappedGhosts,
                    onTrapGhost = { rating -> viewModel.trapGhost(rating) },
                    onRemoveGhost = { ghostId -> viewModel.removeGhost(ghostId) },
                    onDepositGhosts = if (char.characterName == com.ghostbusters.companion.domain.model.CharacterName.WINSTON_ZEDDEMORE &&
                        viewModel.getCurrentLevel() >= com.ghostbusters.companion.domain.model.Level.LEVEL_1) {
                        { ghostIds -> viewModel.depositGhosts(ghostIds) }
                    } else null
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

