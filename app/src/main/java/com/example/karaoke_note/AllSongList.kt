package com.example.karaoke_note

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScoreDao
import com.example.karaoke_note.ui.component.SortMethod
import com.example.karaoke_note.ui.component.SortMethodSelectorBox
import com.example.karaoke_note.ui.component.SortMethodSelectorItem


fun getSortedAllSongs(
    sortMethod: SortMethod,
    songs: List<Song>
): List<Song> {
    return when (sortMethod) {
        SortMethod.NameAsc -> songs.sortedBy { it.title }
        SortMethod.NameDesc -> songs.sortedByDescending { it.title }
        SortMethod.DateAsc-> songs
        SortMethod.DateDesc -> songs
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayAllSongsList(
    navController: NavController,
    sortMethod: MutableState<SortMethod>,
    songs: List<Song>,
    songDao: SongDao,
    songScoreDao: SongScoreDao
){
    var sortedAllSongs by remember(sortMethod, songs) { mutableStateOf(getSortedAllSongs(sortMethod.value, songs)) }

    Column {
        SortMethodSelector(sortMethod) {
            sortedAllSongs = getSortedAllSongs(it, songs)
        }
        Box(modifier = Modifier) {
            LazyColumn {
                itemsIndexed(sortedAllSongs) { _, song ->
                    AllSongsListItem(navController, song, songDao, songScoreDao)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortMethodSelector(
    sortMethod: MutableState<SortMethod>,
    onSortMethodChanged: (SortMethod) -> Unit
){
    var expanded by remember { mutableStateOf(false) }
    val sortMethodsList = enumValues<SortMethod>()

    val sortMethodHeight = 36
    val sortMethodFontSize = 6
    val horizontalPaddingValue = 10
    val verticalPaddingValue = 1

    Box(
        modifier = Modifier
            .padding(
                start = horizontalPaddingValue.dp,
                top = (verticalPaddingValue + 6).dp,
                end = 0.dp,
                bottom = 0.dp
            )
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            // 現在設定値の表示部分
            SortMethodSelectorBox(
                displayedSortMethod = sortMethod.value,
                height = sortMethodHeight,
                modifier = Modifier.menuAnchor(),
                isExpanded = expanded,
                startPaddingValue = 16    // もっと理屈で表せないかな？
            )
            // Menu として出てくる部分
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                sortMethodsList.forEach {
                    SortMethodSelectorItem(
                        sortMethod = it,
                        height = sortMethodHeight,
                        textSize = sortMethodFontSize,
                        textHorizontalPaddingValues = horizontalPaddingValue,
                    ){
                        sortMethod.value = it
                        onSortMethodChanged(sortMethod.value)
                        expanded = false
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun AllSongsListItem(
    navController: NavController,
    song: Song,
    songDao: SongDao,
    songScoreDao: SongScoreDao
) {
    Column {
        ListItem(
            modifier = Modifier
                .height(50.dp)
                .clickable {
                    navController.navigate("song_data/${song.id}")
                },
            headlineContent = {
                Column {
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
                }
            },
            leadingContent = {
            },
            supportingContent = {
                Text(
                    text = "Artist",
                    modifier = Modifier
                        .padding(top = 2.dp, end = 4.dp, bottom = 2.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            trailingContent = {
                Column {
                    Text(
                        text = "Highest Score",
                        modifier = Modifier
                            .padding(top = 0.dp, end = 16.dp, bottom = 2.dp)
                            .align(Alignment.End),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = "Latest Date",
                        modifier = Modifier
                            .padding(top = 2.dp, end = 16.dp, bottom = 2.dp)
                            .align(Alignment.End),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                    )
                }
            },
            shadowElevation = 1.dp
        )
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}