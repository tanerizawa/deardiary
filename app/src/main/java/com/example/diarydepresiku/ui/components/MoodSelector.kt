package com.example.diarydepresiku.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarydepresiku.ui.theme.*

data class MoodOption(
    val value: String,
    val emoji: String,
    val label: String,
    val color: Color,
    val description: String
)

@Composable
fun MoodSelector(
    selectedMood: String?,
    onMoodSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val moodOptions = listOf(
        MoodOption(
            value = "very_happy",
            emoji = "😄",
            label = "Sangat Bahagia",
            color = MoodVeryHappy,
            description = "Merasa luar biasa bahagia dan energik"
        ),
        MoodOption(
            value = "happy",
            emoji = "😊",
            label = "Bahagia",
            color = MoodHappy,
            description = "Merasa senang dan puas"
        ),
        MoodOption(
            value = "neutral",
            emoji = "😐",
            label = "Netral",
            color = MoodNeutral,
            description = "Perasaan biasa-biasa saja"
        ),
        MoodOption(
            value = "sad",
            emoji = "😢",
            label = "Sedih",
            color = MoodSad,
            description = "Merasa sedih atau kecewa"
        ),
        MoodOption(
            value = "very_sad",
            emoji = "😭",
            label = "Sangat Sedih",
            color = MoodVerySad,
            description = "Merasa sangat sedih atau tertekan"
        )
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bagaimana perasaan Anda hari ini?",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(moodOptions.size) { index ->
                val mood = moodOptions[index]
                MoodButton(
                    mood = mood,
                    isSelected = selectedMood == mood.value,
                    onClick = { onMoodSelected(mood.value) }
                )
            }
        }

        // Show description for selected mood
        selectedMood?.let { mood ->
            val selectedMoodOption = moodOptions.find { it.value == mood }
            selectedMoodOption?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = it.color.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = it.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MoodButton(
    mood: MoodOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) mood.color.copy(alpha = 0.2f) else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "backgroundColor"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) mood.color else Gray200,
        animationSpec = tween(durationMillis = 200),
        label = "borderColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable { onClick() }
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
            .width(80.dp)
    ) {
        // Emoji circle
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (isSelected) mood.color else Gray100,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = mood.emoji,
                fontSize = 24.sp,
                modifier = Modifier
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = mood.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) mood.color else TextSecondary,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
}
