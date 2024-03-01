package com.example.karaoke_note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.karaoke_note.data.Artist
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.GameKind
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScore
import com.example.karaoke_note.data.SongScoreDao
import com.example.karaoke_note.ui.component.CommonTextField
import com.example.karaoke_note.ui.component.ExposedGameSelectorBox
import com.example.karaoke_note.ui.component.ExposedGameSelectorItem
import com.example.karaoke_note.ui.component.getErrorSupportingTextForScoreField
import com.example.karaoke_note.ui.component.getErrorSupportingTextForTitleAndArtistField
import com.example.karaoke_note.ui.component.getLocalizedDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.roundToInt

private fun getDefaultValuesBasedOnRoute(
    backStackEntry: NavBackStackEntry?,
    songDao: SongDao
):Pair<Long, String> {
    val currentRoute = backStackEntry?.destination?.route

    return when {
        currentRoute?.startsWith("song_data/") == true -> {
            val songId = backStackEntry.arguments?.getString("songId")?.toLongOrNull()
            val song = if (songId != null) songDao.getSong(songId) else null
            Pair(song?.artistId ?: -1, song?.title ?: "")
        }
        currentRoute?.startsWith("song_list/") == true -> {
            val artistId = backStackEntry.arguments?.getString("artistId")?.toLongOrNull()
            if (artistId == null) {
                Pair(-1, "")
            } else {
                Pair(artistId, "")
            }
        }
        else -> Pair(-1, "")
    }
}

// データに無効値がないか判定する
fun isValid(
    titleErrorSupportingText: String,
    artistErrorSupportingText: String,
    scoreErrorSupportingText: String,
    isPlanning: Boolean
): Boolean {
    // errorSupportingText の有無で判断してしまおう
    // errorSupportingText が isNotBlank = True (ある) ということは空白または不正値があるということ
    return if (isPlanning) {
        !(titleErrorSupportingText.isNotBlank() ||
                artistErrorSupportingText.isNotBlank())
    }
    else {
        !(titleErrorSupportingText.isNotBlank() ||
                artistErrorSupportingText.isNotBlank() ||
                scoreErrorSupportingText.isNotBlank())
    }
}

