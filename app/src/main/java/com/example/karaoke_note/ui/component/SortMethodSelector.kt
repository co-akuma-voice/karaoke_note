package com.example.karaoke_note.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


enum class SortMethod(val displayName: String) {
    NameAsc("名前 (昇順)"),
    NameDesc("名前 (降順)"),
    DateAsc("最近歌った日付 (新しい順)"),
    DateDesc("最近歌った日付 (古い順)");

    companion object {
        fun fromDisplayName(displayName: String): SortMethod? {
            return SortMethod.values().firstOrNull { it.displayName == displayName }
        }
    }
}

@Composable
fun SortMethodSelectorBox(
    displayedSortMethod: SortMethod,
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
                    Text(displayedSortMethod.displayName)
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
fun SortMethodSelectorItem(
    sortMethod: SortMethod,
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
            Text(sortMethod.displayName)
        }
    }
}