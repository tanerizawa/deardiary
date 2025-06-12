package com.example.diarydepresiku.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.diarydepresiku.DiaryEntry
import com.example.diarydepresiku.DiaryViewModel
import com.example.diarydepresiku.ui.theme.*

@Composable
fun HistoryScreen(
    viewModel: DiaryViewModel,
    modifier: Modifier = Modifier
) {
    val entries by viewModel.allEntries.collectAsState()
    val moodCounts by viewModel.moodCounts.collectAsState()
    var selectedTab by remember { mutableStateOf("entries") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundGradientStart,
                        BackgroundGradientEnd
                    )
                )
            )
    ) {
        // Header
        HistoryHeader()

        // Tab selector
        TabSelector(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Content based on selected tab
        when (selectedTab) {
            "entries" -> {
                DiaryEntriesContent(
                    entries = entries,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                )
            }
            "analysis" -> {
                MoodAnalysisContent(
                    moodCounts = moodCounts,
                    entries = entries,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun HistoryHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Riwayat & Analisis",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Lihat perkembangan mood dan catatan Anda",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TabSelector(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TabButton(
            text = "Catatan Diary",
            icon = Icons.Filled.CalendarToday,
            isSelected = selectedTab == "entries",
            onClick = { onTabSelected("entries") },
            modifier = Modifier.weight(1f)
        )
        
        TabButton(
            text = "Analisis Mood",
            icon = Icons.Filled.TrendingUp,
            isSelected = selectedTab == "analysis",
            onClick = { onTabSelected("analysis") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TabButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) SoftBlueDark else MaterialTheme.colorScheme.surface,
        animationSpec = tween(300),
        label = "backgroundColor"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else TextSecondary,
        animationSpec = tween(300),
        label = "contentColor"
    )

    Card(
        modifier = modifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun DiaryEntriesContent(
    entries: List<DiaryEntry>,
    modifier: Modifier = Modifier
) {
    if (entries.isEmpty()) {
        EmptyStateContent(
            title = "Belum ada catatan diary",
            description = "Mulai menulis catatan harian Anda untuk melihat riwayat di sini.",
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(entries.sortedByDescending { it.timestamp }) { entry ->
                DiaryEntryCard(entry = entry)
            }
        }
    }
}

@Composable
private fun DiaryEntryCard(
    entry: DiaryEntry
) {
    val moodColor = getMoodColor(entry.mood)
    val moodEmoji = getMoodEmoji(entry.mood)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with date and mood
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDateTime(entry.timestamp),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = moodColor.copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = moodEmoji,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = getMoodLabel(entry.mood),
                        style = MaterialTheme.typography.labelSmall,
                        color = moodColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Content
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun MoodAnalysisContent(
    moodCounts: Map<String, Int>,
    entries: List<DiaryEntry>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    if (entries.isEmpty()) {
        EmptyStateContent(
            title = "Belum ada data untuk analisis",
            description = "Tulis beberapa catatan diary untuk melihat analisis mood Anda.",
            modifier = modifier
        )
    } else {
        Column(
            modifier = modifier.verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mood distribution card
            MoodDistributionCard(moodCounts = moodCounts)
            
            // Insights card
            MoodInsightsCard(
                moodCounts = moodCounts,
                totalEntries = entries.size
            )
            
            // Recent trends (last 7 days)
            if (entries.size >= 3) {
                RecentTrendsCard(entries = entries)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MoodDistributionCard(
    moodCounts: Map<String, Int>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Insights,
                    contentDescription = null,
                    tint = SoftBlueDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Distribusi Mood",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val totalEntries = moodCounts.values.sum()
            
            if (totalEntries > 0) {
                moodCounts.forEach { (mood, count) ->
                    if (count > 0) {
                        MoodProgressBar(
                            mood = mood,
                            count = count,
                            total = totalEntries
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            } else {
                Text(
                    text = "Belum ada data mood",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun MoodProgressBar(
    mood: String,
    count: Int,
    total: Int
) {
    val percentage = (count.toFloat() / total.toFloat())
    val moodColor = getMoodColor(mood)
    val moodEmoji = getMoodEmoji(mood)
    val moodLabel = getMoodLabel(mood)
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = moodEmoji,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = moodLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
            }
            
            Text(
                text = "$count (${(percentage * 100).toInt()}%)",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = moodColor,
            trackColor = Gray200
        )
    }
}

@Composable
private fun MoodInsightsCard(
    moodCounts: Map<String, Int>,
    totalEntries: Int
) {
    val dominantMood = moodCounts.maxByOrNull { it.value }?.key
    val positiveCount = (moodCounts["very_happy"] ?: 0) + (moodCounts["happy"] ?: 0)
    val negativeCount = (moodCounts["sad"] ?: 0) + (moodCounts["very_sad"] ?: 0)
    val neutralCount = moodCounts["neutral"] ?: 0
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (positiveCount > negativeCount) SuccessLight else PeachLight
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "💡 Insight Mood Anda",
                style = MaterialTheme.typography.titleMedium,
                color = if (positiveCount > negativeCount) Success else PeachDark,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val insightText = when {
                positiveCount > negativeCount -> {
                    "Hebat! Mood positif mendominasi catatan Anda. Terus pertahankan aktivitas yang membuat bahagia! ✨"
                }
                negativeCount > positiveCount -> {
                    "Sepertinya Anda sedang menghadapi masa yang menantang. Ingat, berbagi cerita dengan orang terdekat bisa membantu. 💙"
                }
                else -> {
                    "Mood Anda cukup seimbang. Terus jaga keseimbangan emosi dengan aktivitas yang positif. 🌟"
                }
            }
            
            Text(
                text = insightText,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 20.sp
            )
            
            dominantMood?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mood paling sering: ${getMoodEmoji(it)} ${getMoodLabel(it)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = getMoodColor(it),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun RecentTrendsCard(
    entries: List<DiaryEntry>
) {
    val recentEntries = entries.sortedByDescending { it.timestamp }.take(7)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "📈 Tren 7 Hari Terakhir",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                recentEntries.forEach { entry ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = getMoodColor(entry.mood).copy(alpha = 0.2f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getMoodEmoji(entry.mood),
                                fontSize = 14.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = formatShortDate(entry.timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateContent(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📝",
            fontSize = 48.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
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

private fun formatDateTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale("id", "ID"))
    return formatter.format(dateTime)
}

private fun formatShortDate(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("d/M", Locale("id", "ID"))
    return formatter.format(dateTime)
}
