package com.example.diarydepresiku.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diarydepresiku.DiaryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: DiaryViewModel,
    modifier: Modifier = Modifier
) {
    val diaryEntries = viewModel.diaryEntries.collectAsState().value
    val dateFormat = remember { SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()) }

    Column(modifier = modifier.fillMaxSize()) {
        MoodCalendar(viewModel = viewModel) // diletakkan di luar LazyColumn

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(diaryEntries) { entry ->
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    val date = dateFormat.format(Date(entry.creationTimestamp))
                    Text(
                        text = "($date) Mood: ${entry.mood}",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(text = entry.content)
                }
                Divider()
            }
        }
    }
}
