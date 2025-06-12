package com.example.diarydepresiku.content

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleReactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReaction(reaction: ArticleReaction)

    @Query("SELECT * FROM article_reactions WHERE article_url = :url")
    fun getReactionsForArticle(url: String): Flow<List<ArticleReaction>>
}
