package com.ghostbusters.companion.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ghostbusters.companion.data.database.entities.TrappedGhostData
import com.ghostbusters.companion.ui.theme.TrapBlack
import com.ghostbusters.companion.ui.theme.TrapYellow

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun GhostTrapSection(
    trappedGhosts: List<TrappedGhostData>,
    onTrapGhost: (Int) -> Unit,
    onRemoveGhost: (String) -> Unit,
    onDepositGhosts: ((List<String>) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showDepositDialog by remember { mutableStateOf(false) }
    var selectedGhostIds by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TrapBlack)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "👻 GHOST TRAP",
                style = MaterialTheme.typography.titleMedium,
                color = TrapYellow
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onDepositGhosts != null && trappedGhosts.isNotEmpty()) {
                    Button(
                        onClick = { showDepositDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TrapYellow,
                            contentColor = TrapBlack
                        )
                    ) {
                        Text("Deposit")
                    }
                }

                IconButton(onClick = { showAddDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Trap Ghost",
                        tint = TrapYellow
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (trappedGhosts.isEmpty()) {
            Text(
                text = "No ghosts trapped yet",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 200.dp)
            ) {
                items(trappedGhosts) { ghost ->
                    GhostItem(
                        ghost = ghost,
                        onRemove = { onRemoveGhost(ghost.ghostId) }
                    )
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddGhostDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { rating ->
                onTrapGhost(rating)
                showAddDialog = false
            }
        )
    }

    if (showDepositDialog && onDepositGhosts != null) {
        DepositGhostsDialog(
            trappedGhosts = trappedGhosts,
            selectedGhostIds = selectedGhostIds,
            onSelectionChange = { selectedGhostIds = it },
            onDismiss = {
                showDepositDialog = false
                selectedGhostIds = setOf()
            },
            onConfirm = {
                onDepositGhosts(selectedGhostIds.toList())
                showDepositDialog = false
                selectedGhostIds = setOf()
            }
        )
    }
}

@Composable
private fun GhostItem(
    ghost: TrappedGhostData,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TrapYellow.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = ghost.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TrapYellow
                )
                Text(
                    text = "Rating: ${ghost.rating}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TrapYellow.copy(alpha = 0.7f)
                )
            }
            
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Release Ghost",
                    tint = TrapYellow
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddGhostDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selectedRating by remember { mutableStateOf(1) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Trap Ghost") },
        text = {
            Column {
                Text("Select Ghost Rating:")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(1, 2, 3).forEach { rating ->
                        FilterChip(
                            selected = selectedRating == rating,
                            onClick = { selectedRating = rating },
                            label = { Text("Rating $rating") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedRating) }) {
                Text("Trap")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DepositGhostsDialog(
    trappedGhosts: List<TrappedGhostData>,
    selectedGhostIds: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val totalRating = trappedGhosts
        .filter { it.ghostId in selectedGhostIds }
        .sumOf { it.rating }
    val xpGain = totalRating / 3

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Deposit Ghosts") },
        text = {
            Column {
                Text("Select ghosts to deposit:")
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(trappedGhosts) { ghost ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (ghost.ghostId in selectedGhostIds)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = ghost.ghostId in selectedGhostIds,
                                    onCheckedChange = { checked ->
                                        onSelectionChange(
                                            if (checked) {
                                                selectedGhostIds + ghost.ghostId
                                            } else {
                                                selectedGhostIds - ghost.ghostId
                                            }
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = ghost.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Rating: ${ghost.rating}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedGhostIds.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Total Rating: $totalRating",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "XP Gain: $xpGain (Total ÷ 3)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = selectedGhostIds.isNotEmpty()
            ) {
                Text("Deposit (${selectedGhostIds.size})")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

