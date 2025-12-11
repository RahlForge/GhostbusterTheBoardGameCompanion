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
import com.ghostbusters.companion.domain.model.GameType
import com.ghostbusters.companion.ui.theme.SlimeGreen
import com.ghostbusters.companion.ui.theme.SlimePink

@Composable
fun ProtonStreamTokens(
    color: Color,
    usedStreams: List<Boolean>,
    onToggle: (Int) -> Unit,
    onTrapIt: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeStreamCount = usedStreams.count { it }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Proton Streams",
                style = MaterialTheme.typography.titleMedium
            )

            androidx.compose.material3.Button(
                onClick = onTrapIt,
                enabled = activeStreamCount > 0,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = color,
                    disabledContainerColor = color.copy(alpha = 0.3f)
                )
            ) {
                Text("Trap It!")
            }
        }

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

        if (activeStreamCount > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$activeStreamCount stream${if (activeStreamCount > 1) "s" else ""} active",
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
    }
}

@Composable
fun ActionSlimeTokens(
    actionCount: Int,
    usedActions: List<Boolean>,
    gameType: GameType,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Choose slime color based on game type
    val slimeColor = when (gameType) {
        GameType.GHOSTBUSTERS -> SlimeGreen // Green for GB1
        GameType.GHOSTBUSTERS_II -> SlimePink // Pink for GB2
    }

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
                    if (usedActions[index]) {
                        // Slime state - colored circle
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Slime ${index + 1}",
                            tint = slimeColor,
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        // Action state - lightning bolt emoji
                        Text(
                            text = "⚡",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                }
            }
        }
    }
}

