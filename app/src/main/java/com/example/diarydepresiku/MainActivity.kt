// app/src/main/java/com/example/diarydepresiku/MainActivity.kt

package com.example.diarydepresiku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.Article // ✅ Gunakan versi AutoMirrored

import com.example.diarydepresiku.ui.theme.DiarydepresikuTheme
import com.example.diarydepresiku.ui.screens.DiaryFormScreen
import com.example.diarydepresiku.ui.screens.HistoryScreen
import com.example.diarydepresiku.ui.screens.ContentScreen
import com.example.diarydepresiku.ui.screens.SettingsScreen
import com.example.diarydepresiku.ContentViewModel
import com.example.diarydepresiku.ContentViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val application = LocalContext.current.applicationContext as MyApplication
            val diaryFactory = DiaryViewModelFactory(application = application)
            val diaryViewModel: DiaryViewModel = viewModel(factory = diaryFactory)

            val contentFactory = ContentViewModelFactory(
                repository = application.contentRepository,
                diaryViewModel = diaryViewModel
            )
            val contentViewModel: ContentViewModel = viewModel(factory = contentFactory)

            // Refresh konten artikel saat pertama kali dibuka
            LaunchedEffect(Unit) {
                contentViewModel.refreshArticles()
            }

            // Update artikel berdasarkan statistik mood
            LaunchedEffect(Unit) {
                diaryViewModel.moodCounts.collect { stats ->
                    contentViewModel.updateMoodStats(stats)
                    contentViewModel.refreshArticles()
                }
            }

            DiarydepresikuTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate("form") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "New Entry")
                        }
                    },
                    floatingActionButtonPosition = FabPosition.Center,
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentRoute == "form",
                                onClick = {
                                    navController.navigate("form") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                icon = { Icon(Icons.Filled.Edit, contentDescription = "Diary") },
                                label = { Text("Diary") },
                                alwaysShowLabel = true
                            )
                            NavigationBarItem(
                                selected = currentRoute == "analysis",
                                onClick = {
                                    navController.navigate("analysis") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                icon = { Icon(Icons.Filled.Insights, contentDescription = "Analysis") },
                                label = { Text("Analysis") },
                                alwaysShowLabel = true
                            )
                            NavigationBarItem(
                                selected = currentRoute == "content",
                                onClick = {
                                    navController.navigate("content") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                icon = { Icon(Icons.AutoMirrored.Filled.Article, contentDescription = "Content") },
                                label = { Text("Content") },
                                alwaysShowLabel = true
                            )

                            NavigationBarItem(
                                selected = currentRoute == "settings",
                                onClick = {
                                    navController.navigate("settings") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                label = { Text("Settings") },
                                alwaysShowLabel = true
                            )

                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "form",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("form") {
                            DiaryFormScreen(
                                viewModel = diaryViewModel,
                                onNavigateToContent = { navController.navigate("content") }
                            )
                        }
                        composable("analysis") {
                            MoodAnalysisScreen(
                                viewModel = diaryViewModel,
                                onNavigateToContent = { navController.navigate("content") }
                            )
                        }
                        composable("content") {
                            EducationalContentScreen(viewModel = contentViewModel)
                        }
                        composable("settings") {
                            ReminderSettingsScreen(prefs = application.reminderPreferences)
                        }
                    }
                }
            }
        }
    }
}
