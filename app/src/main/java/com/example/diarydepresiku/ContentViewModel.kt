package com.example.diarydepresiku

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarydepresiku.content.ContentRepository
import com.example.diarydepresiku.content.EducationalArticle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel yang bertugas memuat dan menyediakan artikel edukasi.
 * Repository disuntikkan melalui [ContentViewModelFactory].
 * Opsional dapat memanfaatkan data mood dari [DiaryViewModel] untuk memfilter artikel.
 */
class ContentViewModel(
    private val repository: ContentRepository,
    private val diaryViewModel: DiaryViewModel? = null
) : ViewModel() {

    private val _articles = MutableStateFlow<List<EducationalArticle>>(emptyList())
    val articles: StateFlow<List<EducationalArticle>> = _articles.asStateFlow()

    /**
     * Mengambil artikel terbaru dari repository.
     * Jika [filterMood] diberikan atau terdapat mood dominan dari [DiaryViewModel],
     * hasil akan difilter berdasarkan kata tersebut pada judul atau deskripsi.
     */
    fun refreshArticles(filterMood: String? = null) {
        viewModelScope.launch {
            val mood = filterMood
                ?: diaryViewModel?.moodCounts?.value?.maxByOrNull { it.value }?.key
            val allArticles = repository.getArticles(apiKey = "")
            _articles.value = if (mood.isNullOrBlank()) {
                allArticles
            } else {
                allArticles.filter { article ->
                    article.title?.contains(mood, ignoreCase = true) == true ||
                        article.description?.contains(mood, ignoreCase = true) == true
                }
            }
        }
    }
}

