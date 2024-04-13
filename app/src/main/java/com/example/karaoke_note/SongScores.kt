package com.example.karaoke_note

import SortableTable
import TableColumn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScore
import com.example.karaoke_note.data.SongScoreDao
import com.example.karaoke_note.ui.component.SongScoreDetailDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@Composable
fun SongScores(
    song: Song,
    songDao: SongDao,
    songScoreDao: SongScoreDao,
    scope: CoroutineScope,
    showEntrySheetDialog: MutableState<Boolean>,
    editingSongScore: MutableState<SongScore?>
) {
    fun onUpdate(songId: Long, newTitle: String) {
        scope.launch {
            songDao.updateTitle(songId, newTitle)
        }
    }
    val scoresFlow = songScoreDao.getScoresForSong(song.id)
    val scores by scoresFlow.collectAsState(initial = emptyList())
    val selectedScoreId = remember { mutableStateOf<Long?>(null) }
    val formatter = DateTimeFormatter.ofPattern("yy/MM/dd")
    val fontSize = 18.sp
    var openDetailDialog by remember { mutableStateOf(false) }
    val selectedScore = remember { mutableStateOf<SongScore?>(null) }

    val columns = listOf(
        TableColumn<SongScore>("日付",
            { Text(text = it.date.format(formatter), fontSize = fontSize)},
            compareBy{ it.date },
            2f
        ),
        TableColumn("点数",
            { Text(text = String.format("%.3f", it.score), textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth(), fontSize = fontSize) },
            compareBy{ it.score },
            1.5f
        ),
        TableColumn("キー",
            {
                val keyFormat = if (it.key != 0) { "%+d" } else { "%d" }
                Text(text = String.format(keyFormat, it.key), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontSize = fontSize)
            },
            compareBy{ it.key },
            1f
        ),
        TableColumn("コメント",
            {
                val scrollState = rememberScrollState()
                Text(text = it.comment, modifier = Modifier.horizontalScroll(scrollState), fontSize = fontSize)
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
                }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Filled.MoreVert, "menu", Modifier.size(24.dp))
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
                        selectedScoreId.value?.let { songScoreDao.deleteSongScore(it) }
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

    var text by remember { mutableStateOf(song.title) }
    var isEditing by remember { mutableStateOf(false) }
    Column {
        Row {
            if (isEditing) {
                // テキストフィールド表示
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        isEditing = false
                        onUpdate(song.id, text)
                    }),
                    textStyle = TextStyle(fontSize = 24.sp),
                    singleLine = true
                )
            } else {
                // 通常のテキスト表示
                Text(
                    text = "${text}のスコア一覧",
                    fontSize = 24.sp,
                )
            }
            IconButton(onClick = { isEditing = true }) {
                // 通常状態だと位置が下にずれて見えるが、編集状態だとぴったり真ん中になる
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null,
                )
            }
        }
        Divider(color = Color.Gray, thickness = 1.dp)
        SortableTable(items = scores, columns = columns) {
            openDetailDialog = true
            selectedScore.value = it
        }
    }
    
    if (openDetailDialog) {
        SongScoreDetailDialog(
            onDismissRequest = { openDetailDialog = false },
            songScore = selectedScore.value ?: scores[0],
        )
    }
}