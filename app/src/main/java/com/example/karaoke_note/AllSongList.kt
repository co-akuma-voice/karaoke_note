package com.example.karaoke_note

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.FilterSetting
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongScoreDao
import com.example.karaoke_note.ui.component.SortMethod
import com.example.karaoke_note.ui.component.SortMethodSelectorBox
import com.example.karaoke_note.ui.component.SortMethodSelectorItem
import java.time.format.DateTimeFormatter

// Songの情報 + 最高スコア + 最新日付を表示するためのクラス
data class SongInfo(
    val song: Song,
    val mostRecentDate: String?,
    val highestScore: Float?
)

fun getSortedAllSongInfo(
    sortMethod: SortMethod,
    songs: List<Song>,
    songScoreDao: SongScoreDao,
    filterSetting: FilterSetting
): List<SongInfo> {
    val songInfo = songs.map { song ->
        val mostRecentDate = songScoreDao.getMostRecentDate(song.id)
        val highestScore = songScoreDao.getHighestScoreBySongIdAndGameKinds(song.id, filterSetting.getSelectedGameKinds())?.score
        SongInfo(song, mostRecentDate?.format(DateTimeFormatter.ofPattern("yy/MM/dd")), highestScore)
    }
    return when (sortMethod) {
        SortMethod.NameAsc -> songInfo.sortedBy { it.song.title }
        SortMethod.NameDesc -> songInfo.sortedByDescending { it.song.title }
        SortMethod.DateAsc-> songInfo.sortedBy { it.mostRecentDate }
        SortMethod.DateDesc -> songInfo.sortedByDescending { it.mostRecentDate }
        SortMethod.ScoreAsc -> songInfo.sortedBy { it.highestScore }
        SortMethod.ScoreDesc -> songInfo.sortedByDescending { it.highestScore }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayAllSongsList(
    navController: NavController,
    sortMethod: MutableState<SortMethod>,
    songs: List<Song>,
    artistDao: ArtistDao,
    songScoreDao: SongScoreDao,
    filterSetting: FilterSetting
){
    val selectedGameKinds by remember(filterSetting) {
        derivedStateOf {
            filterSetting.getSelectedGameKinds()
        }
    }
    val sortedAllSongInfo by remember(sortMethod.value, songs, selectedGameKinds) { mutableStateOf(getSortedAllSongInfo(sortMethod.value, songs, songScoreDao, filterSetting)) }

    Column {
        SortMethodSelector(sortMethod)
        Box(modifier = Modifier) {
            LazyColumn {
                itemsIndexed(sortedAllSongInfo) { _, songInfo ->
                    AllSongsListItem(navController, songInfo, artistDao)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortMethodSelector(
    sortMethod: MutableState<SortMethod>,
    onSortMethodChanged: (SortMethod) -> Unit = {},
){
    var expanded by remember { mutableStateOf(false) }
    val sortMethodsList = enumValues<SortMethod>()
    val sortMethodFontSize = 12
    val horizontalPaddingValue = 10
    val verticalPaddingValue = 1

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = horizontalPaddingValue.dp,
                top = (verticalPaddingValue + 6).dp,
                end = horizontalPaddingValue.dp,
                bottom = (verticalPaddingValue + 6).dp
            )
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            // 現在設定値の表示部分
            SortMethodSelectorBox(
                displayedSortMethod = sortMethod.value,
                modifier = Modifier.menuAnchor(),
                textSize = sortMethodFontSize,
                isExpanded = expanded,
            )
            // Menu として出てくる部分
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(150.dp)
            ) {
                sortMethodsList.forEach {
                    SortMethodSelectorItem(
                        sortMethod = it,
                        selectedSortMethod = sortMethod.value,
                        textSize = sortMethodFontSize,
                    ) {
                        sortMethod.value = it
                        onSortMethodChanged(sortMethod.value)
                        expanded = false
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@ExperimentalMaterial3Api
@Composable
fun AllSongsListItem(
    navController: NavController,
    songInfo: SongInfo,
    artistDao: ArtistDao,
) {
    val song = songInfo.song
    val artistName = artistDao.getNameById(song.artistId)
    val mostRecentDate = songInfo.mostRecentDate
    val highestScore = songInfo.highestScore

    Column {
        ListItem(
            modifier = Modifier
                .height(60.dp)
                .clickable {
                    navController.navigate("song_data/${song.id}")
                },
            headlineContent = {
                Text(
                    text = song.title,
                    modifier = Modifier
                        .padding(top = 2.dp, bottom = 2.dp)
                        .align(Alignment.Start),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            leadingContent = {
            },
            supportingContent = {
                if (artistName != null) {
                    Text(
                        text = artistName,
                        modifier = Modifier
                            .padding(top = 2.dp, end = 4.dp, bottom = 2.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 8.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            trailingContent = {
                Column {
                    if (mostRecentDate != null) {
                        Text(
                            text = mostRecentDate,
                            modifier = Modifier
                                .padding(top = 2.dp, end = 16.dp, bottom = 2.dp)
                                .align(Alignment.End),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                        )
                    }
                    if (highestScore != null) {
                        Text(
                            text = String.format("%.3f", highestScore),
                            modifier = Modifier
                                .padding(top = 0.dp, end = 16.dp, bottom = 2.dp)
                                .align(Alignment.End),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                        )
                    }
                }
            },
            shadowElevation = 1.dp
        )
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}