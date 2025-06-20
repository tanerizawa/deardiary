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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person

import com.example.diarydepresiku.ui.theme.DiarydepresikuTheme
import com.example.diarydepresiku.ui.DiaryFormScreen
import com.example.diarydepresiku.ui.HomeScreen
import com.example.diarydepresiku.ui.EducationalContentScreen
import com.example.diarydepresiku.ui.HistoryScreen
import com.example.diarydepresiku.ui.ProfileScreen
import com.example.diarydepresiku.ContentViewModel
import com.example.diarydepresiku.ContentViewModelFactory
import com.example.diarydepresiku.ui.ReminderSettingsScreen

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

            val darkMode by application.reminderPreferences.darkMode.collectAsState(initial = false)
            val fontScale by application.reminderPreferences.fontScale.collectAsState(initial = 1f)

            DiarydepresikuTheme(darkTheme = darkMode, fontScale = fontScale) {
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
                            Icon(Icons.Default.Add, contentDescription = "New Entry")
                        }
                    },
                    floatingActionButtonPosition = FabPosition.Center,
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentRoute == "home",
                                onClick = {
                                    navController.navigate("home") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                icon = { Icon(Icons.Filled.Home, contentDescription = stringResource(R.string.nav_home)) },
                                label = { Text(stringResource(R.string.nav_home)) },
                                alwaysShowLabel = true
                            )
                            NavigationBarItem(
                                selected = currentRoute == "history",
                                onClick = {
                                    navController.navigate("history") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                icon = { Icon(Icons.Filled.History, contentDescription = stringResource(R.string.nav_history)) },
                                label = { Text(stringResource(R.string.nav_history)) },
                                alwaysShowLabel = true
                            )
                            NavigationBarItem(
                                selected = currentRoute == "profile",
                                onClick = {
                                    navController.navigate("profile") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                icon = { Icon(Icons.Filled.Person, contentDescription = stringResource(R.string.nav_profile)) },
                                label = { Text(stringResource(R.string.nav_profile)) },
                                alwaysShowLabel = true
                            )

                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("form") {
                            DiaryFormScreen(
                                viewModel = diaryViewModel,
                                contentViewModel = contentViewModel,
                                onNavigateToContent = { navController.navigate("content") }
                            )
                        }
                        composable("home") {
                            HomeScreen(
                                diaryViewModel = diaryViewModel,
                                contentViewModel = contentViewModel
                            )
                        }
                        composable("history") {
                            HistoryScreen(viewModel = diaryViewModel)
                        }
                        composable("profile") {
                            ProfileScreen(
                                viewModel = diaryViewModel,
                                onNavigateToSettings = {
                                    navController.navigate("settings") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
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
