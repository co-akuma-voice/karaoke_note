import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
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
    val columns = listOf(
        TableColumn<SongData>("タイトル",
            { Text(text = it.title) } ,
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
    SortableTable(items = songs, columns = columns) { item ->
        navController.navigate("song_data/${item.id}")
    }
}
