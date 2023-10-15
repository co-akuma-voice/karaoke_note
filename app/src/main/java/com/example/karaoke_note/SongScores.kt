package com.example.karaoke_note

import SortableTable
import TableColumn
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongScore
import com.example.karaoke_note.data.SongScoreDao

@Composable
fun SongScores(song: Song, songScoreDao: SongScoreDao) {
    val scoresFlow = songScoreDao.getScoresForSong(song.id)
    val scores by scoresFlow.collectAsState(initial = emptyList())

    val columns = listOf(
        TableColumn<SongScore>("日付",
            { Text(text = it.date.toString()) },
            compareBy{ it.date },
            2f
        ),
        TableColumn("点数",
            { Text(text = String.format("%.3f", it.score), textAlign = TextAlign.End) },
            compareBy{ it.score },
            1.5f
        ),
        TableColumn("キー",
            { Text(text = it.key.toString(), textAlign = TextAlign.Center) },
            compareBy{ it.key },
            1f
        ),
        TableColumn("コメント",
            { Text(text = it.comment) },
            compareBy{ it.comment.length },
            3f
        ),
        TableColumn("削除",
            {
                IconButton(onClick = { songScoreDao.deleteSongScore(it.id) }) {
                    Icon(Icons.Filled.Delete, "delete")
                }
            },
            null,
        1f
        )
    )

    SortableTable(items = scores, columns = columns)
}