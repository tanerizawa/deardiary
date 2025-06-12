package com.example.diarydepresiku

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarydepresiku.content.ContentRepository
import com.example.diarydepresiku.content.EducationalArticle
import com.example.diarydepresiku.BuildConfig
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

    private val _dominantMood = MutableStateFlow<String?>(null)
    val dominantMood: StateFlow<String?> = _dominantMood.asStateFlow()

    private val _highlightMood = MutableStateFlow<String?>(null)
    val highlightMood: StateFlow<String?> = _highlightMood.asStateFlow()

    /**
     * Diperbarui dari [DiaryViewModel] untuk menyimpan statistik mood terkini.
     */
    fun updateMoodStats(stats: Map<String, Int>) {
        _dominantMood.value = stats.maxByOrNull { it.value }?.key
    }

    /**
     * Mengambil artikel terbaru dari repository.
     * Jika [filterMood] diberikan atau terdapat mood dominan dari [DiaryViewModel],
     * hasil akan difilter berdasarkan kata tersebut pada judul atau deskripsi.
     */
    fun refreshArticles(filterMood: String? = null) {
        viewModelScope.launch {
            val mood = filterMood
                ?: _dominantMood.value
                ?: diaryViewModel?.moodCounts?.value?.maxByOrNull { it.value }?.key

            val allArticles = repository.getArticles(
                apiKey = BuildConfig.NEWS_API_KEY,
                query = mood
            )

            _articles.value = allArticles
            _highlightMood.value = mood
        }
    }

    fun recordReaction(url: String, reaction: String) {
        viewModelScope.launch { repository.recordReaction(url, reaction) }
    }
}

