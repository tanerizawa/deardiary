package com.example.diarydepresiku.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.diarydepresiku.ui.theme.*

data class OnboardingPage(
    val title: String,
    val description: String,
    val emoji: String,
    val backgroundColor: Color,
    val accentColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = listOf(
        OnboardingPage(
            title = "Selamat Datang di\nDiary Depresiku",
            description = "Tempat aman untuk mengekspresikan perasaan dan memantau kesehatan mental Anda setiap hari",
            emoji = "💙",
            backgroundColor = SoftBlueLight,
            accentColor = SoftBlueDark
        ),
        OnboardingPage(
            title = "Tulis & Ekspresikan\nPerasaan Anda",
            description = "Ceritakan hari Anda, ungkapkan emosi, dan pilih mood dengan mudah dalam lingkungan yang aman",
            emoji = "✍️",
            backgroundColor = MintLight,
            accentColor = MintDark
        ),
        OnboardingPage(
            title = "Analisis & Pantau\nPerkembangan Mood",
            description = "Lihat pola mood Anda, dapatkan insight, dan pahami perjalanan kesehatan mental Anda",
            emoji = "📊",
            backgroundColor = PeachLight,
            accentColor = PeachDark
        ),
        OnboardingPage(
            title = "Siap Memulai\nPerjalanan Anda?",
            description = "Mari mulai menulis catatan harian dan jaga kesehatan mental Anda bersama kami",
            emoji = "🌟",
            backgroundColor = SuccessLight,
            accentColor = Success
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    
    var isAnimating by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundGradientStart,
                        BackgroundGradientEnd
                    )
                )
            )
    ) {
        // Content Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->
            OnboardingPageContent(
                page = pages[pageIndex],
                isVisible = pagerState.currentPage == pageIndex
            )
        }

        // Bottom section with indicators and navigation
        BottomSection(
            currentPage = pagerState.currentPage,
            totalPages = pages.size,
            isLastPage = pagerState.currentPage == pages.size - 1,
            isAnimating = isAnimating,
            onNext = {
                scope.launch {
                    if (pagerState.currentPage < pages.size - 1) {
                        isAnimating = true
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        delay(500)
                        isAnimating = false
                    } else {
                        onComplete()
                    }
                }
            },
            onSkip = onComplete
        )
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    isVisible: Boolean
) {
    val animationSpec = tween<Float>(
        durationMillis = 600,
        easing = EaseOutCubic
    )

    val titleOffset by animateFloatAsState(
        targetValue = if (isVisible) 0f else 100f,
        animationSpec = animationSpec,
        label = "titleOffset"
    )

    val contentOffset by animateFloatAsState(
        targetValue = if (isVisible) 0f else 50f,
        animationSpec = animationSpec,
        label = "contentOffset"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = animationSpec,
        label = "alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Emoji with background circle
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = page.backgroundColor,
                    shape = CircleShape
                )
                .offset(y = (-titleOffset * 0.5f).dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = page.emoji,
                fontSize = 48.sp,
                modifier = Modifier.alpha(alpha)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Title
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            modifier = Modifier
                .offset(y = (-titleOffset).dp)
                .alpha(alpha)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Description
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = (-contentOffset).dp)
                .alpha(alpha)
        )
    }
}

@Composable
private fun BottomSection(
    currentPage: Int,
    totalPages: Int,
    isLastPage: Boolean,
    isAnimating: Boolean,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Page indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            repeat(totalPages) { index ->
                PageIndicator(
                    isActive = index == currentPage,
                    isCompleted = index < currentPage
                )
            }
        }

        // Navigation buttons
        if (!isLastPage) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip button
                TextButton(
                    onClick = onSkip,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = TextSecondary
                    )
                ) {
                    Text(
                        text = "Lewati",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                // Next button
                FilledTonalButton(
                    onClick = onNext,
                    enabled = !isAnimating,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = SoftBlueDark,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    if (isAnimating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Lanjut",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // Get started button
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Success
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mulai Menulis Diary",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun PageIndicator(
    isActive: Boolean,
    isCompleted: Boolean
) {
    val animatedWidth by animateIntAsState(
        targetValue = if (isActive) 24 else 8,
        animationSpec = tween(300),
        label = "width"
    )

    val color = when {
        isCompleted -> Success
        isActive -> SoftBlueDark
        else -> Gray200
    }

    Box(
        modifier = Modifier
            .width(animatedWidth.dp)
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color)
    )
}
