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
import androidx.compose.material.Icon
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

fun deleteSongScore(songId: Long, scoreId: Long, scope: CoroutineScope, songDao: SongDao, songScoreDao: SongScoreDao) {
    scope.launch {
        songScoreDao.deleteSongScore(scoreId)
        if (songScoreDao.countScoresForSong(songId) == 0) {
            songDao.delete(songId)
        }
    }
}

@Composable
fun SongScores(song: Song, songDao: SongDao, songScoreDao: SongScoreDao, scope: CoroutineScope, showEntrySheetDialog: MutableState<Boolean>, editingSongScore: MutableState<SongScore?>) {
    val scoresFlow = songScoreDao.getScoresForSong(song.id)
    val scores by scoresFlow.collectAsState(initial = emptyList())
    val selectedScoreId = remember { mutableStateOf<Long?>(null) }

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
        TableColumn("",
            { songScore ->
                val expanded = remember { mutableStateOf(false) }
                IconButton(onClick = {
                    expanded.value = true
                    selectedScoreId.value = songScore.id
                }, modifier = Modifier.size(22.dp)) {
                    Icon(Icons.Filled.MoreVert, "menu", Modifier.size(22.dp))
                }
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    DropdownMenuItem(onClick = {
                        expanded.value = false
                        showEntrySheetDialog.value = true
                        editingSongScore.value = songScore
                    }) {
                        Text("編集")
                    }
                    DropdownMenuItem(onClick = {
                        selectedScoreId.value?.let { deleteSongScore(song.id, it, scope, songDao, songScoreDao) }
                        expanded.value = false
                    }) {
                        Text("削除")
                    }
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