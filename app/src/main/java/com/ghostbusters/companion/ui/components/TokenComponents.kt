package com.ghostbusters.companion.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProtonStreamTokens(
    color: Color,
    usedStreams: List<Boolean>,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Proton Streams",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            usedStreams.forEachIndexed { index, isUsed ->
                Icon(
                    imageVector = if (isUsed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Proton Stream ${index + 1}",
                    tint = if (isUsed) color.copy(alpha = 0.5f) else color,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onToggle(index) }
                )
            }
        }
    }
}

@Composable
fun ActionSlimeTokens(
    actionCount: Int,
    usedActions: List<Boolean>,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Actions / Slime",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(actionCount) { index ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onToggle(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (usedActions[index]) "💧" else "⚡",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }
    }
}

