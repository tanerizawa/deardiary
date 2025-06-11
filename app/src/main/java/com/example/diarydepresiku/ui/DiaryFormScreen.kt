package com.example.diarydepresiku.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diarydepresiku.DiaryViewModel
import com.example.diarydepresiku.moodOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DiaryFormScreen(viewModel: DiaryViewModel, modifier: Modifier = Modifier) {
    var diaryText by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf(moodOptions[0]) }

    // Mengamati daftar entri dari ViewModel
    val diaryEntries by viewModel.diaryEntries.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()) }

    Column(modifier = modifier.padding(16.dp)) {
        // ... (UI Anda untuk form input) ...

        Button(onClick = {
            if (diaryText.isNotBlank()) {
                viewModel.saveEntry(diaryText, selectedMood)
                diaryText = ""
                selectedMood = moodOptions[0]
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Simpan Entri")
        }

        // Menampilkan pesan status/error
        statusMessage?.let { message ->
            Text(
                text = message,
                color = if (message.contains("gagal", ignoreCase = true)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
        Text(text = "Entri Terbaru:", style = MaterialTheme.typography.titleSmall)
        if (diaryEntries.isEmpty()) {
            Text("Belum ada entri.")
        } else {
            Column {
                diaryEntries.takeLast(3).forEach { entry -> // Tampilkan 3 entri terakhir
                    val date = dateFormat.format(Date(entry.creationTimestamp))
                    Text("($date) Mood: ${entry.mood} - ${entry.content.take(50)}...")
                }
            }
        }
    }
}