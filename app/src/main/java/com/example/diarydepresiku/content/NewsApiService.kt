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
}
