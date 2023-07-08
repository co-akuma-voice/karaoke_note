package com.example.karaoke_note

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.karaoke_note.data.AppDatabase
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.ui.theme.Karaoke_noteTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val songDao = AppDatabase.getDatabase(this).songDao()
        val context = this
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
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController,
                            startDestination = "home",
                            Modifier.padding(paddingValues)
                        ) {
                            composable("home") {
                                Home(navController)
                            }
                            composable("song_data") {
                                val songId = songDao.insertSong(Song(title = "Song1", artist = "Artist1"))
                                SongScores(Song(id = songId, title = "Song1", artist = "Artist1"), context)
                            }
                            composable("list"){
                                ArtistsPage(navController, "artist")
                            }
                        }
                    }
                }
            }
        }
    }
}
