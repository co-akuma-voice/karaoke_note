package com.example.karaoke_note

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.FilterSetting
import com.example.karaoke_note.data.GameKind
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScoreDao
import com.example.karaoke_note.ui.component.CustomTextField


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    navController: NavController,
    songDao: SongDao,
    songScoreDao: SongScoreDao,
    artistDao: ArtistDao,
    filterSetting: MutableState<FilterSetting>,
    searchText: MutableState<String>,
    focusRequesterForSearchBar: FocusRequester,
    focusManagerOfSearchBar: FocusManager,
    isBackKeyDisabled: MutableState<Boolean>
) {
    val canPop = remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, _, _ ->
            canPop.value = navController.previousBackStackEntry != null
        }
    }

    TopAppBar(
        title = {},
        navigationIcon = {
            if (canPop.value) {
                IconButton(
                    onClick = {
                        clearFocusFromSearchBar(focusManagerOfSearchBar)
                        navController.navigateUp()
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
        },
        actions = {
            Row(
                modifier = Modifier.align(alignment = Alignment.CenterVertically),
                horizontalArrangement = Arrangement.End
            ) {
                // 検索ウインドウ
                CustomTextField(
                    value = searchText.value,
                    onValueChange = { searchText.value = it },
                    placeholder = { Text(text = "検索") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp, end = 8.dp)
                        .focusRequester(focusRequesterForSearchBar),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        // バツボタン（クリアボタン）
                        if (searchText.value.isNotEmpty()) {
                            IconButton(
                                onClick = { searchText.value = "" },
                                modifier = Modifier.scale(0.8f)
                            ){
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear text"
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            // Search キーを押すと TextField からフォーカスを外す
                            clearFocusFromSearchBar(focusManagerOfSearchBar)
                        }
                    ),
                    shape = RoundedCornerShape(50),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                        0.dp, 0.dp, 0.dp, 0.dp
                    )
                )

                // フィルターボタン
                IconButton(
                    onClick = {
                        clearFocusFromSearchBar(focusManagerOfSearchBar)
                        showSheet = true
                    }
                ) {
                    Icon(
                        imageVector = if (filterSetting.value.isDefault()) {
                            Icons.Outlined.FilterAlt
                        } else {
                            Icons.Filled.FilterAlt
                        },
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // 設定ボタン
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    IconButton(
                        onClick = {
                            clearFocusFromSearchBar(focusManagerOfSearchBar)
                            showSettings = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )

    // 下から上がってくるフィルター用画面
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            modifier = Modifier.fillMaxSize(),
            sheetState = sheetState,
            shape = BottomSheetDefaults.ExpandedShape,
            containerColor = BottomSheetDefaults.ContainerColor,
            tonalElevation = BottomSheetDefaults.Elevation,
            scrimColor = BottomSheetDefaults.ScrimColor,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            //windowInsets = WindowInsets.displayCutout,
        ) {
            // Sheet content
            FilterContents(filterSetting)
        }
    }

    // 設定画面
    if (showSettings) {
        SettingScreen(navController, songDao, songScoreDao, artistDao, isBackKeyDisabled) {
            showSettings = false
        }
    }
}

fun clearFocusFromSearchBar(focusManager: FocusManager) {
    focusManager.clearFocus()
}

@Composable
fun FilterContents(
    filterSetting: MutableState<FilterSetting>
){
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        // Game 表示
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Games,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .scale(0.75f),
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(text = "Game", fontWeight = FontWeight.Bold)
        }

        // JOY/DAM グループ
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            FilterContentGroup(label = "JOY", filterSetting.value.joySelected, filterSetting.value.joyGameSelected)
            FilterContentGroup(label = "DAM", filterSetting.value.damSelected, filterSetting.value.damGameSelected)
        }
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterContentGroup(
    label: String,
    selectedStatus: MutableState<Boolean>,
    gameSelected: Map<GameKind, MutableState<Boolean>>,
) {
    Column {
        // 採点ゲームグループ
        FilterContent(
            label = label,
            modifier = Modifier,
            selectedStatus = selectedStatus,
            onClick = {
                selectedStatus.value = !selectedStatus.value
                gameSelected.forEach { (_, value) -> value.value = selectedStatus.value }
            }
        )

        // 各採点ゲームのボタン
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                    .padding(start = 2.dp, end = 2.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                gameSelected.entries.forEach { entry ->
                    FilterContent(
                        label = entry.key.displayName,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp),
                        selectedStatus = entry.value
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterContent(
    label: String,
    modifier: Modifier,
    selectedStatus: MutableState<Boolean>,
    onClick: () -> Unit = { selectedStatus.value = !selectedStatus.value }
) {
    FilterChip(
        onClick = onClick,
        modifier = modifier,
        label = { Text(label) },
        selected = selectedStatus.value,
        leadingIcon = if (selectedStatus.value) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        }
    )
}