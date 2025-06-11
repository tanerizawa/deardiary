package com.example.diarydepresiku // Pastikan ini adalah package utama aplikasi Anda

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Factory untuk membuat instance DiaryViewModel dengan dependensi pada Application
class DiaryViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Memeriksa apakah modelClass adalah DiaryViewModel
        if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {
            // Mengembalikan instance DiaryViewModel yang baru, melewati dependensi
            @Suppress("UNCHECKED_CAST")
            return DiaryViewModel(application = application) as T
        }
        // Jika modelClass bukan DiaryViewModel, lemparkan IllegalArgumentException
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
