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
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongDao

data class ArtistData(val name: String, val color: Color = Color.White)

fun getUniqueArtistData(songs: List<Song>): List<ArtistData> {
    val artistDataSet = mutableSetOf<ArtistData>()
    for (song in songs) {
        val color = Color(song.iconColor)
        val artistData = ArtistData(song.artist, color)
        artistDataSet.add(artistData)
    }
    return artistDataSet.toList()
}

@ExperimentalMaterial3Api
@Composable
fun ArtistsPage(navController: NavController, songDao: SongDao) {
    Column {
        // 実際にはデータベースから、artistをもとにデータを探す

        Box(modifier = Modifier.weight(0.5f)) {
            Spacer(modifier = Modifier)
        }
        Box(modifier = Modifier.weight(9f)) {
            val songs = songDao.getAllSongs()
            val artists = getUniqueArtistData(songs)
            SortArtists(navController, artists)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun SortArtists(navController: NavController, artists: List<ArtistData>) {
    var sortedArtists by remember { mutableStateOf(artists) }
    var sortDirection by remember { mutableStateOf(SortDirection.Asc) }

    Column {
        ArtistsListHeader(sortDirection) { newSortDirection ->
            sortDirection = newSortDirection
            sortedArtists = when (sortDirection) {
                SortDirection.None -> artists
                SortDirection.Asc -> artists.sortedBy(ArtistData::name)
                SortDirection.Desc -> artists.sortedByDescending(ArtistData::name)
            }
        }
        Box(
            modifier = Modifier
        ) {
            LazyColumn {
                itemsIndexed(sortedArtists) { index, artists ->
                    ArtistsListDrawing(navController, artists)
                }
            }
        }
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
fun ArtistsListDrawing(navController: NavController, artist: ArtistData) {
    Column (
        modifier = Modifier.clickable {
            navController.navigate("song_list/${artist.name}")
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
                    tint = artist.color,
                    contentDescription = null
                )
            }
        )
        Divider(color = Color.Gray, thickness = 1.dp)
    }
}