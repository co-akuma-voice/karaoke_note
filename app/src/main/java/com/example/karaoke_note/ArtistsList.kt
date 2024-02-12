package com.example.karaoke_note

import SortDirection
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.karaoke_note.data.Artist
import com.example.karaoke_note.data.ArtistDao

@ExperimentalMaterial3Api
@Composable
fun ArtistsPage(navController: NavController, artistDao: ArtistDao) {
    Column {
        Box(modifier = Modifier.weight(0.5f)) {
            Spacer(modifier = Modifier)
        }
        Box(modifier = Modifier.weight(9f)) {
            val artistsFlow = artistDao.getArtistsWithSongs()
            val artists by artistsFlow.collectAsState(initial = emptyList())
            SortArtists(navController, artists)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun SortArtists(navController: NavController, artists: List<Artist>) {
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
                itemsIndexed(sortedArtists) { index, artist ->
                    ArtistsListDrawing(navController, artist)
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

@ExperimentalMaterial3Api
@Composable
fun ArtistsListDrawing(navController: NavController, artist: Artist) {
    Column (
        modifier = Modifier.clickable {
            navController.navigate("song_list/${artist.id}")
        }
    ){
        ListItem(
            headlineContent = {
                Text(
                    text = artist.name,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    tint = Color(artist.iconColor),
                    contentDescription = null
                )
            }
        )
        Divider(color = Color.Gray, thickness = 1.dp)
    }
}