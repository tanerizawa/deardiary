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
import java.time.Instant
import java.time.ZoneId
import java.time.LocalDate
import com.example.diarydepresiku.EntryStatus

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

    private val _analysisResult = MutableStateFlow<String?>(null)
    val analysisResult: StateFlow<String?> = _analysisResult.asStateFlow()

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

    // Streak harian beruntun
    val entryStreak: StateFlow<Int> = diaryEntries
        .map { entries -> computeStreak(entries) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Pencapaian yang telah diperoleh
    val achievements: StateFlow<List<Achievement>> = repository.getAchievements()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Inisialisasi pengambilan statistik mood dari backend
    init {
        viewModelScope.launch {
            val stats = repository.getMoodStats()
            if (stats != null) {
                _moodCounts.value = stats
            }
        }

        // Award badges berdasarkan streak
        viewModelScope.launch {
            entryStreak.collect { streak ->
                if (streak >= 30 && achievements.value.none { it.name == "30 Day Streak" }) {
                    repository.addAchievement("30 Day Streak")
                } else if (streak >= 5 && achievements.value.none { it.name == "5 Day Streak" }) {
                    repository.addAchievement("5 Day Streak")
                }
            }
        }
    }
    // <<< AKHIR TAMBAHAN UNTUK ANALISIS MOOD >>>


    /**
     * Fungsi untuk menyimpan entri diary baru.
     * Dipanggil dari UI dengan content dan mood.
     * Menggunakan coroutine untuk menjalankan operasi I/O di background.
     */
    fun saveEntry(content: String, mood: String, activities: List<String>) {
        // Meluncurkan coroutine dalam viewModelScope
        viewModelScope.launch {
            try {
                val status = repository.addEntry(content, mood, activities)
                _statusMessage.value = if (status == EntryStatus.ONLINE) {
                    "Entri berhasil disimpan!"
                } else {
                    "Entri disimpan offline"
                }
                _analysisResult.value = repository.analyzeEntry(content)
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

    fun clearAnalysisResult() {
        _analysisResult.value = null
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

    /** Hitung streak harian berturut-turut */
    private fun computeStreak(entries: List<DiaryEntry>): Int {
        if (entries.isEmpty()) return 0
        val sorted = entries.sortedByDescending { it.creationTimestamp }
        var streak = 0
        var lastDay: LocalDate? = null
        for (entry in sorted) {
            val day = Instant.ofEpochMilli(entry.creationTimestamp)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            if (lastDay == null) {
                streak = 1
                lastDay = day
                continue
            }
            val diff = lastDay!!.toEpochDay() - day.toEpochDay()
            if (diff == 0L) continue
            if (diff == 1L) {
                streak++
                lastDay = day
            } else {
                break
            }
        }
        return streak
    }

}
