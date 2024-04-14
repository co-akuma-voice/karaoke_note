package com.example.karaoke_note

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.karaoke_note.data.AppDatabase
import com.example.karaoke_note.data.SongScore
import com.example.karaoke_note.ui.theme.Karaoke_noteTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val songDao = AppDatabase.getDatabase(this).songDao()
        val songScoreDao = AppDatabase.getDatabase(this).songScoreDao()
        val artistDao = AppDatabase.getDatabase(this).artistDao()
        setContent {
            Karaoke_noteTheme {
                val showDialog = remember { mutableStateOf(false) }
                val editingSongScore = remember { mutableStateOf<SongScore?>(null) }
                val snackBarHostState = remember { SnackbarHostState() }
                val showFilterSheet = remember { mutableStateOf(false) }

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    Scaffold(
                        topBar = {
                            Column {
                                AppBar(navController, songDao, songScoreDao, artistDao)
                                Breadcrumbs(navController, songDao, artistDao)
                            }
                        },
                        bottomBar = {
                            BottomNavigationBar(navController, songScoreDao)
                        },
                        floatingActionButton = {
                            NewEntryScreen(navController, songDao, songScoreDao, artistDao, lifecycleScope, showDialog, editingSongScore, snackBarHostState)
                        },
                        snackbarHost = {
                            SnackbarHost(snackBarHostState)
                        },
                    ) { paddingValues ->
                        NavHost(
                            navController,
                            startDestination = "latest",
                            Modifier.padding(paddingValues)
                        ) {
                            composable("latest") {
                                LatestPage(navController, songDao, songScoreDao, artistDao)
                            }
                            composable("song_data/{songId}") {backStackEntry ->
                                val songId = backStackEntry.arguments?.getString("songId")?.toLongOrNull()
                                if (songId != null) {
                                    val song = songDao.getSong(songId)
                                    if (song != null) {
                                        SongScores(song, songDao, songScoreDao, lifecycleScope, showDialog, editingSongScore)
                                    }
                                }
                            }
                            composable("plans"){
                                PlansPage(songDao, songScoreDao, artistDao, showDialog, editingSongScore, lifecycleScope, snackBarHostState)
                            }
                            composable("list"){
                                ArtistsPage(navController, artistDao, songDao)
                            }
                            composable("song_list/{artistId}"){backStackEntry ->
                                val artistId = backStackEntry.arguments?.getString("artistId")?.toLongOrNull()
                                if (artistId != null) {
                                    SongList(navController, artistId, songDao, songScoreDao, artistDao)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

