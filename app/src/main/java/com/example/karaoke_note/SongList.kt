
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScoreDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    fun onUpdate(artistId: Long, newTitle: String) {
        artistDao.updateName(artistId, newTitle)
    }
    val songsFlow = songDao.getSongsWithScores(artistId)
    val songs = songsFlow.collectAsState(initial = listOf()).value
    val songDatum = convertToSongDataList(songScoreDao, songs)
    val artistName = artistDao.getNameById(artistId) ?: ""
    val formatter = DateTimeFormatter.ofPattern("yy/MM/dd")
    val fontSize = 18.sp

    val columns = listOf(
        TableColumn<SongData>("タイトル",
            {
                val scrollState = rememberScrollState()
                Text(text = it.title, modifier = Modifier.horizontalScroll(scrollState), fontSize = fontSize)
            } ,
            compareBy{ it.title },
            2f
        ),
        TableColumn<SongData>("最高スコア",
            { Text(text = String.format("%.3f", it.highestScore), textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth(), fontSize = fontSize) },
            compareBy{ it.highestScore },
            2f
        ),
        TableColumn<SongData>("最後に歌った日",
            { Text(text = it.lastDate.format(formatter), textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth(), fontSize = fontSize) },
            compareBy { it.lastDate },
            2f
        )
    )

    var text by remember { mutableStateOf(artistName) }
    var isEditing by remember { mutableStateOf(false) }
    Column {
        Row {
            if (isEditing) {
                // テキストフィールド表示
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        isEditing = false
                        onUpdate(artistId, text)
                    }),
                    textStyle = TextStyle(fontSize = 24.sp),
                    singleLine = true
                )
            } else {
                // 通常のテキスト表示
                Text(
                    text = "${text}の曲一覧",
                    fontSize = 24.sp,
                )
            }
            IconButton(onClick = { isEditing = true }) {
                Icon(Icons.Filled.Edit, contentDescription = null)
            }
        }
        Divider(color = Color.Gray, thickness = 1.dp)
        SortableTable(items = songDatum, columns = columns) { item ->
            navController.navigate("song_data/${item.id}")
        }
    }
}
