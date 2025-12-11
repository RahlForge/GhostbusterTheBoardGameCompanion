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
    slimeCount: Int,
    actionStates: List<Boolean>,
    characterColor: Color,
    onActionToggle: (Int) -> Unit,
    onAddSlime: () -> Unit,
    onRemoveSlime: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Actions",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Action tokens (lightning bolt / checkmark)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(actionCount) { index ->
                val isSlimed = index >= (actionCount - slimeCount)
                val isUsed = actionStates.getOrNull(index) ?: false

                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Base action icon (lightning bolt or checkmark)
                    if (isUsed) {
                        // Used action - show checkmark in character color
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Action Used ${index + 1}",
                            tint = characterColor,
                            modifier = Modifier
                                .size(48.dp)
                                .clickable(enabled = !isSlimed) {
                                    if (!isSlimed) {
                                        onActionToggle(index)
                                    }
                                }
                        )
                    } else {
                        // Unused action - show lightning bolt
                        Text(
                            text = "⚡",
                            style = MaterialTheme.typography.headlineLarge,
                            color = if (isSlimed) Color.Gray.copy(alpha = 0.3f) else Color.Unspecified,
                            modifier = Modifier.clickable(enabled = !isSlimed) {
                                if (!isSlimed) {
                                    onActionToggle(index)
                                }
                            }
                        )
                    }

                    // Show slime indicator overlaying the action if slimed
                    if (isSlimed) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Slimed",
                            tint = SlimeGreen,
                            modifier = Modifier
                                .size(32.dp)
                                .offset(x = 8.dp, y = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Slime controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            androidx.compose.material3.Button(
                onClick = onAddSlime,
                enabled = slimeCount < actionCount,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = SlimeGreen,
                    contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = SlimeGreen.copy(alpha = 0.3f)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("🧪 Slime")
            }

            androidx.compose.material3.Button(
                onClick = onRemoveSlime,
                enabled = slimeCount > 0,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary,
                    disabledContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("De-Slime")
            }
        }

        if (slimeCount > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$slimeCount slime${if (slimeCount > 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = SlimeGreen
            )
        }
    }
}

