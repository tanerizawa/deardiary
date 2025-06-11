package com.example.diarydepresiku

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApplication : Application() {

    // Lazy initialization untuk Room Database
    val database: DiaryDatabase by lazy {
        DiaryDatabase.getDatabase(this)
    }

    // Lazy initialization untuk DiaryDao (dari database)
    val diaryDao: DiaryDao by lazy {
        database.diaryDao()
    }

    // Lazy initialization untuk Retrofit
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/") // PASTIKAN URL INI BENAR (emulator: 10.0.2.2, device: IP lokal Anda)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Lazy initialization untuk DiaryApi (dari Retrofit)
    val diaryApi: DiaryApi by lazy {
        retrofit.create(DiaryApi::class.java)
    }

    // Lazy initialization untuk Repository (menerima DAO dan API)
    val diaryRepository: DiaryRepository by lazy {
        DiaryRepository(diaryDao, diaryApi)
    }
}