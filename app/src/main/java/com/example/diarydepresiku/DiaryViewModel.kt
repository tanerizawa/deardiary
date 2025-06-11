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
import java.util.concurrent.TimeUnit

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

    // StateFlow untuk frekuensi mood mingguan dan bulanan
    val weeklyMoodFrequency: StateFlow<Map<String, Int>> = diaryEntries
        .map { entries -> computeMoodFrequency(entries, 7) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val monthlyMoodFrequency: StateFlow<Map<String, Int>> = diaryEntries
        .map { entries -> computeMoodFrequency(entries, 30) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // Kata kunci umum untuk tiap mood dari isi entri
    val moodKeywords: StateFlow<Map<String, List<String>>> = diaryEntries
        .map { entries -> computeKeywords(entries) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // Inisialisasi pengambilan statistik mood dari backend
    init {
        viewModelScope.launch {
            val stats = repository.getMoodStats()
            if (stats != null) {
                _moodCounts.value = stats
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
                // Perbarui statistik mood setelah menambah entri
                val stats = repository.getMoodStats()
                if (stats != null) {
                    _moodCounts.value = stats
                }
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

    /** Hitung frekuensi mood dalam rentang hari tertentu */
    private fun computeMoodFrequency(entries: List<DiaryEntry>, days: Int): Map<String, Int> {
        val cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days.toLong())
        return entries
            .filter { it.creationTimestamp >= cutoff }
            .groupingBy { it.mood }
            .eachCount()
    }

    /**
     * Mengambil kata kunci yang paling sering muncul untuk tiap mood.
     * Kata dengan panjang < 4 karakter diabaikan agar tidak mendominasi hasil.
     */
    private fun computeKeywords(entries: List<DiaryEntry>): Map<String, List<String>> {
        val moodWordCounts = mutableMapOf<String, MutableMap<String, Int>>()
        entries.forEach { entry ->
            val words = entry.content
                .lowercase()
                .split(Regex("\\W+"))
                .filter { it.length >= 4 }
            val counts = moodWordCounts.getOrPut(entry.mood) { mutableMapOf() }
            for (word in words) {
                counts[word] = counts.getOrDefault(word, 0) + 1
            }
        }
        return moodWordCounts.mapValues { (_, counts) ->
            counts.entries
                .sortedByDescending { it.value }
                .take(5)
                .map { it.key }
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
