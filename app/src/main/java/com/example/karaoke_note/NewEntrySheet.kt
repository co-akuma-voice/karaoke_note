package com.example.karaoke_note

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.karaoke_note.data.Artist
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.GameKind
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScore
import com.example.karaoke_note.data.SongScoreDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun CommonTextField(
    initValue: String,
    label: String,
    horizontalPaddingValue: Int,
    verticalPaddingValue: Int,
    isEmptyAllowed: Boolean,      // TextField を空欄にすることを許可するかどうか
    singleLine: Boolean,
    fontSize: Int,
    normalSupportingText: String,
    errorSupportingText: String,
    isEnabled: Boolean,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    focusRequester: FocusRequester,
    onChange: (String) -> Unit,
    onClear: () -> Unit
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(initValue)) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val invalidValue by remember { derivedStateOf { textFieldValue.text.isEmpty() } }
    val trailingIconSize = 20

    LaunchedEffect(initValue) {
        if (textFieldValue.text != initValue) {
            textFieldValue = textFieldValue.copy(text = initValue)
        }
    }

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { inputText ->
            textFieldValue = inputText
            onChange(inputText.text)
        },
        modifier = Modifier
            .bringIntoViewRequester(bringIntoViewRequester)
            .focusRequester(focusRequester)
            .fillMaxWidth()
            .padding(horizontalPaddingValue.dp, verticalPaddingValue.dp)
            .imePadding(),
        label = { Text(label) },
        isError = isEnabled && !isEmptyAllowed && invalidValue,
        supportingText = {
            if (isEnabled && !isEmptyAllowed && textFieldValue.text.isEmpty()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = errorSupportingText,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = normalSupportingText,
                    color = MaterialTheme.colorScheme.background
                )
            }
        },
        enabled = isEnabled,
        textStyle = TextStyle(fontSize = fontSize.sp),
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        trailingIcon = {
            if (isEnabled && !isEmptyAllowed && textFieldValue.text.isEmpty()) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = "error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(trailingIconSize.dp)
                )
            }
            else {
                IconButton(
                    onClick = {
                        textFieldValue = TextFieldValue("")
                        onClear()
                    }
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "clear",
                        modifier = Modifier.size(trailingIconSize.dp)
                    )
                }
            }
        },
    )
}

@Composable
fun ExposedGameSelectorBox(
    initialGameKind: GameKind,
    height: Int,
    modifier: Modifier,    // Card の Modifier
    isExpanded: Boolean,
    startPaddingValue: Int,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 4.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            ),
            modifier = modifier
                .fillMaxWidth()
                .height(height.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.padding(start = startPaddingValue.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(
                            getPainterResourceIdOfBrandImage(
                                initialGameKind.name.take(3)
                            )
                        ),
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Image(
                        painter = painterResource(
                            getPainterResourceIdOfGameImage(
                                initialGameKind.name
                            )
                        ),
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
                Box(
                    modifier = Modifier
                ) {
                    // Trailing icon の代わり
                    val arrowIcon: ImageVector = if (isExpanded) {
                        Icons.Filled.ArrowDropUp
                    } else {
                        Icons.Filled.ArrowDropDown
                    }
                    Icon(
                        imageVector = arrowIcon,
                        contentDescription = null,
                    )
                }
            }
        }
        // TextField っぽく見せるために下枠のみ線を入れる
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun ExposedGameSelectorItem(
    gameKind: GameKind,
    height: Int,
    textSize: Int,
    textHorizontalPaddingValues: Int,
    onClick: () -> Unit
){
    DropdownMenuItem(
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    getPainterResourceIdOfBrandImage(
                        gameKind.name.take(3)
                    )
                ),
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Image(
                painter = painterResource(
                    getPainterResourceIdOfGameImage(gameKind.name)
                ),
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = "(" + gameKind.displayName + ")",
                fontSize = textSize.sp,
                modifier = Modifier.padding(start = textHorizontalPaddingValues.dp)
            )
        }
    }
}

