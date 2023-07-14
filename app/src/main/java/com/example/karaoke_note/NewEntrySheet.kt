package com.example.karaoke_note

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.launch

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
    modifier: Modifier,
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
@ExperimentalMaterialApi
@Composable
fun ModalBottomSheetCompose() {
    val scope = rememberCoroutineScope()
    //val scaffoldState = rememberBottomSheetScaffoldState()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded, skipHalfExpanded = true)

    var newTitle by remember { mutableStateOf("") }
    var newArtist by remember { mutableStateOf("") }
    var newScore by remember { mutableStateOf("") }
    var newKey by remember { mutableStateOf(0f) }
    var newComment by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }

    ModalBottomSheetLayout(
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
            ){
                IconButton(
                    onClick = {
                        scope.launch { sheetState.hide() }
                    },
                    modifier = Modifier.align(Alignment.TopStart),
                ){
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Cancel"
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    modifier = Modifier
                        .padding(5.dp),
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
                        ) },
                )

                OutlinedTextField(
                    value = newArtist,
                    onValueChange = { newArtist = it },
                    modifier = Modifier
                        .padding(5.dp),
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
                    modifier = Modifier,
                    label = "Score",
                    singleLine = true,
                    focusRequester = focusRequester,
                    onChange = { changed -> newScore = changed }
                )

                Slider(
                    value = newKey,
                    onValueChange = { newKey = it },
                    modifier = Modifier
                        .padding(10.dp),
                    valueRange = -6f..6f,
                    steps = 11,
                )

                OutlinedTextField(
                    value = newComment,
                    onValueChange = { newComment = it },
                    modifier = Modifier
                        .padding(5.dp),
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

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }
                    }
                ){
                    Text("Click to collapse sheet")
                }
            }
        },
        sheetState = sheetState,
    ) {
        // Dummy implementation
        Column() {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}