package com.example.diarydepresiku

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.TypeConverter
import java.util.Date // Penting untuk TypeConverter

// Kelas TypeConverter untuk mengkonversi Date ke Long (Unix timestamp) dan sebaliknya
// Ini memungkinkan Room untuk menyimpan Date object sebagai Long di database
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

// Entitas data untuk satu entri diary yang akan disimpan di Room Database
@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Auto-generate ID untuk setiap entri

    @ColumnInfo(name = "content")
    val content: String, // Isi dari entri diary

    @ColumnInfo(name = "mood")
    val mood: String, // Mood yang dipilih untuk entri ini

    @ColumnInfo(name = "creation_timestamp") // Menggunakan nama kolom yang jelas
    val creationTimestamp: Long // Waktu pembuatan entri dalam bentuk Unix timestamp (Long)
)