fun getNumberOfDecimalPoints(str: String): Int {
    return str.count { it == '.' }
}
fun getIntegerPartOfScore(str: String): String {
    val strArr = str.split(".").map { it.trim() }
    return strArr[0]
}
fun getDecimalPartOfScore(str: String): String {
    val strArr = str.split(".").map { it.trim() }
    return strArr[1]
}
fun isValid(title: String, artist: String, score: String, isPlanning: Boolean): Pair<Boolean, String> {
    var valid = true
    var message = ""

    // タイトルやアーティスト名で問題になるのは空白ぐらい?
    if (title.isBlank()) {
        valid = false
        message += "[Title] Blank is not allowed.\n"
    }
    if (artist.isBlank()) {
        valid = false
        message += "[Artist] Blank is not allowed.\n"
    }
    // スコア欄は予約フラグとの兼ね合いがある
    if (!isPlanning) {
        if (score.isBlank()) {
            valid = false
            message += "[Score] Blank is not allowed.\n"
        }
        else {
            // スコア欄固有のチェック
            val numOfDecimalPoint = getNumberOfDecimalPoints(score)
            if (numOfDecimalPoint != 1) {
                valid = false
                message += "[Score] Only 1 decimal point is allowed."
            }
            else {
                // 整数部、小数部をチェック
                // 小数部は桁数が3桁に足りない場合は末尾に 0 を補充する
                val strIntegerPart = getIntegerPartOfScore(score)
                val integerPart: Int? = strIntegerPart.toIntOrNull()
                val strDecimalPart: String = getDecimalPartOfScore(score)
                val decimalPart: Int? = strDecimalPart.toIntOrNull()

                if (integerPart != null && decimalPart != null) {
                    // 100 点以上の扱い
                    if ((integerPart > 100) || ((integerPart == 100) && (decimalPart != 0))) {
                        valid = false
                        message += "[Score] Too high score.\n"
                    }
                    else if ((integerPart < 0)) {
                        // 負の数の扱い
                        valid = false
                        message += "[Score] Negative value is not allowed."
                    }
                    else {
                        // 99.4444 などの変な形の時
                        if (strDecimalPart.length != 3) {
                            valid = false
                            message += "[Score] The decimal part must have 3 digits."
                        }
                    }
                }
                else {
                    // .123 や ,.,,, みたいな形の時
                    valid = false
                    message += "[Score] There are some invalid characters."
                }
            }
        }
    }

    return valid to message
}

