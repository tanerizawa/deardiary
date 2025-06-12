package com.example.diarydepresiku.content

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Entity menyimpan reaksi pengguna terhadap artikel edukasi */
@Entity(tableName = "article_reactions")
data class ArticleReaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "article_url") val articleUrl: String,
    @ColumnInfo(name = "reaction") val reaction: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)
