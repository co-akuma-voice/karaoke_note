package com.example.karaoke_note

import SortDirection
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.karaoke_note.data.Artist
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.SongDao

@ExperimentalMaterial3Api
@Composable
fun ArtistsPage(navController: NavController, artistDao: ArtistDao, songDao: SongDao) {
    Column {
        Box(modifier = Modifier.weight(0.5f)) {
            Spacer(modifier = Modifier)
        }
        Box(modifier = Modifier.weight(9f)) {
            val artistsFlow = artistDao.getArtistsWithSongs()
            val artists by artistsFlow.collectAsState(initial = emptyList())
            SortArtists(navController, artists, artistDao, songDao)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun SortArtists(
    navController: NavController,
    artists: List<Artist>,
    artistDao: ArtistDao,
    songDao: SongDao
) {
    var sortDirection by remember { mutableStateOf(SortDirection.Asc) }
    var sortedArtists by remember(sortDirection, artists) { mutableStateOf(getSortedArtists(sortDirection, artists)) }

    Column {
        ArtistsListHeader(sortDirection) { newSortDirection ->
            sortDirection = newSortDirection
            sortedArtists = getSortedArtists(sortDirection, artists)
        }
        Box(
            modifier = Modifier
        ) {
            LazyColumn {
                itemsIndexed(sortedArtists) { _, artist ->
                    ArtistsListDrawing(navController, artist, artistDao, songDao)
                }
            }
        }
    }
}

fun getSortedArtists(sortDirection: SortDirection, artists: List<Artist>): List<Artist> {
    return when (sortDirection) {
        SortDirection.None -> artists
        SortDirection.Asc -> artists.sortedBy(Artist::name)
        SortDirection.Desc -> artists.sortedByDescending(Artist::name)
    }
}


@Composable
fun ArtistsListHeader(sortDirection: SortDirection, onSortChanged: (SortDirection) -> Unit) {
    Row(Modifier.fillMaxWidth()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val newArtistDirection = when (sortDirection) {
                    SortDirection.None, SortDirection.Desc -> SortDirection.Asc
                    else -> SortDirection.Desc
                }
                onSortChanged(newArtistDirection)
            }) {
            Text(text = "アーティスト", modifier = Modifier.align(Alignment.CenterStart))
            when(sortDirection) {
                SortDirection.Asc -> Text(
                    text = "↑",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
                SortDirection.Desc -> Text(
                    text = "↓",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
                else -> {}
            }
        }
    }
}

fun getARGB(colorNumber: Int): Color {
    var argb: Color = Color.Black
    when (colorNumber) {
        0 -> argb = Color.Black
        1 -> argb = Color.Red
        2 -> argb = Color(0xffff8000)
        3 -> argb = Color.Yellow
        4 -> argb = Color.Green
        5 -> argb = Color(0xff00ffff)
        6 -> argb = Color.Blue
        7 -> argb = Color(0xff800080)
    }
    return argb
}

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun ArtistsListDrawing(
    navController: NavController,
    artist: Artist,
    artistDao: ArtistDao,
    songDao: SongDao
) {
    fun onUpdate(artistId: Long, iconColor: Int) {
        artistDao.updateIconColor(artistId, iconColor)
    }
    val haptics = LocalHapticFeedback.current
    var iconColorSelectorOpened by remember { mutableStateOf(false) }

    // アーティストごとに曲数を取得する
    //   Plans にある曲もカウントされてしまう
    val songsListFlow = songDao.getSongsByArtist(artist.id)
    val songList by songsListFlow.collectAsState(initial = listOf())
    val numberOfSongs = songList.size

    Column (
        modifier = Modifier.clickable {
            navController.navigate("song_list/${artist.id}")
        }
    ){
        ListItem(
            headlineContent = {
                Text(
                    text = artist.name + "  (" + numberOfSongs + ")",
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    tint = Color(artist.iconColor),
                    contentDescription = null,
                    modifier = Modifier.combinedClickable(
                        onClick = {},
                        onLongClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            iconColorSelectorOpened = true
                        }
                    )
                )
            }
        )
        Divider(color = Color.Gray, thickness = 1.dp)
    }

    if (iconColorSelectorOpened) { // これもできれば切り出したいな
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp)
        ){
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (index in 0..7) { // 8色
                    IconButton(
                        onClick = {
                            onUpdate(artist.id, getARGB(index).toArgb())
                            iconColorSelectorOpened = false
                        },
                        modifier = Modifier
                            .weight(1f)   // 均等に
                            .align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            modifier = Modifier,
                            tint = getARGB(index)
                        )
                    }
                }
            }
        }
    }
}