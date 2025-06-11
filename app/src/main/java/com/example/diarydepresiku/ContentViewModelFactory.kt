package com.example.diarydepresiku

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.diarydepresiku.content.ContentRepository

/**
 * Factory untuk membuat instance [ContentViewModel] dengan dependency [ContentRepository].
 */
class ContentViewModelFactory(
    private val repository: ContentRepository,
    private val diaryViewModel: DiaryViewModel? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContentViewModel(repository, diaryViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

