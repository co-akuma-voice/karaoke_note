package com.example.karaoke_note

import SongList
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.karaoke_note.data.AppDatabase
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScore
import com.example.karaoke_note.ui.theme.Karaoke_noteTheme
import kotlinx.coroutines.launch

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
                            BottomNavigationBar(navController)
                        },
                        floatingActionButton = {
                            NewEntryScreen(navController, songDao, songScoreDao, artistDao, lifecycleScope, showDialog, editingSongScore)
                            //AnimatedContentFABtoDiagram()
                        },
                    ) { paddingValues ->
                        NavHost(
                            navController,
                            startDestination = "home",
                            Modifier.padding(paddingValues)
                        ) {
                            composable("home") {
                                Home(navController, songDao, songScoreDao, artistDao)
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
                            composable("list"){
                                ArtistsPage(navController, artistDao)
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

@Composable
fun Breadcrumbs(navController: NavController, songDao: SongDao, artistDao: ArtistDao) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: return
    val coroutineScope = rememberCoroutineScope()

    // IDに基づいてデータを取得
    var artistName by remember { mutableStateOf<String?>(null) }
    var artistId by remember { mutableStateOf<Long?>(null) }
    var songTitle by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentRoute) {
        coroutineScope.launch {
            songTitle = null
            artistName = null
            when {
                currentRoute.startsWith("song_data/") -> {
                    navBackStackEntry?.arguments?.getString("songId")?.toLongOrNull()?.let { songId ->
                        songDao.getSong(songId)?.let { song ->
                            songTitle = song.title
                            artistName = artistDao.getNameById(song.artistId)
                            artistId = song.artistId
                        }
                    }
                }
                currentRoute.startsWith("song_list/") -> {
                    navBackStackEntry?.arguments?.getString("artistId")?.toLongOrNull()?.let { id ->
                        artistName = artistDao.getNameById(id)
                        artistId = id
                    }
                }
            }
        }
    }

    // パンくずリストの表示
    Row(modifier = Modifier.padding(16.dp)) {
        Text("Home", modifier = Modifier.clickable { navController.navigate("home") })
        if (artistName != null) {
            Text(" > ")
            Text(artistName!!, modifier = Modifier.clickable { navController.navigate("song_list/$artistId") })
        }
        if (songTitle != null) {
            Text(" > ")
            Text(songTitle!!)
        }
    }
}
