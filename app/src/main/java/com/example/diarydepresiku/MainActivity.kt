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
import com.example.diarydepresiku.ui.theme.DiarydepresikuTheme
import com.example.diarydepresiku.ui.DiaryFormScreen // <<< PENTING: Import ini dari package ui
import com.example.diarydepresiku.ui.MoodAnalysisScreen
import androidx.navigation.compose.*

// Hapus definisi 'moodOptions' jika sudah ada di DiaryFormScreen.kt atau tempat lain yang lebih tepat.
// val moodOptions = listOf("Senang", "Tersipu", "Sedih", "Cemas", "Marah")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val application = LocalContext.current.applicationContext as MyApplication
            val factory = DiaryViewModelFactory(application = application)
            val diaryViewModel: DiaryViewModel = viewModel(factory = factory)

            DiarydepresikuTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentRoute == "form",
                                onClick = {
                                    navController.navigate("form") {
                                        popUpTo("form") { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                label = { Text("Diary") },
                                alwaysShowLabel = true
                            )
                            NavigationBarItem(
                                selected = currentRoute == "analysis",
                                onClick = {
                                    navController.navigate("analysis") {
                                        popUpTo("form") { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                label = { Text("Analysis") },
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
                            DiaryFormScreen(viewModel = diaryViewModel)
                        }
                        composable("analysis") {
                            MoodAnalysisScreen(viewModel = diaryViewModel)
                        }
                    }
                }
            }
        }
    }
}

// Hapus fungsi DiaryFormScreen, moodOptions, dan Preview yang ada di sini
// jika Anda sudah memindahkannya ke DiaryFormScreen.kt
/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryFormScreen(
    viewModel: DiaryViewModel,
    modifier: Modifier = Modifier
) {
    // ... semua kode form di sini ...
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun DiaryFormScreenPreview() {
    // ... kode preview ...
}
*/
