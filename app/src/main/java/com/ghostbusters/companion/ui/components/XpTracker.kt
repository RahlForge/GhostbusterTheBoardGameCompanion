package com.ghostbusters.companion.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ghostbusters.companion.domain.model.Level
import com.ghostbusters.companion.ui.theme.GhostbustersYellow

@Composable
fun XpTracker(
    currentXp: Int,
    onXpChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLevel = Level.fromXp(currentXp)
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "XP: $currentXp / 30",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Level ${currentLevel.ordinal + 1}",
            style = MaterialTheme.typography.titleMedium,
            color = GhostbustersYellow
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // XP Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val xpWidth = width / 30f
                
                // Draw XP segments
                for (i in 0 until 30) {
                    val x = i * xpWidth
                    val color = if (i < currentXp) {
                        when (Level.fromXp(i + 1)) {
                            Level.LEVEL_1 -> Color(0xFF4CAF50)
                            Level.LEVEL_2 -> Color(0xFF2196F3)
                            Level.LEVEL_3 -> Color(0xFF9C27B0)
                            Level.LEVEL_4 -> Color(0xFFFF9800)
                            Level.LEVEL_5 -> Color(0xFFF44336)
                        }
                    } else {
                        Color.Gray.copy(alpha = 0.3f)
                    }
                    
                    drawRect(
                        color = color,
                        topLeft = Offset(x + 1f, 0f),
                        size = androidx.compose.ui.geometry.Size(xpWidth - 2f, height)
                    )
                }
                
                // Draw level dividers
                val levelDividers = listOf(4, 10, 18, 29)
                levelDividers.forEach { xp ->
                    val x = xp * xpWidth
                    drawLine(
                        color = Color.Black,
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = 3f
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Level markers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Level.values().forEach { level ->
                Text(
                    text = "L${level.ordinal + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (currentLevel >= level) GhostbustersYellow else Color.Gray
                )
            }
        }
    }
}

