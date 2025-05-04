package com.example.karaoke_note.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karaoke_note.data.GameKind
import com.example.karaoke_note.getPainterResourceIdOfBrandImage
import com.example.karaoke_note.getPainterResourceIdOfGameImage

//
// NewEntrySheet の採点ゲーム選択部分
//
@OptIn(ExperimentalMaterial3Api::class)
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
        HorizontalDivider(
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
        text = {
            Text(
                text = "(" + gameKind.displayName + ")",
                fontSize = textSize.sp,
                modifier = Modifier.padding(start = textHorizontalPaddingValues.dp)
            )
        },
        onClick = { onClick() },
        modifier = Modifier,
        leadingIcon = {
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
            }
        },
        trailingIcon = {},
        enabled = true,
        colors = MenuDefaults.itemColors(),
        contentPadding = MenuDefaults.DropdownMenuItemContentPadding,
        interactionSource = null
    )
}