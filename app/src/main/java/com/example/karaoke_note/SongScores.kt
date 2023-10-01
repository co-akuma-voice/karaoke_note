package com.example.karaoke_note

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongScore
import com.example.karaoke_note.data.SongScoreDao
import java.time.LocalDate

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

@Composable
fun SongScores(song: Song, songScoreDao: SongScoreDao) {
    val scoresFlow = songScoreDao.getScoresForSong(song.id)
    val scores by scoresFlow.collectAsState(initial = emptyList())
    Column {
        Button(onClick = {
            val id = song.id
            val songScore1 = SongScore(
                songId = id,
                date = LocalDate.of(2023, 4, 25),
                score = 85.2f,
                key = 0,
                comment = "dummy1"
            )
            songScoreDao.insertSongScore(songScore1)
            val songScore2 = SongScore(
                songId = id,
                date = LocalDate.of(2023, 5, 25),
                score = 95.125f,
                key = -3,
                comment = "dummy2"
            )
            songScoreDao.insertSongScore(songScore2)
            val songScore3 = SongScore(
                songId = id,
                date = LocalDate.of(2023, 6, 5),
                score = 100f,
                key = 6,
                comment = ""
            )
            songScoreDao.insertSongScore(songScore3)
        }
        ) {
            Text("add")
        }
        Text(text = song.title + "/" + song.artist)
        ScoreTable(scores, songScoreDao)
    }
}

@Composable
fun ScoreTable(scores: List<SongScore>, songScoreDao: SongScoreDao) {
    var sortDirection by remember { mutableStateOf(SortDirection.None) }
    var sortColumn by remember { mutableStateOf(SortColumn.Date) }
    val sortedScores = remember(scores, sortColumn, sortDirection) {
        when (sortColumn) {
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

    Column {
        HeaderRow(sortColumn, sortDirection) { newSortColumn, newSortDirection ->
            sortColumn = newSortColumn
            sortDirection = newSortDirection
        }
        LazyColumn {
            itemsIndexed(sortedScores) { index, score ->
                ScoreRow(score, songScoreDao)
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
        Box(modifier = Modifier
            .weight(2f)
            .clickable {
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
        Box(modifier = Modifier
            .weight(1.5f)
            .clickable {
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
        Box(modifier = Modifier
            .weight(1f)
            .clickable {
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
        Box(modifier = Modifier.weight(0.5f)) {
            Text(text = "", modifier = Modifier.align(Alignment.CenterStart))
        }
    }
}


@Composable
fun ScoreRow(score: SongScore, songScoreDao: SongScoreDao) {
    Row(Modifier.fillMaxWidth()) {
        Text(text = score.date.toString(), modifier = Modifier.weight(2f))
        Text(text = String.format("%.3f", score.score), textAlign = TextAlign.End, modifier = Modifier.weight(1.5f))
        Text(text = score.key.toString(), textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
        Text(text = score.comment, modifier = Modifier.weight(3f))
        IconButton(onClick = {songScoreDao.deleteSongScore(score.id)}, modifier = Modifier.weight(0.5f)) {
            Icon(Icons.Filled.Delete, "delete")
        }
    }
}