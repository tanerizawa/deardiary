package com.example.diarydepresiku.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.diarydepresiku.ContentViewModel
import com.example.diarydepresiku.content.EducationalArticle
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
@Composable
fun EducationalContentScreen(
    viewModel: ContentViewModel,
    modifier: Modifier = Modifier
) {
    ArticleList(
        viewModel = viewModel,
        modifier = modifier.verticalScroll(rememberScrollState())
    )
}

@Composable
fun ArticleList(
    viewModel: ContentViewModel,
    modifier: Modifier = Modifier
) {
    val articles = viewModel.articles.collectAsState().value
    val highlightMood = viewModel.highlightMood.collectAsState().value
    val context = LocalContext.current
    var openedUrl by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        articles.forEach { article ->
            val highlighted = highlightMood != null && (
                article.title?.contains(highlightMood, ignoreCase = true) == true ||
                    article.description?.contains(highlightMood, ignoreCase = true) == true
            )
            ArticleItem(
                article = article,
                isHighlighted = highlighted,
                showReactions = openedUrl == article.url,
                onOpen = { url ->
                    url?.let {
                        openedUrl = it
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        context.startActivity(intent)
                    }
                },
                onReaction = { emoji ->
                    openedUrl?.let { viewModel.recordReaction(it, emoji) }
                    openedUrl = null
                }
            )
        }
    }
}

@Composable
fun ArticleItem(
    article: EducationalArticle,
    isHighlighted: Boolean,
    showReactions: Boolean,
    onOpen: (String?) -> Unit,
    onReaction: (String) -> Unit
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
            if (showReactions) {
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    listOf("👍", "😊", "😢", "😡", "😮").forEach { emoji ->
                        Text(
                            text = emoji,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { onReaction(emoji) }
                        )
                    }
                }
            }
        }
    }
}