// データをデータベースに登録する
fun entryToDataBase(
    editingSongScore: SongScore?,
    isComeFromPlansPage: Boolean,
    newTitle: String,
    newArtist: String,
    newGame: GameKind,
    newScore: String,
    isPlanning: Boolean,
    newKey: Float,
    newDate: LocalDate,
    newComment: String,
    songDao: SongDao,
    artistDao: ArtistDao,
    songScoreDao: SongScoreDao,
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState
){
    val newArtistId = artistDao.insert(
        Artist(
            name = newArtist,
            iconColor = Color.Black.toArgb()
        )
    )
    val newSongId = songDao.insertSong(
        Song(
            title = newTitle,
            artistId = newArtistId,
        )
    )
    // Plans ページから来た場合は既存の id があるが、マニュアル 0 指定する
    //   0 指定すると新しい番号で振り直される
    //   Long 型なら 2^63 - 1 (= 9,223,372,036,854,775,807) までいけるので多少無駄遣いしても問題ない
    val newSongScoreId = if (isComeFromPlansPage) { 0L } else { editingSongScore?.id ?: 0L }
    val newSongScore = SongScore(
        id = newSongScoreId,
        songId = newSongId,
        date = newDate,
        score = newScore.toFloat(),
        key = newKey.roundToInt(),
        comment = newComment,
        gameKind = newGame
    )
    scope.launch {
        if (editingSongScore == null) {  // 通常新規登録
            songScoreDao.insert(newSongScore)
        }
        else {
            if (isComeFromPlansPage && !isPlanning) {  // Plans に仮登録されている曲を編集して正式登録
                songScoreDao.insert(newSongScore)
                songScoreDao.deleteSongScore(editingSongScore.id)  // Plans 仮登録 id を削除
            }
            else {  // List ページの編集ボタンからの更新 or Plans 再仮登録
                songScoreDao.update(newSongScore)
            }
        }

        val snackBarMessage = if (isPlanning) {
            "Saved as plans."
        } else {
            "Saved."
        }
        snackBarHostState.showSnackbar(
            message = snackBarMessage,
            actionLabel = null,
            withDismissAction = true,
            duration = SnackbarDuration.Short
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterial3Api
@Composable
fun NewEntryScreen(
    navController: NavController,
    songDao: SongDao,
    songScoreDao: SongScoreDao,
    artistDao: ArtistDao,
    scope: CoroutineScope,
    screenOpened: MutableState<Boolean>,
    editingSongScoreState: MutableState<SongScore?>,
    snackBarHostState: SnackbarHostState
) {
    val editingSongScore = editingSongScoreState.value
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val parentPage: String? = currentBackStackEntry?.destination?.route
    val allArtistFlow = artistDao.getAllArtists()
    val allArtists by allArtistFlow.collectAsState(initial = emptyList())
    val allSongs = songDao.getAllSongs()

    val (defaultArtistId, defaultTitle) = if (editingSongScore == null) {
        // +ボタンによる新規登録
        getDefaultValuesBasedOnRoute(currentBackStackEntry, songDao)
    } else {
        // 既存スコア or 予定スコアの編集
        val artistId = editingSongScore.songId.let { songDao.getSong(it)?.artistId } ?: -1
        val title = editingSongScore.songId.let { songDao.getSong(it)?.title } ?: ""
        Pair(artistId, title)
    }

    var previousGameKind by rememberSaveable { mutableStateOf(GameKind.JOY_NATIONAL_SCORING_GP) }
    val defaultScore = editingSongScore?.score?.let { String.format("%.3f", it) } ?: ""
    val defaultKey = editingSongScore?.key?.toFloat() ?: 0f
    val defaultDate = editingSongScore?.date ?: LocalDate.now()
    val defaultComment = editingSongScore?.comment ?: ""
    val defaultGameKind = editingSongScore?.gameKind ?: previousGameKind

    val gamesList = enumValues<GameKind>()
    var expanded by remember { mutableStateOf(false) }
    val gameListFontSize = 10
    val gameListHeight = 56

    var newTitle by remember { mutableStateOf("") }
    var newArtist by remember { mutableStateOf("") }
    var newGame by remember { mutableStateOf(GameKind.JOY_NATIONAL_SCORING_GP) }  // あくまで初期値
    var newScore by remember { mutableStateOf("") }
    var isPlanning by remember(parentPage, editingSongScore) { mutableStateOf(parentPage == "plans" && editingSongScore == null) }
    var newKey by remember { mutableFloatStateOf(0f) }
    var newDate by remember { mutableStateOf(LocalDate.now()) }
    var newComment by remember { mutableStateOf("") }

    var errorSupportingTextTitle by remember { mutableStateOf("") }
    var errorSupportingTextArtist by remember { mutableStateOf("") }
    var errorSupportingTextScore by remember { mutableStateOf("") }
    var isSaveButtonEnabled by remember { mutableStateOf(false) }
    var isComeFromPlansPage = false

    LaunchedEffect(key1 = defaultArtistId, key2 = defaultTitle, key3 = editingSongScore) {
        isComeFromPlansPage = (defaultScore == "0.000")

        newArtist = artistDao.getNameById(defaultArtistId) ?: ""
        newTitle = defaultTitle
        newScore = if (defaultScore != "0.000") { defaultScore } else { "" }
        newKey = defaultKey
        newDate = defaultDate
        newComment = defaultComment
        newGame = defaultGameKind

        errorSupportingTextTitle = getErrorSupportingTextForTitleAndArtistField(newTitle)
        errorSupportingTextArtist = getErrorSupportingTextForTitleAndArtistField(newArtist)
        errorSupportingTextScore = getErrorSupportingTextForScoreField(newScore, isPlanning)
        isSaveButtonEnabled = isValid(errorSupportingTextTitle, errorSupportingTextArtist,
            errorSupportingTextScore, isPlanning)
    }

    val focusRequester = remember { FocusRequester() }

    val verticalPaddingValue = 1
    val horizontalPaddingValue = 10
    val fontSize = 16

    FloatingActionButton(
        onClick = { screenOpened.value = true },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Add",
            tint = Color.White,
        )
    }

    if (screenOpened.value) {
        Dialog(
            onDismissRequest = {
                screenOpened.value = false
                editingSongScoreState.value = null
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize()
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontalPaddingValue.dp, verticalPaddingValue.dp)
                    ) {
                        // キャンセル (×) ボタン
                        IconButton(
                            onClick = {
                                editingSongScoreState.value = null
                                newScore = ""
                                newKey = 0f
                                newDate = LocalDate.now()
                                newComment = ""
                                screenOpened.value = false
                            },
                            modifier = Modifier.align(Alignment.CenterStart),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "cancel",
                                modifier = Modifier
                                    .padding(horizontalPaddingValue.dp, verticalPaddingValue.dp)
                                    .size(16.dp)
                            )
                        }
                        // Save ボタン
                        TextButton(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            onClick = {
                                // 予約モードが true ならスコアを 0 とする。
                                if (isPlanning) { newScore = "0.000" }

                                // データベースへ登録
                                entryToDataBase(
                                    editingSongScore,
                                    isComeFromPlansPage,
                                    newTitle,
                                    newArtist,
                                    newGame,
                                    newScore,
                                    isPlanning,
                                    newKey,
                                    newDate,
                                    newComment,
                                    songDao,
                                    artistDao,
                                    songScoreDao,
                                    scope,
                                    snackBarHostState
                                )

                                editingSongScoreState.value = null
                                newScore = ""
                                newKey = 0f
                                newDate = LocalDate.now()
                                newComment = ""

                                isSaveButtonEnabled = false
                                screenOpened.value = false
                            },
                            enabled = isSaveButtonEnabled,
                        ) {
                            Text(
                                text = "Save",
                                color = if (isSaveButtonEnabled) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color.LightGray
                                }
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.spacedBy(verticalPaddingValue.dp)
                    ) {
                        item {
                            CommonTextField(
                                initValue = newTitle,
                                label = "Song",
                                horizontalPaddingValue = horizontalPaddingValue,
                                verticalPaddingValue = verticalPaddingValue,
                                isEmptyAllowed = false,
                                singleLine = true,
                                fontSize = fontSize,
                                errorSupportingText = errorSupportingTextTitle,
                                isEnabled = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Default,
                                focusRequester = focusRequester,
                                autoCompleteSuggestions = allSongs.map { it.title },
                                { inputText ->
                                    newTitle = inputText
                                    errorSupportingTextTitle = getErrorSupportingTextForTitleAndArtistField(newTitle)
                                    isSaveButtonEnabled = isValid(errorSupportingTextTitle,
                                        errorSupportingTextArtist, errorSupportingTextScore, isPlanning)
                                },
                                {
                                    newTitle = ""
                                    errorSupportingTextTitle = getErrorSupportingTextForTitleAndArtistField(newTitle)
                                    isSaveButtonEnabled = isValid(errorSupportingTextTitle,
                                        errorSupportingTextArtist, errorSupportingTextScore, isPlanning)
                                },
                            )
                        }

                        item {
                            CommonTextField(
                                initValue = newArtist,
                                label = "Artist",
                                horizontalPaddingValue = horizontalPaddingValue,
                                verticalPaddingValue = verticalPaddingValue,
                                isEmptyAllowed = false,
                                singleLine = true,
                                fontSize = fontSize,
                                errorSupportingText = errorSupportingTextArtist,
                                isEnabled = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Default,
                                focusRequester = focusRequester,
                                autoCompleteSuggestions = allArtists.map { it.name },
                                { inputText ->
                                    newArtist = inputText
                                    errorSupportingTextArtist = getErrorSupportingTextForTitleAndArtistField(newArtist)
                                    isSaveButtonEnabled = isValid(errorSupportingTextTitle,
                                        errorSupportingTextArtist, errorSupportingTextScore, isPlanning)
                                },
                                {
                                    newArtist = ""
                                    errorSupportingTextArtist = getErrorSupportingTextForTitleAndArtistField(newArtist)
                                    isSaveButtonEnabled = isValid(errorSupportingTextTitle,
                                        errorSupportingTextArtist, errorSupportingTextScore, isPlanning)
                                }
                            )
                        }

                        item {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(
                                            start = horizontalPaddingValue.dp,
                                            top = (verticalPaddingValue + 6).dp,
                                            end = 0.dp,
                                            bottom = 0.dp
                                        )
                                        .weight(1f)
                                ) {
                                    ExposedDropdownMenuBox(
                                        expanded = expanded,
                                        onExpandedChange = { expanded = !expanded },
                                    ) {
                                        // 現在設定値の表示部分
                                        ExposedGameSelectorBox(
                                            initialGameKind = newGame,
                                            height = gameListHeight,
                                            modifier = Modifier.menuAnchor(),
                                            isExpanded = expanded,
                                            startPaddingValue = 16    // もっと理屈で表せないかな？
                                        )
                                        // Menu として出てくる部分
                                        ExposedDropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            gamesList.forEach {
                                                ExposedGameSelectorItem(
                                                    gameKind = it,
                                                    height = gameListHeight,
                                                    textSize = gameListFontSize,
                                                    textHorizontalPaddingValues = horizontalPaddingValue,
                                                ){
                                                    newGame = it
                                                    previousGameKind = newGame
                                                    expanded = false
                                                }
                                            }
                                        }
                                    }
                                }
                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    CommonTextField(
                                        initValue = newScore,
                                        label = "Score",
                                        horizontalPaddingValue = horizontalPaddingValue,
                                        verticalPaddingValue = 0,
                                        isEmptyAllowed = false,
                                        singleLine = true,
                                        fontSize = fontSize,
                                        errorSupportingText = errorSupportingTextScore,
                                        isEnabled = !isPlanning,
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Default,
                                        focusRequester = focusRequester,
                                        autoCompleteSuggestions = emptyList(),
                                        { inputText ->
                                            newScore = inputText
                                            errorSupportingTextScore = getErrorSupportingTextForScoreField(newScore, isPlanning)
                                            isSaveButtonEnabled = isValid(errorSupportingTextTitle,
                                                errorSupportingTextArtist, errorSupportingTextScore, isPlanning)
                                        },
                                        {
                                            newScore = ""
                                            errorSupportingTextScore = getErrorSupportingTextForScoreField(newScore, isPlanning)
                                            isSaveButtonEnabled = isValid(errorSupportingTextTitle,
                                                errorSupportingTextArtist, errorSupportingTextScore, isPlanning)
                                        }
                                    )
                                }
                            }
                        }

                        // Plans スイッチ
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Entry as plans",
                                    fontSize = (fontSize * 0.6).sp
                                )
                                Switch(
                                    checked = isPlanning,
                                    onCheckedChange = {
                                        isPlanning = it
                                        isSaveButtonEnabled = isValid(errorSupportingTextTitle, errorSupportingTextArtist,
                                            errorSupportingTextScore, it)
                                    },
                                    modifier = Modifier.scale(0.5f)
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Box {
                                    Text(
                                        text = "Key",
                                        modifier = Modifier
                                            .padding(
                                                start = (horizontalPaddingValue * 2).dp,
                                                top = (verticalPaddingValue).dp
                                            ),
                                        fontSize = fontSize.sp
                                    )
                                }
                                Column {
                                    BoxWithConstraints(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                start = (horizontalPaddingValue * 2).dp,
                                                top = verticalPaddingValue.dp,
                                                end = (horizontalPaddingValue * 2).dp
                                            )
                                    ) {
                                        val newKeyText: String
                                        val newKeyLabel = newKey.roundToInt()
                                        newKeyText = if (newKeyLabel > 0) {
                                            "+$newKeyLabel"
                                        } else {
                                            "$newKeyLabel"
                                        }
                                        Text(
                                            text = newKeyText,
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .offset(x = ((maxWidth / 15) * newKey)),
                                            fontSize = fontSize.sp
                                        )
                                    }
                                    Slider(
                                        value = newKey,
                                        onValueChange = { newKey = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                horizontal = (horizontalPaddingValue * 2).dp,
                                                vertical = 0.dp
                                            ),
                                        valueRange = -7f..7f,
                                        steps = 13,
                                    )
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .padding(
                                        start = (horizontalPaddingValue * 2).dp,
                                        top = verticalPaddingValue.dp
                                    ),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(text = "Date")
                                // 日付表示とカレンダーマーク
                                newDate = getLocalizedDate(defaultDate)
                            }
                        }

                        item {
                            CommonTextField(
                                initValue = newComment,
                                label = "Comment",
                                horizontalPaddingValue = horizontalPaddingValue,
                                verticalPaddingValue = verticalPaddingValue,
                                isEmptyAllowed = true,
                                singleLine = false,
                                fontSize = fontSize,
                                errorSupportingText = "",
                                isEnabled = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Default,
                                focusRequester = focusRequester,
                                autoCompleteSuggestions = emptyList(),
                                { inputText -> newComment = inputText },
                                { newComment = "" }
                            )
                        }
                    }
                }
            }
        }
    }
}

