package com.example.karaoke_note.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp


enum class SortMethod(val displayName: String) {
    NameAsc("名前 (昇順)"),
    NameDesc("名前 (降順)"),
    DateAsc("最近日付 (古い順)"),
    DateDesc("最近日付 (新しい順)"),
    ScoreAsc("ハイスコア (低い順)"),
    ScoreDesc("ハイスコア (高い順)");

    companion object {
        fun fromDisplayName(displayName: String): SortMethod? {
            return SortMethod.values().firstOrNull { it.displayName == displayName }
        }
    }
}

@Composable
fun SortMethodSelectorBox(
    displayedSortMethod: SortMethod,
    modifier: Modifier,    // Card の Modifier
    textSize: Int,
    isExpanded: Boolean,
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = displayedSortMethod.displayName,
            modifier = modifier,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = textSize.sp,
            textAlign = TextAlign.End
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortMethodSelectorItem(
    sortMethod: SortMethod,
    selectedSortMethod: SortMethod,
    textSize: Int,
    onClick: () -> Unit
) {
    val isSelected = sortMethod == selectedSortMethod
    val fontWeight = if (isSelected) {
        FontWeight.Bold
    } else {
        FontWeight.Normal
    }
    val fontColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    DropdownMenuItem(
        onClick = { onClick() },
    ) {
        Text(
            text = sortMethod.displayName,
            color = fontColor,
            fontSize = textSize.sp,
            fontWeight = fontWeight,
        )
    }
}