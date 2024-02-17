package com.example.karaoke_note.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties


// スコア欄をチェックするための関数
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
fun getSupportingTextForScoreField(
    score: String,
    isPlanning: Boolean,
): String {
    var message = ""

    // スコア欄は予約フラグとの兼ね合いがある
    if (!isPlanning) {
        if (score.isBlank()) {
            message = "Required."
        }
        else {
            // スコア欄固有のチェック
            // 小数が1つ以外のとき
            if (getNumberOfDecimalPoints(score) != 1) {
                message = "Invalid value."
            }
            else {
                // 整数部、小数部をチェック
                // 小数部は桁数が3桁に足りない場合は末尾に 0 を補充する
                val strIntegerPart = getIntegerPartOfScore(score)
                val integerPart: Int? = strIntegerPart.toIntOrNull()
                val strDecimalPart: String = getDecimalPartOfScore(score)
                val decimalPart: Int? = strDecimalPart.toIntOrNull()

                // 何かしら値が入っているとき
                if (integerPart != null && decimalPart != null) {
                    // 100 点以上の扱い
                    if ((integerPart > 100) || ((integerPart == 100) && (decimalPart != 0))) {
                        message = "Invalid value."
                    }
                    else if ((integerPart < 0)) {
                        // 負の数の扱い
                        message = "Invalid value."
                    }
                    else {
                        // 99.1234 などの変な形の時
                        if (strDecimalPart.length != 3) {
                            message = "Invalid value."
                        }
                    }
                }
                else {
                    // .123 や ,.,,, みたいな形の時
                    message = "Invalid value."
                }
            }
        }
    }

    return message
}

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
    errorSupportingText: String,
    isEnabled: Boolean,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    focusRequester: FocusRequester,
    autoCompleteSuggestions: List<String> = emptyList(),
    onChange: (String) -> Unit,
    onClear: () -> Unit,
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(initValue)) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val invalidValue by remember { derivedStateOf { textFieldValue.text.isEmpty() } }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val trailingIconSize = 20
    val filteredAutoCompleteSuggestions = remember(textFieldValue.text) {
        autoCompleteSuggestions.filter { it.startsWith(textFieldValue.text, ignoreCase = true) }
    }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(initValue) {
        if (textFieldValue.text != initValue) {
            textFieldValue = textFieldValue.copy(text = initValue)
        }
    }

    Column {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { inputText ->
                textFieldValue = inputText
                isDropdownExpanded = inputText.text.isNotEmpty()
                onChange(inputText.text)
            },
            modifier = Modifier
                .bringIntoViewRequester(bringIntoViewRequester)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                    if (!it.isFocused) {
                        isDropdownExpanded = false
                    }
                }
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

        val alreadyComplete = filteredAutoCompleteSuggestions.size == 1 && filteredAutoCompleteSuggestions[0] == textFieldValue.text
        if (isFocused && filteredAutoCompleteSuggestions.isNotEmpty() && !alreadyComplete) {
            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                properties = PopupProperties(
                    focusable = false,
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                filteredAutoCompleteSuggestions.forEach { suggestion ->
                    DropdownMenuItem (
                        text = { Text(suggestion) },
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            textFieldValue = TextFieldValue(suggestion)
                            onChange(suggestion)
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }
    }
}