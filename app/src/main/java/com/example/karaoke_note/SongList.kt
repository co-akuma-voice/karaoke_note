package com.example.karaoke_note
import SortableTable
import TableColumn
import android.annotation.SuppressLint
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.FilterSetting
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScoreDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class SongData(
    val id: Long,
    val title: String,
    val highestScore: Float,
    val lastDate: LocalDate
)

fun convertToSongDataList(
    songScoreDao: SongScoreDao,
    songs: List<Song>,
    filterSetting: FilterSetting,
    searchText: String
): List<SongData> {
    return runBlocking(Dispatchers.IO) {
        songs.map { song ->
            async {
                val highestScoreEntry = songScoreDao.getHighestScoreBySongIdAndGameKinds(song.id, filterSetting.getSelectedGameKinds())
                val lastDate = songScoreDao.getMostRecentDate(song.id)
                SongData(
                    id = song.id,
                    title = song.title,
                    highestScore = highestScoreEntry?.score ?: 0f,
                    lastDate = lastDate ?: LocalDate.MIN
                )
            }
        }.filter {
            it.await().title.contains(searchText, ignoreCase = true)
        }.awaitAll()
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun SongList(
    navController: NavController,
    artistId: Long,
    songDao: SongDao,
    songScoreDao: SongScoreDao,
    artistDao: ArtistDao,
    filterSetting: FilterSetting,
    searchText: String,
    focusManagerOfSearchBar: FocusManager
) {
    fun onUpdate(artistId: Long, newTitle: String) {
        artistDao.updateName(artistId, newTitle)
    }

    val selectedGameKinds by remember(filterSetting) {
        derivedStateOf {
            filterSetting.getSelectedGameKinds()
        }
    }
    val songsFlow = songDao.getSongsWithScores(artistId)
    val songs = songsFlow.collectAsState(initial = listOf()).value
    val songDatum by remember(songs, selectedGameKinds, searchText) { mutableStateOf(convertToSongDataList(songScoreDao, songs, filterSetting, searchText)) }

    val artistName = artistDao.getNameById(artistId) ?: ""
    val formatter = DateTimeFormatter.ofPattern("yy/MM/dd")
    val titleFontSize = 20
    val dataFontSize = 18

    val columns = listOf(
        TableColumn<SongData>(
            title = "タイトル",
            {
                val scrollState = rememberScrollState()
                Text(
                    text = it.title,
                    modifier = Modifier.horizontalScroll(scrollState),
                    fontSize = dataFontSize.sp,
                    overflow = TextOverflow.Ellipsis
                )
            },
            compareBy { it.title },
            weight = 4f
        ),
        TableColumn<SongData>(
            title = "最高スコア",
            {
                Text(
                    text = String.format("%.3f", it.highestScore),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = dataFontSize.sp,
                    overflow = TextOverflow.Ellipsis
                )
            },
            compareBy { it.highestScore },
            weight = 3f
        ),
        TableColumn<SongData>(
            title = "最後に歌った日",
            {
                Text(
                    text = it.lastDate.format(formatter),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = dataFontSize.sp,
                    overflow = TextOverflow.Ellipsis
                )
            },
            compareBy { it.lastDate },
            weight = 4f
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
                    keyboardActions = KeyboardActions(
                        onDone = {
                            isEditing = false
                            onUpdate(artistId, text)
                        }
                    ),
                    textStyle = TextStyle(fontSize = titleFontSize.sp),
                    singleLine = true
                )
            } else {
                // 通常のテキスト表示
                Text(
                    text = "$text の曲一覧",
                    fontSize = titleFontSize.sp,
                )
            }
            IconButton(
                onClick = { isEditing = true }
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 1.dp
        )
        SortableTable(
            items = songDatum,
            columns = columns,
            onHeaderClick = { clearFocusFromSearchBar(focusManagerOfSearchBar) },
            onRowClick = { item ->
                clearFocusFromSearchBar(focusManagerOfSearchBar)
                navController.navigate("song_data/${item.id}")
            }
        )
    }
}
