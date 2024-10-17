package com.example.karaoke_note

import SortDirection
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Face3
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.karaoke_note.data.Artist
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.FilterSetting
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScoreDao
import com.example.karaoke_note.ui.component.SortMethod

@ExperimentalMaterial3Api
@Composable
fun ArtistsPage(
    navController: NavController,
    isArtistListSelected: MutableState<Boolean>,
    sortMethodOfAllSongs: MutableState<SortMethod>,
    artistDao: ArtistDao,
    songDao: SongDao,
    songScoreDao: SongScoreDao,
    filterSetting: FilterSetting,
    searchText: String,
) {
    val buttonWidth = 120
    val buttonInnerPadding = 4
    val buttonShape = 16
    val buttonTextSize = 12
    val artistsFlow = artistDao.getArtistsWithSongs()
    val artists by artistsFlow.collectAsState(initial = emptyList())
    val allSongsFlow = songDao.getAllSongsWithScores()
    val allSongs by allSongsFlow.collectAsState(initial = emptyList())

    Column {
        // 疑似的な Segmented Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row {
                OutlinedButton(
                    onClick = { isArtistListSelected.value = true },
                    modifier = Modifier
                        .width(buttonWidth.dp)
                        .defaultMinSize(minHeight = 1.dp),
                    contentPadding = PaddingValues(buttonInnerPadding.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isArtistListSelected.value) {
                            MaterialTheme.colorScheme.secondaryContainer
                        }
                        else {
                            MaterialTheme.colorScheme.surface
                        },
                        contentColor = if (isArtistListSelected.value) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        }
                        else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(
                        topStart = buttonShape.dp,
                        bottomStart = buttonShape.dp,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp
                    )
                ) {
                    Text(
                        text = "Artists (" + artists.size + ")",
                        modifier = Modifier,
                        fontSize = buttonTextSize.sp
                    )
                }
                OutlinedButton(
                    onClick = { isArtistListSelected.value = false },
                    modifier = Modifier
                        .width(buttonWidth.dp)
                        .defaultMinSize(minHeight = 1.dp)
                        .offset(x = (-1).dp),
                    contentPadding = PaddingValues(buttonInnerPadding.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isArtistListSelected.value) {
                            MaterialTheme.colorScheme.secondaryContainer
                        }
                        else {
                            MaterialTheme.colorScheme.surface
                        },
                        contentColor = if (!isArtistListSelected.value) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        }
                        else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        bottomStart = 0.dp,
                        topEnd = buttonShape.dp,
                        bottomEnd = buttonShape.dp,
                    )
                ) {
                    Text(
                        text = "All songs (" + allSongs.size + ")",
                        modifier = Modifier,
                        fontSize = buttonTextSize.sp
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            if (isArtistListSelected.value) {
                DisplayArtistsList(navController, artists, artistDao, songDao, searchText)
            }
            else {
                DisplayAllSongsList(navController, sortMethodOfAllSongs, allSongs, artistDao, songScoreDao, filterSetting)
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun DisplayArtistsList(
    navController: NavController,
    artists: List<Artist>,
    artistDao: ArtistDao,
    songDao: SongDao,
    searchText: String,
) {
    var sortDirection by remember { mutableStateOf(SortDirection.Asc) }
    var sortedArtists by remember(sortDirection, artists, searchText) { mutableStateOf(getSortedArtists(sortDirection, artists, searchText)) }

    Column {
        ArtistsListHeader(sortDirection) { newSortDirection ->
            sortDirection = newSortDirection
            sortedArtists = getSortedArtists(sortDirection, artists, searchText)
        }
        Box(
            modifier = Modifier
        ) {
            LazyColumn {
                itemsIndexed(sortedArtists) { _, artist ->
                    ArtistListItem(navController, artist, artistDao, songDao)
                }
            }
        }
    }
}

fun getSortedArtists(
    sortDirection: SortDirection,
    artists: List<Artist>,
    searchText: String
): List<Artist> {
    val sortedArtists = when (sortDirection) {
        SortDirection.None -> artists
        SortDirection.Asc -> artists.sortedBy(Artist::name)
        SortDirection.Desc -> artists.sortedByDescending(Artist::name)
    }
    return sortedArtists.filter { artist ->
        artist.name.contains(searchText)
    }
}


@Composable
fun ArtistsListHeader(
    sortDirection: SortDirection,
    onSortChanged: (SortDirection) -> Unit
) {
    val iconScale = 0.8f
    val iconPaddingValues = 8

    Row(Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val newArtistDirection = when (sortDirection) {
                        SortDirection.None, SortDirection.Desc -> SortDirection.Asc
                        else -> SortDirection.Desc
                    }
                    onSortChanged(newArtistDirection)
                }
        ) {
            when(sortDirection) {
                SortDirection.Asc -> Icon(
                    imageVector = Icons.Filled.ArrowUpward,
                    contentDescription = null,
                    modifier = Modifier
                        .scale(iconScale)
                        .align(Alignment.CenterEnd)
                        .padding(end = iconPaddingValues.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                SortDirection.Desc -> Icon(
                    imageVector = Icons.Filled.ArrowDownward,
                    contentDescription = null,
                    modifier = Modifier
                        .scale(iconScale)
                        .align(Alignment.CenterEnd)
                        .padding(end = iconPaddingValues.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                else -> {}
            }
        }
    }
}

fun getIcon(iconNumber: Int): ImageVector {
    val image: ImageVector = when (iconNumber) {
        1 -> Icons.Outlined.Circle
        2 -> Icons.Filled.Favorite
        3 -> Icons.Filled.FavoriteBorder
        4 -> Icons.Filled.Star
        5 -> Icons.Filled.StarBorder
        6 -> Icons.Filled.Face
        7 -> Icons.Filled.Face3
        else -> Icons.Filled.Circle
    }
    return image
}

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun ArtistListItem(
    navController: NavController,
    artist: Artist,
    artistDao: ArtistDao,
    songDao: SongDao
) {
    fun updateArtistIcon(artistId: Long, icon: Int) {
        artistDao.updateIcon(artistId, icon)
    }

    val haptics = LocalHapticFeedback.current
    var iconColorSelectorOpened by remember { mutableStateOf(false) }

    // アーティストごとに曲数を取得する
    val songsListFlow = songDao.getSongsWithScores(artist.id)
    val songList by songsListFlow.collectAsState(initial = listOf())
    val numberOfSongs = songList.size

    Column(
        modifier = Modifier.clickable {
            navController.navigate("song_list/${artist.id}")
        }
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = artist.name + "  (" + numberOfSongs + ")",
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingContent = {
                Icon(
                    imageVector = getIcon(artist.icon),
                    tint = MaterialTheme.colorScheme.primary,
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
        Divider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 1.dp
        )
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
                            updateArtistIcon(artist.id, index)
                            iconColorSelectorOpened = false
                        },
                        modifier = Modifier
                            .weight(1f)   // 均等に
                            .align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = getIcon(index),
                            contentDescription = null,
                            modifier = Modifier,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}