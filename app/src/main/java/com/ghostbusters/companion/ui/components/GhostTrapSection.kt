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

@Composable
fun GhostTrapSection(
    trappedGhosts: List<TrappedGhostData>,
    onTrapGhost: (Int) -> Unit,
    onRemoveGhost: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    
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
            
            IconButton(onClick = { showAddDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Trap Ghost",
                    tint = TrapYellow
                )
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

