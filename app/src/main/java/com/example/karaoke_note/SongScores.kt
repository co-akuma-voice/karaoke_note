package com.example.karaoke_note

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate

data class SongScore(val date: LocalDate, val score: Float, val key: Int, val comment: String)

enum class SortColumn {
    Date,
    Score,
    Key
}

enum class SortDirection {
    None,
    Asc,
    Desc,
}

val sampleSongScores = listOf(
    SongScore(LocalDate.of(2023, 4, 25), 85.2f, 0, "dummy1"),
    SongScore(LocalDate.of(2023, 5, 24), 92.7f, -3, "dummy2"),
    SongScore(LocalDate.of(2023, 6, 23), 78.5f, 0, ""),
)

@Composable
fun SongScores(song: String, artist: String) {
    Column {
        Text(text = "$song/$artist")
        // 実際にはデータベースから、songとartistをもとにデータを探す
        val scores = sampleSongScores
        ScoreTable(scores)
    }
}

@Composable
fun ScoreTable(scores: List<SongScore>) {
    var sortedScores by remember { mutableStateOf(scores) }
    var sortDirection by remember { mutableStateOf(SortDirection.None) }
    var sortColumn by remember { mutableStateOf(SortColumn.Date) }

    Column {
        HeaderRow(sortColumn, sortDirection) { newSortColumn, newSortDirection ->
            sortColumn = newSortColumn
            sortDirection = newSortDirection
            sortedScores = when (sortColumn) {
                SortColumn.Date -> {
                    when (sortDirection) {
                        SortDirection.None -> scores
                        SortDirection.Asc -> scores.sortedBy(SongScore::date)
                        SortDirection.Desc -> scores.sortedByDescending(SongScore::date)
                    }
                }
                SortColumn.Score -> {
                    when (sortDirection) {
                        SortDirection.None -> scores
                        SortDirection.Asc -> scores.sortedBy(SongScore::score)
                        SortDirection.Desc -> scores.sortedByDescending(SongScore::score)
                    }
                }
                SortColumn.Key-> {
                    when (sortDirection) {
                        SortDirection.None -> scores
                        SortDirection.Asc -> scores.sortedBy(SongScore::key)
                        SortDirection.Desc -> scores.sortedByDescending(SongScore::key)
                    }
                }
            }
        }
        LazyColumn {
            itemsIndexed(sortedScores) { index, score ->
                ScoreRow(score)
                if (index < scores.size - 1) {
                    Divider(color = Color.Gray, thickness = 1.dp)
                }
            }
        }
    }
}

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
        Box(modifier = Modifier.weight(1f).clickable {
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


@Composable
fun ScoreRow(score: SongScore) {
    Row(Modifier.fillMaxWidth()) {
        Text(text = score.date.toString(), modifier = Modifier.weight(2f))
        Text(text = String.format("%.3f", score.score), textAlign = TextAlign.End, modifier = Modifier.weight(1f))
        Text(text = score.key.toString(), textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
        Text(text = score.comment, modifier = Modifier.weight(3f))
    }
}