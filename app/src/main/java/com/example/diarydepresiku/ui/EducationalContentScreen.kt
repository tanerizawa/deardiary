package com.example.diarydepresiku.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.diarydepresiku.ContentViewModel
import com.example.diarydepresiku.content.EducationalArticle
@Composable
fun EducationalContentScreen(
    viewModel: ContentViewModel,
    modifier: Modifier = Modifier
) {
    val articles = viewModel.articles.collectAsState().value
    val highlightMood = viewModel.highlightMood.collectAsState().value
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(articles) { article ->
            val highlighted = highlightMood != null && (
                article.title?.contains(highlightMood, ignoreCase = true) == true ||
                    article.description?.contains(highlightMood, ignoreCase = true) == true
            )
            ArticleItem(article = article, isHighlighted = highlighted) { url ->
                url?.let {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
private fun ArticleItem(
    article: EducationalArticle,
    isHighlighted: Boolean,
    onOpen: (String?) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable { onOpen(article.url) },
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = article.title.orEmpty(), style = MaterialTheme.typography.titleMedium)
            article.description?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}
