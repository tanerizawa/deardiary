package com.example.diarydepresiku

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

// Entitas data untuk satu entri diary yang akan disimpan di Room Database
@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Auto-generate ID untuk setiap entri

    @ColumnInfo(name = "content")
    val content: String, // Isi dari entri diary

    @ColumnInfo(name = "mood")
    val mood: String, // Mood yang dipilih untuk entri ini

    @ColumnInfo(name = "activities")
    val activities: List<String>,

    @ColumnInfo(name = "creation_timestamp") // Menggunakan nama kolom yang jelas
    val creationTimestamp: Long // Waktu pembuatan entri dalam bentuk Unix timestamp (Long)
)
