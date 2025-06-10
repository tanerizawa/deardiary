package com.example.diarydepresiku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge // Digunakan untuk edge-to-edge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.* // Menggunakan Material 3 components
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview // Untuk Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 1. Definisikan Data Class untuk Entri Diary
data class DiaryEntry(
    val id: Long = System.currentTimeMillis(), // ID unik untuk setiap entri
    val date: String,
    val content: String,
    val mood: String
)

// 2. Definisikan ViewModel untuk mengelola data dan logika bisnis
class DiaryViewModel : ViewModel() {
    // State untuk daftar entri diary
    private val _diaryEntries = mutableStateListOf<DiaryEntry>()
    val diaryEntries: List<DiaryEntry> = _diaryEntries // Hanya bisa dibaca dari luar

    fun saveEntry(content: String, mood: String) {
        val currentDate = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
        val newEntry = DiaryEntry(date = currentDate, content = content, mood = mood)
        _diaryEntries.add(newEntry)
        // Log atau simpan ke database/Shared Preferences di sini
        println("Entry saved: $newEntry") // Contoh: print ke logcat
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge() // Aktifkan edge-to-edge
        super.onCreate(savedInstanceState)
        setContent {
            // Membungkus seluruh UI dengan tema aplikasi
            DiarydepresikuTheme {
                // Mengambil instance ViewModel
                val diaryViewModel: DiaryViewModel = viewModel()
                // Memanggil fungsi composable utama untuk UI aplikasi
                // Menggunakan Scaffold untuk struktur dasar Material Design
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DiaryFormScreen(
                        viewModel = diaryViewModel,
                        modifier = Modifier.padding(innerPadding) // Penting untuk padding Scaffold
                    )
                }
            }
        }
    }
}

// Daftar pilihan mood yang tersedia
val moodOptions = listOf("Senang", "Tersipu", "Sedih", "Cemas", "Marah")

@OptIn(ExperimentalMaterial3Api::class) // Anotasi untuk OutlinedTextField (Material 3)
@Composable
fun DiaryFormScreen(
    viewModel: DiaryViewModel,
    modifier: Modifier = Modifier // Modifier ditambahkan sebagai parameter
) {
    // State untuk input teks diary
    var diaryText by remember { mutableStateOf("") } // Menggunakan `by` untuk sintaks yang lebih ringkas
    // State untuk pilihan mood (default ke mood pertama)
    var selectedMood by remember { mutableStateOf(moodOptions[0]) } // Menggunakan `by`

    // UI disusun secara vertikal (Column)
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(), // Mengisi lebar penuh
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Input teks untuk isi diary (dari Material 3)
        OutlinedTextField(
            value = diaryText,
            onValueChange = { diaryText = it },
            label = { Text("Isi Diary") },
            placeholder = { Text("Tuliskan apa yang Anda rasakan hari ini...") }, // Placeholder
            modifier = Modifier.fillMaxWidth(),
            minLines = 5, // Minimum 5 baris
            maxLines = 10 // Maksimum 10 baris
        )

        // Pilihan mood menggunakan RadioButton (dari Material 3)
        Text(
            text = "Mood Anda hari ini:",
            style = MaterialTheme.typography.titleMedium, // Style yang lebih sesuai
            modifier = Modifier.padding(top = 8.dp)
        )
        // Menampilkan setiap mood sebagai opsi yang bisa dipilih
        FlowRow( // Menggunakan FlowRow untuk layout yang lebih fleksibel
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            moodOptions.forEach { mood ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { selectedMood = mood } // Clickable di Row untuk area klik lebih besar
                ) {
                    RadioButton(
                        selected = (mood == selectedMood),
                        onClick = { selectedMood = mood }
                    )
                    // Label teks untuk RadioButton
                    Text(text = mood)
                }
            }
        }


        Spacer(Modifier.height(16.dp)) // Spasi tambahan sebelum tombol

        // Tombol Simpan untuk menyimpan entri diary (dari Material 3)
        Button(
            onClick = {
                if (diaryText.isNotBlank()) { // Validasi sederhana agar tidak menyimpan teks kosong
                    viewModel.saveEntry(diaryText, selectedMood)
                    // Membersihkan input setelah menyimpan
                    diaryText = ""
                    selectedMood = moodOptions[0] // Reset mood ke default
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = diaryText.isNotBlank() // Tombol aktif hanya jika ada teks
        ) {
            Text(text = "Simpan Entri")
        }

        // Contoh bagaimana menampilkan daftar entri (opsional, untuk debugging/demonstrasi)
        // Jika Anda ingin menampilkan daftar entri yang sudah disimpan, Anda bisa menambahkannya di sini.
        Spacer(Modifier.height(16.dp))
        Text(text = "Entri Terbaru:", style = MaterialTheme.typography.titleSmall)
        if (viewModel.diaryEntries.isEmpty()) {
            Text("Belum ada entri.")
        } else {
            Column {
                viewModel.diaryEntries.takeLast(3).forEach { entry -> // Tampilkan 3 entri terakhir
                    Text("(${entry.date}) Mood: ${entry.mood} - ${entry.content.take(50)}...")
                }
            }
        }
    }
}

// Preview composable
@Preview(showBackground = true, widthDp = 320)
@Composable
fun DiaryFormScreenPreview() {
    DiarydepresikuTheme {
        DiaryFormScreen(viewModel = DiaryViewModel())
    }
}