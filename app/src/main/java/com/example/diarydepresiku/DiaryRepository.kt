package com.example.diarydepresiku

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
// import java.util.concurrent.TimeUnit // Hapus jika tidak digunakan

// Repository bertanggung jawab untuk abstraksi akses data (lokal & remote)
// Menerima DAO dan API service sebagai parameter constructor (praktik terbaik)
class DiaryRepository(
    private val diaryDao: DiaryDao,
    private val diaryApi: DiaryApi // API service disuntikkan
) {

    suspend fun addEntry(content: String, mood: String) {
        val currentTimestamp = System.currentTimeMillis()

        val localEntry = DiaryEntry(
            content = content,
            mood = mood,
            creationTimestamp = currentTimestamp
        )

        withContext(Dispatchers.IO) {
            diaryDao.insertEntry(localEntry)
            println("Entry saved locally: $localEntry")
        }

        // 2. Coba kirim ke server melalui API
        val remoteRequest = DiaryEntryRequest( // Membutuhkan definisi DiaryEntryRequest
            content = content,
            mood = mood,
            timestamp = currentTimestamp
        )

        withContext(Dispatchers.IO) {
            try {
                // Membutuhkan definisi DiaryApi dan Retrofit setup
                val response = diaryApi.postEntry(remoteRequest)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    println("Entry sent to server successfully: $responseBody")
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Failed to send entry to server: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                println("Network error sending entry: ${e.message}")
            }
        }
    }

    /**
     * Mendapatkan semua entri diary dari database lokal.
     * Mengembalikan Flow, memungkinkan UI bereaksi terhadap perubahan data secara real-time.
     */
    fun getAllEntries(): Flow<List<DiaryEntry>> { // <<< KOREKSI NAMA FUNGSI INI
        return diaryDao.getAllEntries()
    }

    /**
     * Meminta statistik mood dari backend.
     * @return Map<String, Int> berisi jumlah entri untuk tiap mood, atau null jika gagal.
     */
    suspend fun getMoodStats(): Map<String, Int>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = diaryApi.getMoodStats()
                if (response.isSuccessful) {
                    response.body()?.stats
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Failed to fetch mood stats: ${response.code()} - $errorBody")
                    null
                }
            } catch (e: Exception) {
                println("Network error fetching mood stats: ${e.message}")
                null
            }
        }
    }

    // TODO: Tambahkan fungsi lain untuk CRUD (update, delete, getById) jika diperlukan
    // suspend fun updateEntry(entry: DiaryEntry) = withContext(Dispatchers.IO) { diaryDao.updateEntry(entry) }
    // suspend fun deleteEntry(entry: DiaryEntry) = withContext(Dispatchers.IO) { diaryDao.deleteEntry(entry) }
    // suspend fun getEntryById(id: Int) = withContext(Dispatchers.IO) { diaryDao.getEntryById(id) }
}
