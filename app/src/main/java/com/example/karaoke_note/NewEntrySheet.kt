package com.example.karaoke_note

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScore
import com.example.karaoke_note.data.SongScoreDao
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.roundToInt

@ExperimentalMaterial3Api
@Composable
fun CustomScoreTextField(
    value: String,
    label: String,
    singleLine: Boolean,
    focusRequester: FocusRequester,
    onChange: (String) -> Unit
){
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            )
        )
    }
    var invalidScore by remember { mutableStateOf(true) }
    val patternPerfect = "100\\.000".toRegex()
    val patternNormal = """^\d{2}\.\d{3}$""".toRegex()

    val ctx = LocalContext.current
    val patternZero = Regex("^0+")

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { changed ->
            if (changed.text.matches(patternZero)) {
                Toast.makeText(ctx, "Leading 0 is not allowed.", Toast.LENGTH_SHORT).show()
            }
            else {
                invalidScore = !(changed.text.matches(patternNormal) or changed.text.matches(patternPerfect))
                textFieldValue = changed
            }
            onChange(changed.text)
        },
        modifier = Modifier
            .focusRequester(focusRequester)
            .fillMaxWidth()
            .padding(10.dp),
        label = { Text(label) },
        isError = invalidScore,
        supportingText = {
            if (invalidScore) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Format: XX.XXX or 100.000",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Default
        ),
        trailingIcon = {
            if (invalidScore) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "clear text",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.clickable { textFieldValue = TextFieldValue("") }
                )
            } else {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "clear text",
                    modifier = Modifier.clickable { textFieldValue = TextFieldValue("") }
                )
            }
        },
    )
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
fun getLocalizedDate(): LocalDate {
    var showPicker by remember { mutableStateOf(false) }
    val (datePickerState, pendingDatePickerState) = rememberCustomDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    var localizedNullableSelectedDate: LocalDate?
    var localizedSelectedDate: LocalDate = LocalDate.now()

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
            localizedSelectedDate = localizedNullableSelectedDate ?: LocalDate.now()

            Text(
                text = localizedSelectedDate.toString(),
                modifier = Modifier
                    .padding(end = 20.dp)
            )
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
                showModeToggle = false
            )
        }
    }

    return localizedSelectedDate
}

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterial3Api
@Composable
fun NewEntryScreen(songDao: SongDao, songScoreDao: SongScoreDao) {
    var dialogOpened by remember { mutableStateOf(false) }

    var invalidTitle by remember { mutableStateOf(true) }
    var newTitle by remember { mutableStateOf("") }
    var invalidArtist by remember { mutableStateOf(true) }
    var newArtist by remember { mutableStateOf("") }
    var newScore by remember { mutableStateOf("") }
    var newKey by remember { mutableFloatStateOf(0f) }
    var newDate by remember { mutableStateOf(LocalDate.now()) }
    var newComment by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }

    val verticalPaddingValue = 5
    val horizontalPaddingValue = 10

        FloatingActionButton(
            onClick = { dialogOpened = true },
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

        if (dialogOpened) {
            Dialog(
                onDismissRequest = { dialogOpened = false },
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
                            IconButton(
                                onClick = {
                                    newTitle = ""
                                    invalidTitle = true
                                    newArtist = ""
                                    invalidArtist = true
                                    newScore = ""
                                    newKey = 0f
                                    newDate = LocalDate.now()
                                    newComment = ""
                                    dialogOpened = false
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
                            TextButton(
                                modifier = Modifier.align(Alignment.CenterEnd),
                                onClick = {
                                    val newSongId = songDao.insertSong(
                                        Song(
                                            title = newTitle,
                                            artist = newArtist,
                                            iconColor = Color.Black.toArgb()
                                        )
                                    )
                                    songScoreDao.insertSongScore(
                                        SongScore(
                                            songId = newSongId,
                                            date = newDate,
                                            score = newScore.toFloat(),
                                            key = newKey.toInt(),
                                            comment = newComment
                                        )
                                    )
                                    newTitle = ""
                                    invalidTitle = true
                                    newArtist = ""
                                    invalidArtist = true
                                    newScore = ""
                                    newKey = 0f
                                    newDate = LocalDate.now()
                                    newComment = ""

                                    dialogOpened = false
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
                                OutlinedTextField(
                                    value = newTitle,
                                    onValueChange = {
                                        invalidTitle = it.isBlank()
                                        newTitle = it
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontalPaddingValue.dp,
                                            verticalPaddingValue.dp
                                        ),
                                    label = { Text(text = "Song") },
                                    singleLine = true,
                                    isError = invalidTitle,
                                    supportingText = {
                                        if (invalidTitle) {
                                            Text(
                                                modifier = Modifier.fillMaxWidth(),
                                                text = "Blank is not allowed.",
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Next
                                    ),
                                    trailingIcon = {
                                        if (invalidTitle) {
                                            Icon(
                                                Icons.Default.Clear,
                                                contentDescription = "clear text",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.clickable { newTitle = "" }
                                            )
                                        } else {
                                            Icon(
                                                Icons.Default.Clear,
                                                contentDescription = "clear text",
                                                modifier = Modifier.clickable { newTitle = "" }
                                            )
                                        }
                                    },
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = newArtist,
                                    onValueChange = {
                                        invalidArtist = it.isBlank()
                                        newArtist = it
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontalPaddingValue.dp,
                                            verticalPaddingValue.dp
                                        ),
                                    label = { Text(text = "Artist") },
                                    singleLine = true,
                                    isError = invalidArtist,
                                    supportingText = {
                                        if (invalidArtist) {
                                            Text(
                                                modifier = Modifier.fillMaxWidth(),
                                                text = "Blank is not allowed.",
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Next
                                    ),
                                    trailingIcon = {
                                        if (invalidArtist) {
                                            Icon(
                                                Icons.Default.Clear,
                                                contentDescription = "clear text",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.clickable { newArtist = "" }
                                            )
                                        } else {
                                            Icon(
                                                Icons.Default.Clear,
                                                contentDescription = "clear text",
                                                modifier = Modifier.clickable { newArtist = "" }
                                            )
                                        }
                                    },
                                )
                            }

                            item {
                                CustomScoreTextField(
                                    value = newScore,
                                    label = "Score",
                                    singleLine = true,
                                    focusRequester = focusRequester
                                ) { changed -> newScore = changed }
                            }

                            item {
                                Column {
                                    Text(
                                        text = "Key",
                                        modifier = Modifier
                                            .padding(
                                                start = (horizontalPaddingValue * 2).dp,
                                                top = verticalPaddingValue.dp
                                            ),
                                        fontSize = 16.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        val sliderLength: Int =
                                            LocalConfiguration.current.screenWidthDp
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
                                                .offset(x = ((sliderLength - horizontalPaddingValue * 2) / 13 * newKey).dp)
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
                                        valueRange = -6f..6f,
                                        steps = 11,
                                    )
                                }
                            }

                            item {
                                Column(
                                    modifier = Modifier
                                        .padding(
                                            start = (horizontalPaddingValue * 2).dp,
                                            top = verticalPaddingValue.dp
                                        )
                                ) {
                                    Text(text = "Date")
                                    newDate = getLocalizedDate()
                                }
                            }

                            item {
                                OutlinedTextField(
                                    value = newComment,
                                    onValueChange = { newComment = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontalPaddingValue.dp,
                                            verticalPaddingValue.dp
                                        )
                                        .imePadding(),
                                    label = { Text(text = "Comment") },
                                    singleLine = false,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Default
                                    ),
                                    trailingIcon = {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = "clear text",
                                            modifier = Modifier.clickable { newComment = "" }
                                        )
                                    },
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(300.dp))
                            }
                        }
                    }
                }
            }
        }
}

/*
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@ExperimentalAnimationApi
@Composable
fun AnimatedContentFABtoDiagram() {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.primary,
        onClick = { expanded = !expanded }
    ){
        AnimatedContent(
            targetState = expanded,
        ) { targetExpanded ->
            if (targetExpanded) {
                NewEntryScreen()
            }
            else {
                NewEntryButton()
            }
        }
    }
}

 */
