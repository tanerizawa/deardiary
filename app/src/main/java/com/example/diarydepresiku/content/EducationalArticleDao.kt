package com.example.diarydepresiku.content

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EducationalArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<EducationalArticleEntity>)

    @Query("SELECT * FROM educational_articles")
    suspend fun getAllArticles(): List<EducationalArticleEntity>

    @Query("DELETE FROM educational_articles")
    suspend fun clearAll()
}
