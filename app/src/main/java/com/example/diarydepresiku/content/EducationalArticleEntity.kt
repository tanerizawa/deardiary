package com.example.diarydepresiku.content

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Entity used for caching articles in Room */
@Entity(tableName = "educational_articles")
data class EducationalArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "url") val url: String?,
    @ColumnInfo(name = "url_to_image") val urlToImage: String?,
    @ColumnInfo(name = "published_at") val publishedAt: String?
)

fun EducationalArticleEntity.toModel(): EducationalArticle = EducationalArticle(
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt
)

fun EducationalArticle.toEntity(): EducationalArticleEntity = EducationalArticleEntity(
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt
)
