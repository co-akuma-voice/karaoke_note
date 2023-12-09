package com.example.karaoke_note

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.SongDao
import kotlinx.coroutines.launch

fun truncateText(text: String, maxLength: Int): String {
    return if (text.length > maxLength) text.take(maxLength) + "..." else text
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
                    navBackStackEntry?.arguments?.getString("songId")?.toLongOrNull()
                        ?.let { songId ->
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
            Text(
                truncateText(artistName!!, 10),
                modifier = Modifier.clickable { navController.navigate("song_list/$artistId") })
        }
        if (songTitle != null) {
            Text(" > ")
            Text(truncateText(songTitle!!, 10))
        }
    }
}