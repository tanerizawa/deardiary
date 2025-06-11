package com.example.diarydepresiku

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update // Import untuk update
import androidx.room.Delete // Import untuk delete
import kotlinx.coroutines.flow.Flow // Penting untuk data yang diobservasi

@Dao
interface DiaryDao {
    /**
     * Menyimpan entri diary baru ke database lokal.
     * Jika ada konflik (misalnya, entri dengan ID yang sama), akan diganti.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DiaryEntry)

    /**
     * Mengupdate entri diary yang sudah ada di database.
     * Room akan menggunakan PrimaryKey untuk menemukan entri yang akan diupdate.
     */
    @Update
    suspend fun updateEntry(entry: DiaryEntry)

    /**
     * Menghapus entri diary dari database.
     * Room akan menggunakan PrimaryKey untuk menemukan entri yang akan dihapus.
     */
    @Delete
    suspend fun deleteEntry(entry: DiaryEntry)

    /**
     * Mendapatkan satu entri diary berdasarkan ID.
     */
    @Query("SELECT * FROM diary_entries WHERE id = :id")
    suspend fun getEntryById(id: Int): DiaryEntry?

    /**
     * Mengambil semua entri diary dari database dan mengembalikannya sebagai Flow.
     * Flow akan memancarkan daftar entri terbaru setiap kali ada perubahan di tabel.
     * Diurutkan berdasarkan timestamp pembuatan dari yang terbaru ke terlama.
     */
    @Query("SELECT * FROM diary_entries ORDER BY creation_timestamp DESC")
    fun getAllEntries(): Flow<List<DiaryEntry>> // Mengembalikan Flow, tidak lagi suspend
}