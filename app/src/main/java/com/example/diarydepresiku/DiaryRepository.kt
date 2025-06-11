package com.example.diarydepresiku

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit // Untuk penggunaan waktu yang lebih jelas

// Repository bertanggung jawab untuk abstraksi akses data (lokal & remote)
// Menerima DAO dan API service sebagai parameter constructor (praktik terbaik)
class DiaryRepository(
    private val diaryDao: DiaryDao, // DAO disuntikkan
    private val diaryApi: DiaryApi // API service disuntikkan
) {

    // Fungsi untuk menambahkan entri baru (lokal & remote)
    suspend fun addEntry(content: String, mood: String) {
        // Ambil timestamp saat ini dalam milidetik (Long)
        val currentTimestamp = System.currentTimeMillis()

        // 1. Simpan ke database lokal (Room)
        // Pastikan nama parameter cocok dengan DiaryEntry (content, mood, creationTimestamp)
        val localEntry = DiaryEntry(
            content = content,
            mood = mood,
            creationTimestamp = currentTimestamp
        )

        withContext(Dispatchers.IO) { // Pastikan operasi DB berjalan di IO Dispatcher
            diaryDao.insertEntry(localEntry)
            println("Entry saved locally: $localEntry") // Untuk debugging
        }

        // 2. Coba kirim ke server melalui API
        val remoteRequest = DiaryEntryRequest(
            content = content,
            mood = mood,
            timestamp = currentTimestamp // Gunakan timestamp yang sama
        )

        withContext(Dispatchers.IO) { // Pastikan operasi jaringan berjalan di IO Dispatcher
            try {
                val response = diaryApi.postEntry(remoteRequest)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    println("Entry sent to server successfully: $responseBody")
                    // Logika tambahan:
                    // Jika server mengembalikan ID atau status, Anda bisa mengupdate entri lokal.
                    // Misalnya, menandai entri lokal sebagai 'synced' atau 'uploaded'.
                } else {
                    // Penanganan kesalahan dari server (misal kode HTTP 4xx, 5xx)
                    val errorBody = response.errorBody()?.string()
                    println("Failed to send entry to server: ${response.code()} - $errorBody")
                    // TODO: Implementasi logika pengiriman ulang (retry mechanism)
                    // Misalnya, simpan entri ini ke tabel "pending_uploads" atau tandai di DiaryEntry.
                }
            } catch (e: Exception) {
                // Penanganan kesalahan jaringan (misal tidak ada internet, timeout)
                println("Network error sending entry: ${e.message}")
                // TODO: Implementasi logika pengiriman ulang (retry mechanism)
                // Misalnya, gunakan WorkManager untuk menjadwalkan pengiriman ulang.
            }
        }
    }

    /**
     * Mendapatkan semua entri diary dari database lokal.
     * Mengembalikan Flow, memungkinkan UI bereaksi terhadap perubahan data secara real-time.
     */
    fun getAllDiaryEntries(): Flow<List<DiaryEntry>> {
        return diaryDao.getAllEntries()
    }

    // TODO: Tambahkan fungsi lain untuk CRUD (update, delete, getById) jika diperlukan
    // suspend fun updateEntry(entry: DiaryEntry) = withContext(Dispatchers.IO) { diaryDao.updateEntry(entry) }
    // suspend fun deleteEntry(entry: DiaryEntry) = withContext(Dispatchers.IO) { diaryDao.deleteEntry(entry) }
    // suspend fun getEntryById(id: Int) = withContext(Dispatchers.IO) { diaryDao.getEntryById(id) }
}