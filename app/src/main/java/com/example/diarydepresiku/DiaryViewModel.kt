package com.example.diarydepresiku

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * DiaryViewModel: Mengelola UI state dan berinteraksi dengan Repository untuk operasi data.
 * Menggunakan AndroidViewModel untuk akses ke Application context.
 *
 * @param application Instance dari Application class (diperlukan oleh AndroidViewModel).
 */
class DiaryViewModel(application: Application) : AndroidViewModel(application) {

    // Inisialisasi repository menggunakan instance yang disediakan oleh Application
    // Ini adalah bentuk sederhana dari Dependency Injection
    private val repository: DiaryRepository = (application as MyApplication).diaryRepository

    // UI State: Mengelola daftar entri diary yang akan diobservasi oleh UI
    // Mengubah Flow dari repository menjadi StateFlow untuk observasi Compose
    val diaryEntries: StateFlow<List<DiaryEntry>> =
        repository.getAllDiaryEntries()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000), // Mulai berbagi data saat ada subscriber (UI)
                initialValue = emptyList() // Nilai awal sebelum data dimuat
            )

    // State untuk pesan status atau error yang bisa ditampilkan ke UI
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    /**
     * Fungsi untuk menyimpan entri diary baru.
     * Dipanggil dari UI dengan content dan mood.
     * Menggunakan coroutine untuk menjalankan operasi I/O di background.
     */
    fun saveEntry(content: String, mood: String) {
        // Meluncurkan coroutine dalam viewModelScope
        viewModelScope.launch {
            // Operasi Repository sudah suspend dan akan menggunakan IO Dispatcher yang sesuai
            try {
                repository.addEntry(content, mood)
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