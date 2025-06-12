package com.example.diarydepresiku.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import kotlin.math.roundToInt

@Composable
fun MoodSlider(
    selectedMood: String,
    onMoodChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val emojis = listOf("😊", "😟", "😢", "😡")
    val moodNames = listOf("Senang", "Cemas", "Sedih", "Marah")
    var sliderPosition by remember { mutableFloatStateOf(moodNames.indexOf(selectedMood).coerceAtLeast(0).toFloat()) }

    val haptic = LocalHapticFeedback.current

    LaunchedEffect(selectedMood) {
        sliderPosition = moodNames.indexOf(selectedMood).coerceAtLeast(0).toFloat()
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            emojis.forEachIndexed { index, emoji ->
                val label = when (emoji) {
                    "😊" -> "Bahagia"
                    "😟" -> "Cemas"
                    "😢" -> "Sedih"
                    "😡" -> "Marah"
                    else -> ""
                }
                Text(
                    text = emoji,
                    modifier = Modifier.semantics { contentDescription = label }
                )
            }
        }
        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                val idx = it.roundToInt().coerceIn(emojis.indices)
                onMoodChange(moodNames[idx])
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },
            valueRange = 0f..(emojis.size - 1).toFloat(),
            steps = emojis.size - 2
        )
    }
}

