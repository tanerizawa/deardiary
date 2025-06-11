package com.example.diarydepresiku.content

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.diarydepresiku.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class ContentRepository(
    private val api: NewsApiService,
    private val dao: EducationalArticleDao,
    private val context: Context
) {
    private fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val cap = cm.getNetworkCapabilities(network) ?: return false
        return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    open suspend fun getArticles(apiKey: String, country: String = "us"): List<EducationalArticle> =
        withContext(Dispatchers.IO) {
            if (isNetworkAvailable()) {
                try {
                    val response = api.getTopHeadlines(apiKey, country)
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
            return@withContext loadDefaultArticles()
        }

    private fun loadDefaultArticles(): List<EducationalArticle> {
        val input = context.resources.openRawResource(R.raw.default_articles)
        val json = input.bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<EducationalArticle>>() {}.type
        return Gson().fromJson(json, type)
    }
}
