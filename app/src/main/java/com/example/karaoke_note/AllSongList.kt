package com.example.karaoke_note

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.karaoke_note.ui.component.SortMethod
import com.example.karaoke_note.ui.component.SortMethodSelectorBox
import com.example.karaoke_note.ui.component.SortMethodSelectorItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayAllSongsList(
    sortMethod: MutableState<SortMethod>
){
    var expanded by remember { mutableStateOf(false) }
    val sortMethodsList = enumValues<SortMethod>()

    val sortMethodHeight = 36
    val sortMethodFontSize = 6
    val horizontalPaddingValue = 10
    val verticalPaddingValue = 1

    Box(
        modifier = Modifier
            .padding(
                start = horizontalPaddingValue.dp,
                top = (verticalPaddingValue + 6).dp,
                end = 0.dp,
                bottom = 0.dp
            )
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            // 現在設定値の表示部分
            SortMethodSelectorBox(
                displayedSortMethod = sortMethod.value,
                height = sortMethodHeight,
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
                sortMethodsList.forEach {
                    SortMethodSelectorItem(
                        sortMethod = it,
                        height = sortMethodHeight,
                        textSize = sortMethodFontSize,
                        textHorizontalPaddingValues = horizontalPaddingValue,
                    ){
                        sortMethod.value = it
                        expanded = false
                    }
                }
            }
        }
    }
}