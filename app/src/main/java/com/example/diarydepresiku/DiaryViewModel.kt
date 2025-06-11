package com.example.diarydepresiku

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers // Mungkin tidak langsung terpakai di sini, tapi bagus untuk ada
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map // Diperlukan untuk transformasi Flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext // Mungkin tidak langsung terpakai di sini

/**
 * DiaryViewModel: Mengelola UI state dan berinteraksi dengan Repository untuk operasi data.
 * Menggunakan AndroidViewModel untuk akses ke Application context.
 *
 * @param application Instance dari Application class (diperlukan oleh AndroidViewModel).
 */
class DiaryViewModel(application: Application) : AndroidViewModel(application) {

    // Inisialisasi repository menggunakan instance yang disediakan oleh Application
    // Ini adalah bentuk sederhana dari Dependency Injection
    // Pastikan MyApplication mengekspos properti 'diaryRepository'
    private val repository: DiaryRepository = (application as MyApplication).diaryRepository

    // UI State: Mengelola daftar entri diary yang akan diobservasi oleh UI
    val diaryEntries: StateFlow<List<DiaryEntry>> =
        repository.getAllEntries() // <<< KOREKSI: Gunakan getAllEntries()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // State untuk pesan status atau error yang bisa ditampilkan ke UI
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // <<< TAMBAHAN UNTUK ANALISIS MOOD >>>
    // State untuk menampung hitungan mood (Map<MoodName, Count>)
    private val _moodCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val moodCounts: StateFlow<Map<String, Int>> = _moodCounts.asStateFlow()

    // Inisialisasi logika pengumpulan dan penghitungan mood
    init {
        viewModelScope.launch {
            repository.getAllEntries().collect { entries ->
                // Peta untuk menyimpan hitungan setiap mood
                val counts = mutableMapOf<String, Int>()
                for (entry in entries) {
                    counts[entry.mood] = (counts[entry.mood] ?: 0) + 1
                }
                _moodCounts.value = counts
            }
        }
    }
    // <<< AKHIR TAMBAHAN UNTUK ANALISIS MOOD >>>


    /**
     * Fungsi untuk menyimpan entri diary baru.
     * Dipanggil dari UI dengan content dan mood.
     * Menggunakan coroutine untuk menjalankan operasi I/O di background.
     */
    fun saveEntry(content: String, mood: String) {
        // Meluncurkan coroutine dalam viewModelScope
        viewModelScope.launch {
            try {
                repository.addEntry(content, mood) // <<< KOREKSI: Panggil addEntry di repository
                // Memberikan feedback sukses ke UI
                _statusMessage.value = "Entri berhasil disimpan!"
                // Reset pesan setelah beberapa waktu jika diperlukan
                launch {
                    kotlinx.coroutines.delay(3000) // Tunda 3 detik
                    _statusMessage.value = null // Bersihkan pesan
                }
            } catch (e: Exception) {
                // Memberikan feedback error ke UI
                _statusMessage.value = "Gagal menyimpan entri: ${e.localizedMessage}"
                println("Error saving entry: ${e.stackTraceToString()}") // Log error lengkap
            }
        }
    }

    // TODO: Tambahkan fungsi lain untuk operasi CRUD (update, delete, getById) jika diperlukan
    /*
    fun updateEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            try {
                repository.updateEntry(entry)
                _statusMessage.value = "Entri berhasil diperbarui!"
            } catch (e: Exception) {
                _statusMessage.value = "Gagal memperbarui entri: ${e.localizedMessage}"
            }
        }
    }

    fun deleteEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            try {
                repository.deleteEntry(entry)
                _statusMessage.value = "Entri berhasil dihapus!"
            } catch (e: Exception) {
                _statusMessage.value = "Gagal menghapus entri: ${e.localizedMessage}"
            }
        }
    }
    */
}