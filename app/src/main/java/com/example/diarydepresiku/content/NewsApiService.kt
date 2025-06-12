package com.example.diarydepresiku.content

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/** Retrofit service for fetching articles from NewsAPI */
interface NewsApiService {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("apiKey") apiKey: String,
        @Query("country") country: String = "us",
        @Query("category") category: String = "health"
    ): Response<NewsResponse>

    /**
     * Search articles using the NewsAPI `everything` endpoint.
     * The [query] term is used to find relevant articles.
     */
    @GET("v2/everything")
    suspend fun searchArticles(
        @Query("apiKey") apiKey: String,
        @Query("q") query: String,
        @Query("sortBy") sortBy: String = "popularity"
    ): Response<NewsResponse>
}
