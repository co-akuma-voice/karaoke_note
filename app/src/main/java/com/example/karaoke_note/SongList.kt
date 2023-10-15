import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScoreDao
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

enum class SongSortColumn {
    Title,
    HighestScore,
    Date
}
data class SongData(val id: Long, val title: String, val highestScore: Float, val lastDate: LocalDate)

fun convertToSongDataList(songScoreDao: SongScoreDao, songs: List<Song>): List<SongData> {
    return runBlocking(Dispatchers.IO) {
        songs.map { song ->
            async {
                val highestScoreEntry = songScoreDao.getHighestScoreBySongId(song.id)
                val lastDate = songScoreDao.getMostRecentDate()
                SongData(
                    id = song.id,
                    title = song.title,
                    highestScore = highestScoreEntry?.score ?: 0f,
                    lastDate = lastDate ?: LocalDate.MIN
                )
            }
        }.awaitAll()
    }
}
@Composable
fun SongList(navController: NavController, artist: String, songDao: SongDao, songScoreDao: SongScoreDao) {
    val songs = convertToSongDataList(songScoreDao, songDao.getSongsByArtist(artist))
    Column {
        Text(text = artist)
        SongTable(navController, songs)
    }
}

@Composable
fun SongTable(navController: NavController, songs: List<SongData>) {
    var sortDirection by remember { mutableStateOf(SortDirection.None) }
    var sortColumn by remember { mutableStateOf(SongSortColumn.Title) }
    val sortedSongs = remember(songs, sortColumn, sortDirection) {
        when (sortColumn) {
            SongSortColumn.Title -> {
                when (sortDirection) {
                    SortDirection.None -> songs
                    SortDirection.Asc -> songs.sortedBy(SongData::title)
                    SortDirection.Desc -> songs.sortedByDescending(SongData::title)
                }
            }
            SongSortColumn.HighestScore -> {
                when (sortDirection) {
                    SortDirection.None -> songs
                    SortDirection.Asc -> songs.sortedBy(SongData::highestScore)
                    SortDirection.Desc -> songs.sortedByDescending(SongData::highestScore)
                }
            }
            SongSortColumn.Date -> {
                when (sortDirection) {
                    SortDirection.None -> songs
                    SortDirection.Asc -> songs.sortedBy(SongData::lastDate)
                    SortDirection.Desc -> songs.sortedByDescending(SongData::lastDate)
                }
            }
        }
    }

    Column {
        HeaderRow(sortColumn, sortDirection) { newSortColumn, newSortDirection ->
            sortColumn = newSortColumn
            sortDirection = newSortDirection
        }
        LazyColumn {
            itemsIndexed(sortedSongs) { index, songData ->
                SongRow(navController, songData)
                if (index < songs.size - 1) {
                    Divider(color = Color.Gray, thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun HeaderRow(sortColumn: SongSortColumn, sortDirection: SortDirection, onSortChanged: (SongSortColumn, SortDirection) -> Unit) {
    Row(Modifier.fillMaxWidth()) {
        Box(modifier = Modifier
            .weight(2f)
            .clickable {
                val newDirection = when (sortColumn) {
                    SongSortColumn.Title -> when (sortDirection) {
                        SortDirection.None, SortDirection.Desc -> SortDirection.Asc
                        else -> SortDirection.Desc
                    }

                    else -> SortDirection.Asc
                }
                onSortChanged(SongSortColumn.Title, newDirection)
            }) {
            Text(text = "タイトル", modifier = Modifier.align(Alignment.CenterStart))
            when (sortColumn) {
                SongSortColumn.Title -> when (sortDirection) {
                    SortDirection.Asc -> Text(text = "↑", modifier = Modifier.align(Alignment.CenterEnd))
                    SortDirection.Desc -> Text(text = "↓", modifier = Modifier.align(Alignment.CenterEnd))
                    else -> {}
                }
                else -> {}
            }
        }
        Box(modifier = Modifier
            .weight(1.5f)
            .clickable {
                val newDirection = when (sortColumn) {
                    SongSortColumn.HighestScore -> when (sortDirection) {
                        SortDirection.None, SortDirection.Desc -> SortDirection.Asc
                        else -> SortDirection.Desc
                    }

                    else -> SortDirection.Asc
                }
                onSortChanged(SongSortColumn.HighestScore, newDirection)
            }) {
            Text(text = "最高スコア", modifier = Modifier.align(Alignment.CenterStart))
            when (sortColumn) {
                SongSortColumn.HighestScore -> when (sortDirection) {
                    SortDirection.Asc -> Text(text = "↑", modifier = Modifier.align(Alignment.CenterEnd))
                    SortDirection.Desc -> Text(text = "↓", modifier = Modifier.align(Alignment.CenterEnd))
                    else -> {}
                }
                else -> {}
            }
        }
        Box(modifier = Modifier
            .weight(1f)
            .clickable {
                val newDirection = when (sortColumn) {
                    SongSortColumn.Date -> when (sortDirection) {
                        SortDirection.None, SortDirection.Desc -> SortDirection.Asc
                        else -> SortDirection.Desc
                    }

                    else -> SortDirection.Asc
                }
                onSortChanged(SongSortColumn.Date, newDirection)
            }) {
            Text(text = "最後に歌った日", modifier = Modifier.align(Alignment.CenterStart))
            when (sortColumn) {
                SongSortColumn.Date -> when (sortDirection) {
                    SortDirection.Asc -> Text(text = "↑", modifier = Modifier.align(Alignment.CenterEnd))
                    SortDirection.Desc -> Text(text = "↓", modifier = Modifier.align(Alignment.CenterEnd))
                    else -> {}
                }
                else -> {}
            }
        }
    }
}


@Composable
fun SongRow(navController: NavController, songData: SongData) {
    Row(Modifier.fillMaxWidth().clickable {
        navController.navigate("song_data/${songData.id}")
    }) {
        Text(text = songData.title, modifier = Modifier.weight(2f))
        Text(text = String.format("%.3f", songData.highestScore), textAlign = TextAlign.Center, modifier = Modifier.weight(1.5f))
        Text(text = songData.lastDate.toString(), textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
    }
}