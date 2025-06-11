package com.example.diarydepresiku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import android.app.Application
import androidx.compose.ui.platform.LocalContext // <<< PENTING: Import ini untuk LocalContext

// PENTING: Impor file tema Anda dari package yang benar
import com.example.diarydepresiku.ui.theme.DiarydepresikuTheme // <<< TAMBAHKAN BARIS INI

import java.text.SimpleDateFormat // Sudah ada
import java.util.Date // Sudah ada
import java.util.Locale // Sudah ada

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            // Akses Application context DARI DALAM Composable
            // LocalContext.current harus dipanggil dalam fungsi @Composable.
            // Gunakan 'applicationContext' dari LocalContext dan cast ke 'MyApplication'.
            val application = LocalContext.current.applicationContext as MyApplication

            // Inisialisasi ViewModelFactory Anda, melewati instance 'application'
            val factory = DiaryViewModelFactory(application = application)

            // Mengambil instance ViewModel menggunakan factory.
            // Panggilan viewModel() ini sekarang berada dalam konteks Composable yang benar.
            val diaryViewModel: DiaryViewModel = viewModel(factory = factory)

            // Membungkus seluruh UI dengan tema aplikasi Anda
            DiarydepresikuTheme {
                // Menggunakan Scaffold untuk struktur dasar Material Design
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DiaryFormScreen(
                        viewModel = diaryViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// Daftar pilihan mood yang tersedia
val moodOptions = listOf("Senang", "Tersipu", "Sedih", "Cemas", "Marah")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryFormScreen(
    viewModel: DiaryViewModel,
    modifier: Modifier = Modifier
) {
    var diaryText by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf(moodOptions[0]) }

    val diaryEntries by viewModel.diaryEntries.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()) }

    Column(
        modifier = modifier
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
        // Menggunakan FlowRow untuk layout yang lebih fleksibel
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            moodOptions.forEach { mood ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { selectedMood = mood }
                ) {
                    RadioButton(
                        selected = (mood == selectedMood),
                        onClick = { selectedMood = mood }
                    )
                    Text(text = mood)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (diaryText.isNotBlank()) {
                    viewModel.saveEntry(diaryText, selectedMood)
                    diaryText = ""
                    selectedMood = moodOptions[0]
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

// Preview composable
@Preview(showBackground = true, widthDp = 320)
@Composable
fun DiaryFormScreenPreview() {
    // Untuk preview, ViewModel harus diinisialisasi dengan Application context
    // Anda bisa membuat instance dummy dari MyApplication jika tidak ada.
    // Atau lebih baik, buat ViewModelFactory yang bisa menerima Context biasa untuk Preview,
    // atau gunakan teknik lain seperti hilt-android-testing untuk preview.
    // Namun, cara termudah adalah menggunakan LocalContext.current.applicationContext
    // dan mengasumsikan itu adalah instance MyApplication.
    val application = LocalContext.current.applicationContext as MyApplication
    val factory = DiaryViewModelFactory(application = application)

    DiarydepresikuTheme {
        DiaryFormScreen(viewModel = viewModel(factory = factory))
    }
}