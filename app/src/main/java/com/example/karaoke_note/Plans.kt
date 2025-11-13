package com.example.karaoke_note

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScore
import com.example.karaoke_note.data.SongScoreDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

// artistId についている曲の数をチェックし、0個の場合はアーティストデータ自体を削除する
private fun deleteArtistData(
    artistId: Long,
    artistDao: ArtistDao,
    songDao: SongDao,
    scope: CoroutineScope
) {
    scope.launch {
        if (songDao.countSongsForArtist(artistId) == 0) {
            artistDao.delete(artistId)
        }
    }
}

// songId の曲についているスコアの数をチェックし、0個の場合は Song データ自体を削除する
private fun deleteSongData(
    songId: Long,
    songScoreDao: SongScoreDao,
    songDao: SongDao,
    scope: CoroutineScope
) {
    scope.launch {
        if (songScoreDao.countScoresForSong(songId) == 0) {
            songDao.delete(songId)
        }
    }
}

// Undo 機能で元に戻すこともしたいので関数名を remove にしている
private fun removePlansListItem(
    artistId: Long,
    songId: Long,
    scoreId: Long,
    artistDao: ArtistDao,
    songDao: SongDao,
    songScoreDao: SongScoreDao,
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    onUndo: () -> Unit
) {
    scope.launch {
        val snackbarResult = snackBarHostState.showSnackbar(
            message = "One data has been removed",
            actionLabel = "Undo",
            withDismissAction = true,
            duration = SnackbarDuration.Long
        )
        // スナックバーの Undo がタップされなかった場合にのみ削除処理を実行する
        if (snackbarResult != SnackbarResult.ActionPerformed) {
            // スコア ID を削除
            //   これだけでは一度 Plans に登録した SongID や ArtistID などは残るので、サジェスト機能で出てくる
            songScoreDao.deleteSongScore(scoreId)

            // SongID の削除
            deleteSongData(songId, songScoreDao, songDao, scope)
            // ArtistID の削除
            deleteArtistData(artistId, artistDao, songDao, scope)
        }
        else {
            // Undo が押された場合は onUndo コールバックを実行する
            onUndo()
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun PlansPage(
    songDao: SongDao,
    songScoreDao: SongScoreDao,
    artistDao: ArtistDao,
    showEntrySheetDialog: MutableState<Boolean>,
    editingSongScore: MutableState<SongScore?>,
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState
) {
    val songScoreFlow = songScoreDao.getAll0Scores()
    val songScoreList by songScoreFlow.collectAsState(initial = listOf())

    Column {
        Box(
            modifier = Modifier.weight(8f)
        ) {
            LazyColumn(
                modifier = Modifier
            ) {
                items(songScoreList, {songScore:SongScore -> songScore.id}) { songScore ->
                    val song = songDao.getSong(songScore.songId)
                    if (song != null) {
                        val artist = artistDao.getNameById(song.artistId)
                        if (artist != null) {
                            val localCoroutineScope = rememberCoroutineScope()
                            val isDeleted = remember { mutableStateOf(false) }

                            if (!isDeleted.value) {
                                // SwipeToDismissBox の状態を管理する State
                                val dismissState = rememberSwipeToDismissBoxState(
                                    initialValue = SwipeToDismissBoxValue.Settled,
                                    // スワイプが確定する距離的閾値を調整する
                                    // 本当なら速度的閾値も調整したいのだが・・・
                                    positionalThreshold = { it * 0.75f },
                                )
                                SwipeToDismissBox(
                                    state = dismissState,
                                    backgroundContent = {
                                        val color = when (dismissState.targetValue) {
                                            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                                            else -> MaterialTheme.colorScheme.background
                                        }
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(color)
                                                .padding(horizontal = 24.dp),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Delete,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                                modifier = Modifier
                                            )
                                        }
                                    },
                                    // 左から右へのスワイプを無効化する
                                    enableDismissFromStartToEnd = false,
                                    onDismiss = { dismissValue ->
                                        // 右から左へのスワイプが完了した場合
                                        if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                            // UI から即座にアイテムを削除する
                                            isDeleted.value = true

                                            // Plan データを削除する処理を非同期で実行する
                                            removePlansListItem(
                                                artistId = song.artistId,
                                                songId = songScore.songId,
                                                scoreId = songScore.id,
                                                artistDao = artistDao,
                                                songDao = songDao,
                                                songScoreDao = songScoreDao,
                                                scope = localCoroutineScope,
                                                snackBarHostState = snackBarHostState
                                            ) {
                                                // Undo が押されたら UI に再表示する
                                                isDeleted.value = false
                                            }
                                        }
                                    }
                                ) {
                                    // 表示するメインのコンテンツ
                                    PlansListItem(
                                        song,
                                        songScore,
                                        artist,
                                        showEntrySheetDialog,
                                        editingSongScore
                                    )
                                }
                            }
                        }
                    }
                    else {
                        // データベースが壊れている
                    }
                }
            }
        }
    }
}

@Composable
fun PlansListItem(
    song: Song,
    songScore: SongScore,
    artist: String,
    showEntrySheetDialog: MutableState<Boolean>,
    editingSongScore: MutableState<SongScore?>
) {
    val keyFormat = if (songScore.key != 0) { "%+d" } else { "%d" }
    val fontSize = listOf(12, 10)

    Column(modifier = Modifier) {
        ListItem(
            modifier = Modifier
                //.height(60.dp)
                .clickable {
                    // ListItem をタップしたときには、その仮登録データを初期データとして新規登録画面を起動する
                    editingSongScore.value = songScore.copy(date = LocalDate.now())
                    showEntrySheetDialog.value = true
                },
            headlineContent = {
                Text(
                    text = song.title,
                    modifier = Modifier
                        .padding(start = 20.dp, top = 2.dp, bottom = 2.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = fontSize[0].sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            supportingContent = {
                Text(
                    text = artist,
                    modifier = Modifier
                        .padding(start = 20.dp, top = 2.dp, bottom = 2.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = fontSize[1].sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            leadingContent = {},
            trailingContent = {
                Text(
                    text = String.format(keyFormat, songScore.key),
                    modifier = Modifier
                        .padding(top = 2.dp, end = 16.dp, bottom = 2.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = fontSize[0].sp,
                    textAlign = TextAlign.End,
                )
            },
            shadowElevation = 1.dp
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 1.dp
        )
    }
}