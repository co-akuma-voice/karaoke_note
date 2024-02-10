package com.example.karaoke_note.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            } else {
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
            } else {
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