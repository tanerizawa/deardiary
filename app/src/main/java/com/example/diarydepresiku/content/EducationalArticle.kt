package com.example.diarydepresiku.content

import com.google.gson.annotations.SerializedName

/** Data class representing an article returned by NewsAPI */
data class EducationalArticle(
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("urlToImage") val urlToImage: String?,
    @SerializedName("publishedAt") val publishedAt: String?
)

/** Wrapper for the NewsAPI response */
data class NewsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("totalResults") val totalResults: Int,
    @SerializedName("articles") val articles: List<EducationalArticle>
)
