package com.example.karaoke_note

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class MarkerColor {
    White,
    Red,
    Orange,
    Yellow,
    Green,
    Aqua,
    Blue,
    Purple,
    Gray
}

data class ArtistData(val name: String, val color: MarkerColor = MarkerColor.White)

val sampleArtist = listOf(
    ArtistData("Artist0", MarkerColor.Red),
    ArtistData("Artist123456789012345678901234567890", MarkerColor.Orange),
    ArtistData("あいうえお", MarkerColor.Yellow),
    ArtistData("]-[|/34<#!", MarkerColor.Green),
    ArtistData("@@@@@#####", MarkerColor.Aqua),
    ArtistData("Artist5", MarkerColor.Blue),
    ArtistData("Artist6", MarkerColor.Purple),
    ArtistData("Artist7", MarkerColor.Gray),
    ArtistData("Artist8", MarkerColor.White),
)

@ExperimentalMaterial3Api
@Composable
fun ArtistsList(artist: String) {
    Column {
        Text("$artist")
        // 実際にはデータベースから、artistをもとにデータを探す

        Box(
            modifier = Modifier
                .weight(8f)
        ){
            val artists = sampleArtist
            ArtistsUI(artists)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ArtistsUI(artists: List<ArtistData>) {
    var sortedArtists by remember { mutableStateOf(artists) }
    var sortDirection by remember { mutableStateOf(SortDirection.None) }

    Column {
/*            HeaderRow(sortColumn, sortDirection) { newSortColumn, newSortDirection ->
                sortColumn = newSortColumn
                sortDirection = newSortDirection
                sortedArtists = when (0) {
                    SortColumn.Score -> {
                        when (sortDirection) {
                            SortDirection.None -> artists
                            SortDirection.Asc -> artists.sortedBy(SongScore::score)
                            SortDirection.Desc -> artists.sortedByDescending(SongScore::score)
                        }
                    }
                }
            }
 */
        Box(
            modifier = Modifier.weight(8f)
        ) {
            LazyColumn {
                itemsIndexed(sortedArtists) { index, artists ->
                    ArtistsList(artists)
/*                    if (index < artists.size - 1) {
                        Divider(color = Color.Gray, thickness = 1.dp)
                    }

 */
                }
            }
        }
    }
}
/*
@Composable
fun HeaderRow(sortColumn: SortColumn, sortDirection: SortDirection, onSortChanged: (SortColumn, SortDirection) -> Unit) {
    Row(Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.weight(2f).clickable {
            val newDirection = when (sortColumn) {
                SortColumn.Date -> when (sortDirection) {
                    SortDirection.None, SortDirection.Desc -> SortDirection.Asc
                    else -> SortDirection.Desc
                }
                else -> SortDirection.Asc
            }
            onSortChanged(SortColumn.Date, newDirection)
        }) {
            Text(text = "日付", modifier = Modifier.align(Alignment.CenterStart))
            when (sortColumn) {
                SortColumn.Date -> when (sortDirection) {
                    SortDirection.Asc -> Text(text = "↑", modifier = Modifier.align(Alignment.CenterEnd))
                    SortDirection.Desc -> Text(text = "↓", modifier = Modifier.align(Alignment.CenterEnd))
                    else -> {}
                }
                else -> {}
            }
        }
        Box(modifier = Modifier.weight(1.2f).clickable {
            val newDirection = when (sortColumn) {
                SortColumn.Score -> when (sortDirection) {
                    SortDirection.None, SortDirection.Desc -> SortDirection.Asc
                    else -> SortDirection.Desc
                }
                else -> SortDirection.Asc
            }
            onSortChanged(SortColumn.Score, newDirection)
        }) {
            Text(text = "点数", modifier = Modifier.align(Alignment.CenterStart))
            when (sortColumn) {
                SortColumn.Score -> when (sortDirection) {
                    SortDirection.Asc -> Text(text = "↑", modifier = Modifier.align(Alignment.CenterEnd))
                    SortDirection.Desc -> Text(text = "↓", modifier = Modifier.align(Alignment.CenterEnd))
                    else -> {}
                }
                else -> {}
            }
        }
        Box(modifier = Modifier.weight(1f).clickable {
            val newDirection = when (sortColumn) {
                SortColumn.Key -> when (sortDirection) {
                    SortDirection.None, SortDirection.Desc -> SortDirection.Asc
                    else -> SortDirection.Desc
                }
                else -> SortDirection.Asc
            }
            onSortChanged(SortColumn.Key, newDirection)
        }) {
            Text(text = "キー", modifier = Modifier.align(Alignment.CenterStart))
            when (sortColumn) {
                SortColumn.Key -> when (sortDirection) {
                    SortDirection.Asc -> Text(text = "↑", modifier = Modifier.align(Alignment.CenterEnd))
                    SortDirection.Desc -> Text(text = "↓", modifier = Modifier.align(Alignment.CenterEnd))
                    else -> {}
                }
                else -> {}
            }
        }
        Box(modifier = Modifier.weight(3f)) {
            Text(text = "コメント", modifier = Modifier.align(Alignment.CenterStart))
        }
    }
}
*/

@ExperimentalMaterial3Api
@Composable
fun ArtistsList(artist: ArtistData) {
    Column {
        ListItem(
            headlineText = {
                Text(text = artist.name)
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null
                )
            }
        )
        Divider(color = Color.Gray, thickness = 1.dp)
    }
/*
    Row(Modifier.fillMaxWidth()) {
        Text(text = score.date.toString(), modifier = Modifier.weight(2f))
        Text(text = String.format("%.3f", score.score), textAlign = TextAlign.End, modifier = Modifier.weight(1.2f))
        Text(text = score.key.toString(), textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
        Text(text = score.comment, modifier = Modifier.weight(3f))
    }
 */
}