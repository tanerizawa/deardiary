package com.example.diarydepresiku.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarydepresiku.ui.theme.*

@Composable
fun MoodSummaryWidget(
    moodCounts: Map<String, Int>,
    totalEntries: Int,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    val dominantMood = moodCounts.maxByOrNull { it.value }?.key ?: "neutral"
    val dominantMoodCount = moodCounts[dominantMood] ?: 0
    val percentage = if (totalEntries > 0) (dominantMoodCount.toFloat() / totalEntries.toFloat() * 100).toInt() else 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(SoftBlueDark, MintDark)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TrendingUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "Mood Summary",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "7 hari terakhir",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                Text(
                    text = "$totalEntries catatan",
                    style = MaterialTheme.typography.labelMedium,
                    color = SoftBlueDark,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (totalEntries > 0) {
                // Dominant mood display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mood emoji and info
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = getMoodEmoji(dominantMood),
                                fontSize = 32.sp
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column {
                                Text(
                                    text = getMoodLabel(dominantMood),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = getMoodColor(dominantMood),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Mood paling sering",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    // Percentage
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = getMoodColor(dominantMood).copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.titleLarge,
                            color = getMoodColor(dominantMood),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                // Expandable detailed view
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Divider(color = Gray200)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Detail Mood",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Mood breakdown
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            moodCounts.filter { it.value > 0 }.forEach { (mood, count) ->
                                MoodBreakdownItem(
                                    mood = mood,
                                    count = count,
                                    total = totalEntries
                                )
                            }
                        }
                    }
                }

                // Expand/Collapse button
                Spacer(modifier = Modifier.height(12.dp))
                
                TextButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = SoftBlueDark
                    )
                ) {
                    Text(
                        text = if (isExpanded) "Sembunyikan Detail" else "Lihat Detail",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                // Empty state
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📝",
                        fontSize = 32.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Belum ada catatan mood",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Mulai menulis diary untuk melihat summary",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextHint,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun MoodBreakdownItem(
    mood: String,
    count: Int,
    total: Int
) {
    val percentage = (count.toFloat() / total.toFloat())
    val moodColor = getMoodColor(mood)
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mood info
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = getMoodEmoji(mood),
                fontSize = 16.sp
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = getMoodLabel(mood),
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary
            )
        }
        
        // Count and progress
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.width(20.dp),
                textAlign = TextAlign.End
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            LinearProgressIndicator(
                progress = { percentage },
                modifier = Modifier
                    .width(60.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = moodColor,
                trackColor = moodColor.copy(alpha = 0.2f)
            )
        }
    }
}

// Helper functions
private fun getMoodColor(mood: String): Color {
    return when (mood) {
        "very_happy" -> MoodVeryHappy
        "happy" -> MoodHappy
        "neutral" -> MoodNeutral
        "sad" -> MoodSad
        "very_sad" -> MoodVerySad
        else -> Gray400
    }
}

private fun getMoodEmoji(mood: String): String {
    return when (mood) {
        "very_happy" -> "😄"
        "happy" -> "😊"
        "neutral" -> "😐"
        "sad" -> "😢"
        "very_sad" -> "😭"
        else -> "😐"
    }
}

private fun getMoodLabel(mood: String): String {
    return when (mood) {
        "very_happy" -> "Sangat Bahagia"
        "happy" -> "Bahagia"
        "neutral" -> "Netral"
        "sad" -> "Sedih"
        "very_sad" -> "Sangat Sedih"
        else -> "Netral"
    }
}
