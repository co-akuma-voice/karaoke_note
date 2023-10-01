package com.example.karaoke_note

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.isDigitsOnly
import kotlin.math.roundToInt

var scoreMaxLength = 5

class ScoreNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text
        var output = ""

        for (i in trimmed.indices) {
            if (trimmed[0] == '1') {
                output = "100.000"
                break
            }
            else {
                output += trimmed[i]
                if (i == scoreMaxLength - 4) {
                    output += '.'
                }
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 2) return offset
                return offset + 1
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                return offset - 1
            }
        }

        return TransformedText(
            text = AnnotatedString(output),
            offsetMapping = offsetMapping
        )
    }
}


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
    val ctx = LocalContext.current
    val patternPerfect = Regex("^1")
    val patternZero = Regex("^0+")

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { changed ->
            if (!changed.text.isDigitsOnly()) {
                Toast.makeText(ctx, "Digits are only available.", Toast.LENGTH_SHORT).show()
            }
            else {
                if (changed.text.matches(patternZero)) {
                    Toast.makeText(ctx, "Leading 0 is not allowed.", Toast.LENGTH_SHORT).show()
                }
                else {
                    if (changed.text.matches(patternPerfect)) {
                        scoreMaxLength = 6
                    }
                    if (changed.text.length <= scoreMaxLength) {
                        textFieldValue = changed
                    }
                }
            }
            onChange(changed.text)
        },
        modifier = Modifier
            .focusRequester(focusRequester)
            .fillMaxWidth()
            .padding(10.dp),
        label = { Text(label) },
        singleLine = singleLine,
        visualTransformation = ScoreNumberVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        trailingIcon = {
            Icon(
                Icons.Default.Clear,
                contentDescription = "clear text",
                modifier = Modifier.clickable { textFieldValue = TextFieldValue("") }
            )
        },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterial3Api
@Composable
fun NewEntryScreen() {
    var dialogOpened by remember { mutableStateOf(false) }

    var newTitle by remember { mutableStateOf("") }
    var newArtist by remember { mutableStateOf("") }
    var newScore by remember { mutableStateOf("") }
    var newKey by remember { mutableFloatStateOf(0f) }
    var newComment by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }

    val verticalPaddingValue = 10.dp
    val horizontalPaddingValue = 10.dp

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
                    Box (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontalPaddingValue, verticalPaddingValue)
                    ){
                        IconButton(
                            onClick = { dialogOpened = false },
                            modifier = Modifier.align(Alignment.CenterStart),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "cancel",
                                modifier = Modifier
                                    .padding(horizontalPaddingValue, verticalPaddingValue)
                                    .size(16.dp)
                            )
                        }
                        TextButton(
                            onClick = { },
                            modifier = Modifier.align(Alignment.CenterEnd),
                        ) {
                            Text("Save")
                        }
                    }

                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontalPaddingValue, verticalPaddingValue),
                        label = { Text(text = "Song") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        trailingIcon = {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "clear text",
                                modifier = Modifier.clickable { newTitle = "" }
                            )
                        },
                    )

                    OutlinedTextField(
                        value = newArtist,
                        onValueChange = { newArtist = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontalPaddingValue, verticalPaddingValue),
                        label = { Text(text = "Artist") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        trailingIcon = {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "clear text",
                                modifier = Modifier.clickable { newArtist = "" }
                            )
                        },
                    )

                    CustomScoreTextField(
                        value = newScore,
                        label = "Score",
                        singleLine = true,
                        focusRequester = focusRequester
                    ) { changed -> newScore = changed }

                    Column {
                        Text(
                            text = "Key",
                            modifier = Modifier
                                .padding(start = 20.dp, top = verticalPaddingValue, end = 0.dp, bottom = 0.dp),
                            fontSize = 16.sp
                        )
                        Box (
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            val sliderLength : Int = LocalConfiguration.current.screenWidthDp
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
                                    .offset(x = (sliderLength / 13 * newKey).dp)
                            )
                        }
                        Slider(
                            value = newKey,
                            onValueChange = { newKey = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = horizontalPaddingValue, vertical = 0.dp),
                            valueRange = -6f..6f,
                            steps = 11,
                        )
                    }

                    OutlinedTextField(
                        value = newComment,
                        onValueChange = { newComment = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontalPaddingValue, verticalPaddingValue),
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
