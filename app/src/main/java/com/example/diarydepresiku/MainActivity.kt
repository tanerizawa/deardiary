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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person

import com.example.diarydepresiku.ui.theme.DiarydepresikuTheme
import com.example.diarydepresiku.ui.DiaryFormScreen
import com.example.diarydepresiku.ui.HistoryScreen
import com.example.diarydepresiku.ui.ProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val application = LocalContext.current.applicationContext as MyApplication
            val diaryFactory = DiaryViewModelFactory(application = application)
            val diaryViewModel: DiaryViewModel = viewModel(factory = diaryFactory)


            DiarydepresikuTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate("home") {
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
                                selected = currentRoute == "home",
                                onClick = {
                                    navController.navigate("home") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                icon = { Icon(Icons.Filled.Home, contentDescription = "Beranda") },
                                label = { Text("Beranda") },
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
                                icon = { Icon(Icons.Filled.CalendarToday, contentDescription = "Riwayat") },
                                label = { Text("Riwayat") },
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
                                icon = { Icon(Icons.Filled.Person, contentDescription = "Profil") },
                                label = { Text("Profil") },
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
                        composable("home") {
                            DiaryFormScreen(
                                viewModel = diaryViewModel,
                                onNavigateToContent = null
                            )
                        }
                        composable("history") {
                            HistoryScreen(viewModel = diaryViewModel)
                        }
                        composable("profile") {
                            ProfileScreen()
                        }
                    }
                }
            }
        }
    }
}
