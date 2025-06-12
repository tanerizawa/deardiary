package com.example.diarydepresiku.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diarydepresiku.ContentViewModel
import com.example.diarydepresiku.DiaryViewModel

@Composable
fun HomeScreen(
    diaryViewModel: DiaryViewModel,
    contentViewModel: ContentViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) { contentViewModel.refreshArticles() }

    val moodCounts = diaryViewModel.moodCounts.collectAsState().value
    val weeklyFreq = diaryViewModel.weeklyMoodFrequency.collectAsState().value
    val monthlyFreq = diaryViewModel.monthlyMoodFrequency.collectAsState().value
    val analysis = diaryViewModel.analysisResult.collectAsState().value

    LaunchedEffect(analysis) {
        analysis?.let { contentViewModel.refreshArticles(filterMood = it) }
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
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

        ArticleList(viewModel = contentViewModel)
    }
}