@ExperimentalMaterial3Api
@Composable
fun rememberCustomDatePickerState(
    @Suppress("AutoBoxing") initialSelectedDateMillis: Long? = null,
    @Suppress("AutoBoxing") initialDisplayedMonthMillis: Long? = initialSelectedDateMillis,
    yearRange: IntRange = DatePickerDefaults.YearRange,
    initialDisplayMode: DisplayMode = DisplayMode.Picker
): Pair<DatePickerState, DatePickerState> {
    val datePickerState = rememberSaveable(
        saver = DatePickerState.Saver()
    ){
        DatePickerState(
            initialSelectedDateMillis = initialSelectedDateMillis,
            initialDisplayedMonthMillis = initialDisplayedMonthMillis,
            yearRange = yearRange,
            initialDisplayMode = initialDisplayMode
        )
    }
    val pendingDatePickerState = rememberSaveable(
        saver = DatePickerState.Saver()
    ){
        DatePickerState(
            initialSelectedDateMillis = initialSelectedDateMillis,
            initialDisplayedMonthMillis = initialDisplayedMonthMillis,
            yearRange = yearRange,
            initialDisplayMode = initialDisplayMode
        )
    }
    return datePickerState to pendingDatePickerState
}

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterial3Api
@Composable
fun getLocalizedDate(defaultDate: LocalDate): LocalDate {
    var showPicker by remember { mutableStateOf(false) }
    val defaultZone = ZoneId.systemDefault()
    // UTC+0 とシステムデフォルトとの時差をミリ秒単位にしたもの
    val mSecondFromUTC = defaultDate.atStartOfDay(defaultZone).offset.totalSeconds * 1000
    val (datePickerState, pendingDatePickerState) = rememberCustomDatePickerState(
        // toInstant(): Java の Instant 型 (エポック秒 = UNIX 時間を保持する) に変換する。
        //              ただし、表示形式は UNIX 時間ではない。
        //              このとき、タイムゾーン情報が UTC+0 になる。
        // toEpochMilli(): UNIX 時間形式 (ミリ秒) に変換する。
        // mSecondFromUTC を足すことで無理やり Zoned 時刻にする
        initialSelectedDateMillis = (defaultDate.atStartOfDay(defaultZone).toInstant().toEpochMilli() + mSecondFromUTC)
    )
    var localizedNullableSelectedDate: LocalDate?
    var localizedSelectedDate: LocalDate = defaultDate

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            localizedNullableSelectedDate = datePickerState.selectedDateMillis?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            }
            localizedSelectedDate = localizedNullableSelectedDate ?: defaultDate

            // 現在設定されている日付を描画する
            Text(
                text = localizedSelectedDate.toString(),
                modifier = Modifier
                    .padding(end = 20.dp)
            )
            // カレンダーボタン
            IconButton(
                onClick = { showPicker = true }
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        }
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = {
                showPicker = false
                pendingDatePickerState.setSelection(datePickerState.selectedDateMillis)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingDatePickerState.setSelection(datePickerState.selectedDateMillis)
                        showPicker = false
                    }
                ) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        datePickerState.setSelection(pendingDatePickerState.selectedDateMillis)
                        showPicker = false
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Sung date",
                        modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp)
                    )
                },
                showModeToggle = true
            )
        }
    }

    return localizedSelectedDate
}

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterial3Api
@Composable
fun NewEntryScreen(songDao: SongDao, songScoreDao: SongScoreDao, artistDao: ArtistDao, scope: CoroutineScope, screenOpened: MutableState<Boolean>, editingSongScoreState: MutableState<SongScore?>, snackBarHostState: SnackbarHostState) {
    val editingSongScore = editingSongScoreState.value
    var errorDialogOpened by remember { mutableStateOf(false) }

    val defaultArtistId = editingSongScore?.songId?.let { songDao.getSong(it)?.artistId } ?: -1
    val defaultTitle = editingSongScore?.songId?.let { songDao.getSong(it)?.title } ?: ""
    val defaultScore = editingSongScore?.score?.let { String.format("%.3f", it) } ?: ""
    val defaultKey = editingSongScore?.key?.toFloat() ?: 0f
    val defaultDate = editingSongScore?.date ?: LocalDate.now()
    val defaultComment = editingSongScore?.comment ?: ""
    val defaultGameKind = editingSongScore?.gameKind ?: GameKind.JOY_NATIONAL_SCORING_GP

    val gamesList = enumValues<GameKind>()
    var expanded by remember { mutableStateOf(false) }
    val gameListFontSize = 10
    val gameListHeight = 56

    var newArtist by remember { mutableStateOf("") }
    var newTitle by remember { mutableStateOf("") }
    var newGame by remember { mutableStateOf(gamesList[0]) }
    var newScore by remember { mutableStateOf("") }
    var isPlanning by remember { mutableStateOf(false) }
    var newKey by remember { mutableFloatStateOf(0f) }
    var newDate by remember { mutableStateOf(LocalDate.now()) }
    var newComment by remember { mutableStateOf("") }
    LaunchedEffect(key1 = defaultArtistId, key2 = defaultTitle, key3 = editingSongScore) {
        newArtist = artistDao.getNameById(defaultArtistId) ?: ""
        newTitle = defaultTitle
        newScore = defaultScore
        newKey = defaultKey
        newDate = defaultDate
        newComment = defaultComment
        newGame = defaultGameKind
    }

    val focusRequester = remember { FocusRequester() }

    val verticalPaddingValue = 1
    val horizontalPaddingValue = 10
    val fontSize = 16

    FloatingActionButton(
        onClick = { screenOpened.value = true },
        modifier = Modifier
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Add",
            tint = Color.White,
        )
    }

    if (errorDialogOpened) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(
                    onClick = { errorDialogOpened = false }
                ) {
                    Text("OK")
                }
            },
            dismissButton = null,
            title = {
                Text("Error")
            },
            text = {
                Text(isValid(newTitle, newArtist, newScore, isPlanning).second)
            }
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
                                    .padding(
                                        horizontalPaddingValue.dp,
                                        verticalPaddingValue.dp
                                    )
                                    .size(16.dp)
                            )
                        }
                        // Save ボタン
                        TextButton(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            onClick = {
                                // タイトル、アーティスト、スコア欄のチェック
                                if (!isValid(newTitle, newArtist, newScore, isPlanning).first) {
                                    errorDialogOpened = true
                                }
                                else {
                                    // 予約モードが true ならスコアを 0 とする。
                                    if (isPlanning) {
                                        newScore = "0.000"
                                    }

                                    // データを登録
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
                                    val newSongScore = SongScore(
                                        id = editingSongScore?.id ?: 0L,
                                        songId = newSongId,
                                        date = newDate,
                                        score = newScore.toFloat(),
                                        key = newKey.roundToInt(),
                                        comment = newComment,
                                        gameKind = newGame
                                    )
                                    scope.launch {
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

                                        if (editingSongScore == null) {
                                            songScoreDao.insert(newSongScore)
                                        } else {
                                            songScoreDao.update(newSongScore)
                                        }
                                    }
                                    editingSongScoreState.value = null
                                    newScore = ""
                                    newKey = 0f
                                    newDate = LocalDate.now()
                                    newComment = ""

                                    screenOpened.value = false
                                }
                            },
                        ) {
                            Text("Save")
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
                                normalSupportingText = "",
                                errorSupportingText = "Blank is not allowed.",
                                isEnabled = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Default,
                                focusRequester = focusRequester,
                                { inputText -> newTitle = inputText },
                                { newTitle = "" }
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
                                normalSupportingText = "",
                                errorSupportingText = "Blank is not allowed.",
                                isEnabled = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Default,
                                focusRequester = focusRequester,
                                { inputText -> newArtist = inputText },
                                { newArtist = "" }
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
                                        normalSupportingText = "",
                                        errorSupportingText = "Required",
                                        isEnabled = !isPlanning,
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Default,
                                        focusRequester = focusRequester,
                                        { inputText -> newScore = inputText },
                                        { newScore = "" }
                                    )
                                }
                            }
                        }

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
                                    onCheckedChange = { isPlanning = it },
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
                                normalSupportingText = "",
                                errorSupportingText = "",
                                isEnabled = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Default,
                                focusRequester = focusRequester,
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

