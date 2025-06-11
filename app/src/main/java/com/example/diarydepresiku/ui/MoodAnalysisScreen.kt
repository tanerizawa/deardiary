package com.example.diarydepresiku.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diarydepresiku.DiaryViewModel

@Composable
fun MoodAnalysisScreen(
    viewModel: DiaryViewModel,
    modifier: Modifier = Modifier,
    onNavigateToContent: (() -> Unit)? = null
) {
    val moodCounts = viewModel.moodCounts.collectAsState().value
    val weeklyFreq = viewModel.weeklyMoodFrequency.collectAsState().value
    val monthlyFreq = viewModel.monthlyMoodFrequency.collectAsState().value

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Mood Frequencies", style = MaterialTheme.typography.titleMedium)
        BarChart(data = moodCounts)

        Text("Weekly Mood Frequency", style = MaterialTheme.typography.titleMedium)
        BarChart(data = weeklyFreq, barColor = MaterialTheme.colorScheme.secondary)

        Text("Monthly Mood Frequency", style = MaterialTheme.typography.titleMedium)
        BarChart(data = monthlyFreq, barColor = MaterialTheme.colorScheme.tertiary)

        onNavigateToContent?.let {
            Button(
                onClick = it,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lihat Artikel")
            }
        }
    }
}

@Composable
fun BarChart(
    data: Map<String, Int>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary
) {
    if (data.isEmpty()) {
        Text("No data available")
        return
    }

    val barWidth = 40.dp
    val spacing = 16.dp
    val maxValue = data.values.maxOrNull() ?: 0

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val barWidthPx = barWidth.toPx()
            val spacingPx = spacing.toPx()
            val totalChartWidth = data.size * barWidthPx + (data.size - 1) * spacingPx
            var currentX = (size.width - totalChartWidth) / 2f

            data.forEach { (_, value) ->
                val barHeight = if (maxValue == 0) 0f else size.height * (value / maxValue.toFloat())
                drawRect(
                    color = barColor,
                    topLeft = Offset(currentX, size.height - barHeight),
                    size = Size(barWidthPx, barHeight)
                )
                currentX += barWidthPx + spacingPx
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            data.forEach { (label, _) ->
                Box(Modifier.width(barWidth), contentAlignment = Alignment.Center) {
                    Text(label, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

