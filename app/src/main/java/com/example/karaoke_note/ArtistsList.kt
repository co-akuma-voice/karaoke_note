package com.example.karaoke_note

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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

data class ArtistData(val name: String, val color: Color = Color.White)

val sampleArtist = listOf(
    ArtistData("Artist0", Color.Red),
    ArtistData("Artist123456789012345678901234567890", Color.Magenta),
    ArtistData("あいうえお", Color.Yellow),
    ArtistData("]-[|/34<#!", Color.Green),
    ArtistData("@@@@@#####", Color.Cyan),
    ArtistData("Artist5", Color.Blue),
    ArtistData("Artist6", Color.Black),
    ArtistData("Artist7", Color.Gray),
    ArtistData("Artist8", Color.White),
)

@ExperimentalMaterial3Api
@Composable
fun ArtistEntrance(artist: String) {
    Column {
        // 実際にはデータベースから、artistをもとにデータを探す

        Box(modifier = Modifier) {
            val artists = sampleArtist
            SortArtists(artists)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun SortArtists(artists: List<ArtistData>) {
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
                    ArtistsListDrawing(artists)
                }
            }
        }
    }
}

@Composable
fun ArtistsListHeader(sortDirection: SortDirection, onSortChanged: (SortDirection) -> Unit) {
    Row(Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth().clickable {
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
fun ArtistsListDrawing(artist: ArtistData) {
    Column {
        ListItem(
            headlineText = {
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