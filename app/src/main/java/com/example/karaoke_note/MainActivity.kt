package com.example.karaoke_note

import SongList
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.karaoke_note.data.AppDatabase
import com.example.karaoke_note.ui.theme.Karaoke_noteTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val songDao = AppDatabase.getDatabase(this).songDao()
        val songScoreDao = AppDatabase.getDatabase(this).songScoreDao()
        setContent {
            Karaoke_noteTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    Scaffold(
                        topBar = {
                            AppBar(navController)
                        },
                        bottomBar = {
                            BottomNavigationBar(navController)
                        },
                        floatingActionButton = {
                            NewEntryScreen()
                            //AnimatedContentFABtoDiagram()
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController,
                            startDestination = "home",
                            Modifier.padding(paddingValues)
                        ) {
                            composable("home") {
                                Home(navController, songDao, songScoreDao)
                            }
                            composable("song_data/{songId}") {backStackEntry ->
                                val songId = backStackEntry.arguments?.getString("songId")?.toLongOrNull()
                                if (songId != null) {
                                    val song = songDao.getSong(songId)
                                    if (song != null) {
                                        SongScores(song, songScoreDao)
                                    }
                                }
                            }
                            composable("list"){
                                ArtistsPage(navController, songDao)
                            }
                            composable("song_list/{artist}"){backStackEntry ->
                                val artist = backStackEntry.arguments?.getString("artist")
                                if (artist != null) {
                                    SongList(navController, artist, songDao, songScoreDao)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
