package com.example.diarydepresiku.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diarydepresiku.DiaryViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MoodCalendar(
    viewModel: DiaryViewModel,
    modifier: Modifier = Modifier
) {
    val entries by viewModel.diaryEntries.collectAsState()
    val moodMap = remember(entries) {
        entries.groupBy { entry ->
            Instant.ofEpochMilli(entry.creationTimestamp)
                .atZone(ZoneId.systemDefault()).toLocalDate()
        }.mapValues { (_, list) -> moodToEmoji(list.last().mood) }
    }

    val today = LocalDate.now()
    val month = YearMonth.of(today.year, today.month)
    val firstDay = month.atDay(1)
    val daysInMonth = month.lengthOfMonth()
    val offset = (firstDay.dayOfWeek.value % 7)

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = firstDay.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + month.year,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxWidth()) {
            items(offset) { Spacer(modifier = Modifier.size(40.dp)) }
            items(daysInMonth) { index ->
                val day = index + 1
                val date = month.atDay(day)
                val emoji = moodMap[date] ?: ""
                Card(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(40.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(text = day.toString())
                        Text(text = emoji)
                    }
                }
            }
        }
    }
}

private fun moodToEmoji(mood: String): String = when (mood) {
    "Senang" -> "😊"
    "Cemas" -> "😟"
    "Sedih" -> "😢"
    "Marah" -> "😡"
    else -> ""
}

