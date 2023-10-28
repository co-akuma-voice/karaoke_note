package com.example.karaoke_note

import SortableTable
import TableColumn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.Icon
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScore
import com.example.karaoke_note.data.SongScoreDao
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope



@Composable
fun SongScores(song: Song, songDao: SongDao, songScoreDao: SongScoreDao, scope: CoroutineScope) {
    val scoresFlow = songScoreDao.getScoresForSong(song.id)
    val scores by scoresFlow.collectAsState(initial = emptyList())
    fun deleteSongScore(scoreId: Long) {
        scope.launch {
            songScoreDao.deleteSongScore(scoreId)
            if (songScoreDao.countScoresForSong(song.id) == 0) {
                songDao.delete(song.id)
            }
        }
    }

    val columns = listOf(
        TableColumn<SongScore>("日付",
            { Text(text = it.date.toString()) },
            compareBy{ it.date },
            2f
        ),
        TableColumn("点数",
            { Text(text = String.format("%.3f", it.score), textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()) },
            compareBy{ it.score },
            1.5f
        ),
        TableColumn("キー",
            { Text(text = it.key.toString(), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            compareBy{ it.key },
            1f
        ),
        TableColumn("コメント",
            {
                val scrollState = rememberScrollState()
                Text(text = it.comment, modifier = Modifier.horizontalScroll(scrollState))
            },
            compareBy{ it.comment.length },
            3f
        ),
        TableColumn("削除",
            {
                IconButton(onClick = {deleteSongScore(it.id)}, modifier = Modifier.size(22.dp) ) {
                    Icon(Icons.Filled.Delete, "delete", Modifier.size(22.dp))
                }
            },
            null,
        1f
        )
    )

    Column {
        Text(text = "${song.title}のスコア一覧", fontSize = 24.sp)
        Divider(color = Color.Gray, thickness = 1.dp)
        SortableTable(items = scores, columns = columns)
    }
}