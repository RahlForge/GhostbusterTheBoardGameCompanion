package com.ghostbusters.companion.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
                                contentAlignment = Alignment.Center
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                            val maxActions = viewModel.getMaxActions()
                            val canAddMoreSlime = char.slimeCount < maxActions

                            val isTappable = isUnlocked &&
                                ability.abilityType == com.ghostbusters.companion.domain.model.AbilityType.TAPPABLE &&
                                when {
                                    // Venkman's abilities require ability to add slime
                                    char.characterName == com.ghostbusters.companion.domain.model.CharacterName.PETER_VENKMAN &&
                                    (ability.level == com.ghostbusters.companion.domain.model.Level.LEVEL_1 ||
                                     ability.level == com.ghostbusters.companion.domain.model.Level.LEVEL_2) -> canAddMoreSlime
                                    // Other abilities use the old requirement
                                    else -> !ability.requiresAction || hasActionsAvailable
                                }

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
                                        verticalAlignment = Alignment.CenterVertically
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
                var showTrapItDialog by remember { mutableStateOf(false) }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ProtonStreamTokens(
                            color = Color(char.characterName.getProtonStreamColor().hex),
                            usedStreams = (0..4).map { viewModel.isProtonStreamUsed(it) },
                            onToggle = { viewModel.toggleProtonStream(it) },
                            onTrapIt = { showTrapItDialog = true }
                        )
                    }
                }

                if (showTrapItDialog) {
                    val charactersWithStreams = viewModel.getCharactersWithActiveStreams()
                    var selectedAssistants by remember { mutableStateOf(setOf<Long>()) }

                    AlertDialog(
                        onDismissRequest = {
                            showTrapItDialog = false
                            selectedAssistants = setOf()
                        },
                        title = { Text("Trap It!") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                if (charactersWithStreams.isNotEmpty()) {
                                    Text(
                                        text = "Select assisting Ghostbusters:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        charactersWithStreams.forEach { (character, streamCount) ->
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        selectedAssistants = if (character.id in selectedAssistants) {
                                                            selectedAssistants - character.id
                                                        } else {
                                                            selectedAssistants + character.id
                                                        }
                                                    },
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (character.id in selectedAssistants) {
                                                        Color(character.characterName.getProtonStreamColor().hex).copy(alpha = 0.3f)
                                                    } else {
                                                        MaterialTheme.colorScheme.surfaceVariant
                                                    }
                                                )
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(12.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Checkbox(
                                                            checked = character.id in selectedAssistants,
                                                            onCheckedChange = null
                                                        )
                                                        Text(
                                                            text = character.characterName.getDisplayName(),
                                                            style = MaterialTheme.typography.bodyLarge
                                                        )
                                                    }
                                                    Text(
                                                        text = "$streamCount stream${if (streamCount > 1) "s" else ""}",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = Color(character.characterName.getProtonStreamColor().hex)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                Text(
                                    text = "Did you successfully trap the ghost?",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                viewModel.handleTrapIt(true, selectedAssistants.toList())
                                showTrapItDialog = false
                                selectedAssistants = setOf()
                            }) {
                                Text("Yes - Trapped!")
                            }
                        },
                        dismissButton = {
                            Column {
                                TextButton(onClick = {
                                    viewModel.handleTrapIt(false, selectedAssistants.toList())
                                    showTrapItDialog = false
                                    selectedAssistants = setOf()
                                }) {
                                    Text("No - Escaped")
                                }
                                TextButton(onClick = {
                                    showTrapItDialog = false
                                    selectedAssistants = setOf()
                                }) {
                                    Text("Cancel")
                                }
                            }
                        }
                    )
                }

                // Actions / Slime
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ActionSlimeTokens(
                            actionCount = viewModel.getMaxActions(),
                            slimeCount = char.slimeCount,
                            actionStates = (0 until viewModel.getMaxActions()).map { viewModel.isActionUsed(it) },
                            characterColor = Color(char.characterName.getProtonStreamColor().hex),
                            onActionToggle = { viewModel.toggleAction(it) },
                            onAddSlime = { viewModel.addSlime() },
                            onRemoveSlime = { viewModel.removeSlime() }
                        )
                    }
                }

                // Ghost Trap
                var showCharacterSelectDialog by remember { mutableStateOf(false) }
                var selectedGhostIdsForTransfer by remember { mutableStateOf<List<String>>(emptyList()) }

                GhostTrapSection(
                    trappedGhosts = char.trappedGhosts,
                    onTrapGhost = { rating -> viewModel.trapGhost(rating) },
                    onRemoveGhost = { ghostId -> viewModel.removeGhost(ghostId) },
                    onDepositGhosts = if (char.characterName == com.ghostbusters.companion.domain.model.CharacterName.WINSTON_ZEDDEMORE &&
                        viewModel.getCurrentLevel() >= com.ghostbusters.companion.domain.model.Level.LEVEL_1) {
                        { ghostIds -> viewModel.depositGhosts(ghostIds) }
                    } else null,
                    onTransferGhosts = { ghostIds ->
                        selectedGhostIdsForTransfer = ghostIds
                        showCharacterSelectDialog = true
                    }
                )

                // Character selection dialog for transfers
                if (showCharacterSelectDialog) {
                    val allCharacters by viewModel.allCharacters.collectAsState()
                    val otherCharacters = allCharacters.filter { it.id != char.id }

                    AlertDialog(
                        onDismissRequest = {
                            showCharacterSelectDialog = false
                            selectedGhostIdsForTransfer = emptyList()
                        },
                        title = { Text("Transfer to...") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Select which Ghostbuster to transfer the ghosts to:")
                                Spacer(modifier = Modifier.height(8.dp))
                                otherCharacters.forEach { targetChar ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                // Transfer ghosts to target character
                                                viewModel.transferGhostsToCharacter(
                                                    selectedGhostIdsForTransfer,
                                                    targetChar.id
                                                )
                                                showCharacterSelectDialog = false
                                                selectedGhostIdsForTransfer = emptyList()
                                            },
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(targetChar.characterName.getProtonStreamColor().hex).copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = targetChar.characterName.getDisplayName(),
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Text(
                                                text = "${targetChar.trappedGhosts.size} 👻",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {},
                        dismissButton = {
                            TextButton(onClick = {
                                showCharacterSelectDialog = false
                                selectedGhostIdsForTransfer = emptyList()
                            }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                // Ghost Trap Token (GB2 only)
                // This would need game type info - we'll add it later when we pass that data

                Spacer(modifier = Modifier.height(16.dp))
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

