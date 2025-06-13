package com.example.diarydepresiku.ui // Pastikan package ini sesuai dengan struktur folder Anda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api // <<< PENTING: Untuk Material3 API tertentu (jika ada)
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Diperlukan untuk Preview ViewModel
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.FilterChip
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import com.example.diarydepresiku.DiaryViewModel
import com.example.diarydepresiku.DiaryViewModelFactory // Pastikan ini diimpor
import com.example.diarydepresiku.MyApplication // Pastikan ini diimpor
import com.example.diarydepresiku.ContentViewModel
import com.example.diarydepresiku.ui.theme.DiarydepresikuTheme // Pastikan ini diimpor
import com.example.diarydepresiku.ui.theme.SoftYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect
import com.example.diarydepresiku.ContentViewModelFactory


// Daftar pilihan mood yang tersedia - Pindahkan di sini atau di file tersendiri
val moodOptions = listOf("Senang", "Cemas", "Sedih", "Marah")
val activityOptions = listOf("Olahraga", "Membaca", "Bersosialisasi", "Bekerja", "Meditasi")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryFormScreen(
    viewModel: DiaryViewModel,
    contentViewModel: ContentViewModel,
    modifier: Modifier = Modifier,
    onNavigateToContent: (() -> Unit)? = null
) {
    var diaryText by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf(moodOptions[0]) }
    val selectedActivities = remember { mutableStateListOf<String>() }

    val haptic = LocalHapticFeedback.current

    val diaryEntries by viewModel.diaryEntries.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val analysisResult by viewModel.analysisResult.collectAsState()
    val articles by contentViewModel.articles.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()) }
    LaunchedEffect(analysisResult) {
        if (analysisResult != null) {
            contentViewModel.refreshArticles(filterMood = analysisResult)
        }
    }
    val prefs = (LocalContext.current.applicationContext as MyApplication).reminderPreferences
    val userName by prefs.userName.collectAsState(initial = "")

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (userName.isNotBlank()) {
            Text(
                text = "Halo, $userName!",
                style = MaterialTheme.typography.titleLarge
            )
        }
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
        MoodSlider(
            selectedMood = selectedMood,
            onMoodChange = { selectedMood = it },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Aktivitas:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            activityOptions.forEach { act ->
                FilterChip(
                    selected = selectedActivities.contains(act),
                    onClick = {
                        if (selectedActivities.contains(act)) {
                            selectedActivities.remove(act)
                        } else {
                            selectedActivities.add(act)
                        }
                    },
                    label = { Text(act) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (diaryText.isNotBlank()) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.saveEntry(diaryText, selectedMood, selectedActivities.toList())
                    diaryText = ""
                    selectedMood = moodOptions[0]
                    selectedActivities.clear()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = diaryText.isNotBlank()
        ) {
            Text(text = "Simpan Entri")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                tint = SoftYellow
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.privacy_message),
                color = SoftYellow,
                style = MaterialTheme.typography.labelSmall
            )
        }

        statusMessage?.let { message ->
            if (message.contains("offline", ignoreCase = true)) {
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    Icon(Icons.Filled.CloudOff, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                Text(
                    text = message,
                    color = if (message.contains("gagal", ignoreCase = true)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                // Button navigasi dipindahkan ke dialog hasil analisis
            }
        }

        if (analysisResult != null) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(text = "Mood Dominan", style = MaterialTheme.typography.titleSmall)
                Text(analysisResult!!, modifier = Modifier.padding(top = 4.dp))
                Spacer(Modifier.height(8.dp))
                articles.take(3).forEach { article ->
                    article.title?.let { Text("\u2022 $it", style = MaterialTheme.typography.bodySmall) }
                }
                Button(
                    onClick = {
                        viewModel.clearAnalysisResult()
                        onNavigateToContent?.invoke()
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) { Text("Lihat Artikel") }
            }
        } else {
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
        val diaryVm: DiaryViewModel = viewModel(factory = factory)
        val contentFactory = ContentViewModelFactory(
            repository = context.contentRepository,
            diaryViewModel = diaryVm
        )
        val contentVm: ContentViewModel = viewModel(factory = contentFactory)
        DiarydepresikuTheme {
            DiaryFormScreen(
                viewModel = diaryVm,
                contentViewModel = contentVm
            )
        }
    } else {
        DiarydepresikuTheme {
            androidx.compose.material3.Text("Preview unavailable")
        }
    }
}
