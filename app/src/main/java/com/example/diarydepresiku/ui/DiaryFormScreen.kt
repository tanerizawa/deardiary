package com.example.diarydepresiku.ui // Pastikan package ini sesuai dengan struktur folder Anda

import android.app.Application // Diperlukan untuk Preview ViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api // <<< PENTING: Untuk Material3 API tertentu (jika ada)
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Diperlukan untuk Preview ViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diarydepresiku.DiaryViewModel
import com.example.diarydepresiku.DiaryViewModelFactory // Pastikan ini diimpor
import com.example.diarydepresiku.MyApplication // Pastikan ini diimpor
import com.example.diarydepresiku.ui.theme.DiarydepresikuTheme // Pastikan ini diimpor
import com.example.diarydepresiku.ui.theme.Blue80
import com.example.diarydepresiku.ui.theme.RedSoft
import com.example.diarydepresiku.ui.theme.YellowSoft

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MoodItem(val emoji: String, val label: String, val color: Color)

val moodOptions = listOf(
    MoodItem("\uD83D\uDE0A", "Positif", YellowSoft),
    MoodItem("\uD83D\uDE1F", "Cemas", Blue80),
    MoodItem("\uD83D\uDE22", "Sedih", RedSoft),
    MoodItem("\uD83D\uDE21", "Marah", RedSoft)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryFormScreen(
    viewModel: DiaryViewModel,
    modifier: Modifier = Modifier,
    onNavigateToContent: (() -> Unit)? = null
) {
    var diaryText by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf(moodOptions[0].label) }

    val diaryEntries by viewModel.diaryEntries.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()) }

    Column(
        modifier = modifier
            .background(Blue80)
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = diaryText,
            onValueChange = { diaryText = it },
            label = { Text("Isi Diary") },
            placeholder = { Text("Tuliskan apa yang Anda rasakan hari ini...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 5,
            maxLines = 10
        )

        Text(
            text = "Mood Anda hari ini:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            moodOptions.forEach { item ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .background(item.color, CircleShape)
                        .clickable { selectedMood = item.label }
                        .padding(8.dp)
                ) {
                    Text(
                        text = item.emoji,
                        fontSize = 24.sp,
                        modifier = Modifier.semantics { contentDescription = item.label }
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Work,
                contentDescription = "Kerja",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(32.dp)
            )
            Icon(
                imageVector = Icons.Outlined.FitnessCenter,
                contentDescription = "Olahraga",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(32.dp)
            )
            Icon(
                imageVector = Icons.Outlined.Group,
                contentDescription = "Sosialisasi",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (diaryText.isNotBlank()) {
                    viewModel.saveEntry(diaryText, selectedMood)
                    diaryText = ""
                    selectedMood = moodOptions[0].label
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = diaryText.isNotBlank()
        ) {
            Text(text = "Simpan Entri")
        }

        statusMessage?.let { message ->
            Text(
                text = message,
                color = if (message.contains("gagal", ignoreCase = true)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
            if (!message.contains("gagal", ignoreCase = true)) {
                onNavigateToContent?.let {
                    Button(
                        onClick = it,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Text("Lihat Artikel")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(text = "Entri Terbaru:", style = MaterialTheme.typography.titleSmall)
        if (diaryEntries.isEmpty()) {
            Text("Belum ada entri.")
        } else {
            Column {
                diaryEntries.takeLast(3).forEach { entry ->
                    val date = dateFormat.format(Date(entry.creationTimestamp))
                    Text("($date) Mood: ${entry.mood} - ${entry.content.take(50)}...")
                }
            }
        }
    }
}

// Preview composable untuk DiaryFormScreen
@Preview(showBackground = true, widthDp = 320)
@Composable
fun DiaryFormScreenPreview() {
    // Untuk preview ViewModel yang membutuhkan Application context,
    // Anda bisa membuat instance dummy dari MyApplication atau menggunakan LocalContext.current.applicationContext
    // Perlu diingat bahwa LocalContext.current.applicationContext di preview mungkin tidak selalu valid
    // atau memberikan perilaku yang diharapkan untuk operasi database/jaringan.
    // Untuk preview yang lebih robust, pertimbangkan untuk menginject mock ViewModel.

    val context = LocalContext.current.applicationContext
    if (context is MyApplication) {
        val factory = DiaryViewModelFactory(application = context)
        DiarydepresikuTheme {
            DiaryFormScreen(viewModel = viewModel(factory = factory))
        }
    } else {
        DiarydepresikuTheme {
            androidx.compose.material3.Text("Preview unavailable")
        }
    }
}
