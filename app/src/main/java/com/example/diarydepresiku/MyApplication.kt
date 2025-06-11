package com.example.diarydepresiku

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.diarydepresiku.content.NewsApiService
import com.example.diarydepresiku.content.EducationalArticleDao
import com.example.diarydepresiku.content.ContentRepository

class MyApplication : Application() {

    // Lazy initialization untuk Room Database
    val database: DiaryDatabase by lazy {
        DiaryDatabase.getDatabase(this)
    }

    // Lazy initialization untuk DiaryDao (dari database)
    val diaryDao: DiaryDao by lazy {
        database.diaryDao()
    }

    val articleDao: EducationalArticleDao by lazy {
        database.educationalArticleDao()
    }

    // Lazy initialization untuk Retrofit
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/") // PASTIKAN URL INI BENAR (emulator: 10.0.2.2, device: IP lokal Anda)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val newsRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Lazy initialization untuk DiaryApi (dari Retrofit)
    val diaryApi: DiaryApi by lazy {
        retrofit.create(DiaryApi::class.java)
    }

    val newsApi: NewsApiService by lazy {
        newsRetrofit.create(NewsApiService::class.java)
    }

    // Lazy initialization untuk Repository (menerima DAO dan API)
    val diaryRepository: DiaryRepository by lazy {
        DiaryRepository(diaryDao, diaryApi)
    }

    val contentRepository: ContentRepository by lazy {
        ContentRepository(newsApi, articleDao, this)
    }
}
