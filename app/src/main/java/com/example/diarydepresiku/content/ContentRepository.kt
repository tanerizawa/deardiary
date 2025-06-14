package com.example.diarydepresiku.content

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.diarydepresiku.content.ArticleReaction
import com.example.diarydepresiku.content.ArticleReactionDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class ContentRepository(
    private val api: NewsApiService,
    private val diaryApi: com.example.diarydepresiku.DiaryApi,
    private val dao: EducationalArticleDao,
    private val reactionDao: ArticleReactionDao,
    private val context: Context
) {
    private fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val cap = cm.getNetworkCapabilities(network) ?: return false
        return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    open suspend fun getArticles(
        apiKey: String,
        country: String = "us",
        query: String? = null
    ): List<EducationalArticle> =
        withContext(Dispatchers.IO) {
            if (isNetworkAvailable()) {
                if (!query.isNullOrBlank()) {
                    try {
                        val aiResp = diaryApi.openrouterArticles(
                            com.example.diarydepresiku.ArticlesRequest(query)
                        )
                        if (aiResp.isSuccessful) {
                            val articles = aiResp.body() ?: emptyList()
                            dao.clearAll()
                            dao.insertArticles(articles.map { it.toEntity() })
                            return@withContext articles
                        }
                    } catch (_: Exception) {
                        // fall back to NewsAPI
                    }
                }
                try {
                    val response = if (!query.isNullOrBlank()) {
                        api.searchArticles(apiKey, query)
                    } else {
                        api.getTopHeadlines(apiKey, country)
                    }
                    if (response.isSuccessful) {
                        val articles = response.body()?.articles ?: emptyList()
                        dao.clearAll()
                        dao.insertArticles(articles.map { it.toEntity() })
                        return@withContext articles
                    }
                } catch (_: Exception) {
                    // fall back to cache
                }
            }
            val cached = dao.getAllArticles().map { it.toModel() }
            if (cached.isNotEmpty()) return@withContext cached
            return@withContext emptyList()
        }

    suspend fun recordReaction(url: String, reaction: String) {
        val entity = ArticleReaction(articleUrl = url, reaction = reaction)
        withContext(Dispatchers.IO) { reactionDao.insertReaction(entity) }
    }

}
