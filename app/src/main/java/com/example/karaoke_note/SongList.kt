import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScoreDao
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

data class SongData(val id: Long, val title: String, val highestScore: Float, val lastDate: LocalDate)

fun convertToSongDataList(songScoreDao: SongScoreDao, songs: List<Song>): List<SongData> {
    return runBlocking(Dispatchers.IO) {
        songs.map { song ->
            async {
                val highestScoreEntry = songScoreDao.getHighestScoreBySongId(song.id)
                val lastDate = songScoreDao.getMostRecentDate(song.id)
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
fun SongList(navController: NavController, artistId: Long, songDao: SongDao, songScoreDao: SongScoreDao, artistDao: ArtistDao) {
    val songsFlow = songDao.getSongsByArtist(artistId)
    val songs = songsFlow.collectAsState(initial = listOf()).value
    val songDatum = convertToSongDataList(songScoreDao, songs)

    val columns = listOf(
        TableColumn<SongData>("タイトル",
            {
                val scrollState = rememberScrollState()
                Text(text = it.title, modifier = Modifier.horizontalScroll(scrollState))
            } ,
            compareBy{ it.title },
            2f
        ),
        TableColumn<SongData>("最高スコア",
            { Text(text = String.format("%.3f", it.highestScore), textAlign = TextAlign.Center) },
            compareBy{ it.highestScore },
            2f
        ),
        TableColumn<SongData>("最後に歌った日",
            { Text(text = it.lastDate.toString(), textAlign = TextAlign.Center) },
            compareBy { it.lastDate },
            2f
        )
    )
    Column {
        val artistName = artistDao.getNameById(artistId) ?: ""
        Text(text = "${artistName}の曲一覧", fontSize = 24.sp)
        Divider(color = Color.Gray, thickness = 1.dp)
        SortableTable(items = songDatum, columns = columns) { item ->
            navController.navigate("song_data/${item.id}")
        }
    }
}
