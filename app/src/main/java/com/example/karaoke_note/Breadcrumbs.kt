package com.example.karaoke_note

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.SongDao
import kotlinx.coroutines.launch

fun truncateText(
    text: String,
    maxLength: Int
): String {
    return if (text.length > maxLength) text.take(maxLength) + "..." else text
}

@Composable
fun Breadcrumbs(
    navController: NavController,
    focusManagerOfSearchBar: FocusManager,
    songDao: SongDao,
    artistDao: ArtistDao
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: return
    val coroutineScope = rememberCoroutineScope()

    // IDに基づいてデータを取得
    var artistName by remember { mutableStateOf<String?>(null) }
    var artistId by remember { mutableStateOf<Long?>(null) }
    var songTitle by remember { mutableStateOf<String?>(null) }

    val fontSize = 14

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
        // BottomNavBar があるので Top ページに戻る部分は不要？
        //Text("Latest", modifier = Modifier.clickable { navController.navigate("latest") })
        if (artistName != null) {
            // Top ページの表示を削除するならこれも不要
            //Text(" > ")
            Text(
                truncateText(artistName!!, 8),
                modifier = Modifier
                    .clickable {
                        focusManagerOfSearchBar.clearFocus()
                        navController.navigate("song_list/$artistId")
                    },
                fontSize = fontSize.sp
            )
        }
        if (songTitle != null) {
            Text(
                text = " > ",
                fontSize = fontSize.sp
            )
            Text(
                text = truncateText(songTitle!!, 8),
                fontSize = fontSize.sp
            )
        }
    }
}