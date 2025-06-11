package com.example.diarydepresiku

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.diarydepresiku.content.NewsApiService
import com.example.diarydepresiku.content.EducationalArticleDao
import com.example.diarydepresiku.content.ContentRepository
import com.example.diarydepresiku.BuildConfig

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
            .baseUrl(BuildConfig.BASE_URL)
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

    val reminderPreferences: ReminderPreferences by lazy {
        ReminderPreferences(this)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReminderWorker.CHANNEL_ID,
                "Daily Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
