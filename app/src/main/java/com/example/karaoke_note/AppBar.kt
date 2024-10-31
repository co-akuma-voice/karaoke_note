package com.example.karaoke_note

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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
import com.example.karaoke_note.data.Artist
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.DATABASE_VERSION
import com.example.karaoke_note.data.FilterSetting
import com.example.karaoke_note.data.GameKind
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScore
import com.example.karaoke_note.data.SongScoreDao
import com.example.karaoke_note.ui.component.CustomTextField
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


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
    focusManagerOfSearchBar: FocusManager
) {
    val canPop = remember { mutableStateOf(false) }
    val showMenu = remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, _, _ ->
            canPop.value = navController.previousBackStackEntry != null
        }
    }

    TopAppBar(
        backgroundColor = MaterialTheme.colorScheme.surface,
        title = {},
        elevation = 2.dp,
        navigationIcon = {
            if (canPop.value) {
                IconButton(
                    onClick = {
                        clearFocusFromSearchBar(focusManagerOfSearchBar)
                        navController.navigateUp()
                    }
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
            }
        },
        actions = {
            Row(
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
            ) {
                // 検索ウインドウ
                CustomTextField(
                    value = searchText.value,
                    onValueChange = { searchText.value = it },
                    placeholder = { Text(text = "検索") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
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
                    contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(0.dp, 0.dp, 0.dp, 0.dp)
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

                // メニューボタン
                IconButton(
                    onClick = {
                        clearFocusFromSearchBar(focusManagerOfSearchBar)
                        showMenu.value = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "メニュー"
                    )
                }
                DropdownMenu(
                    expanded = showMenu.value,
                    onDismissRequest = { showMenu.value = false }
                ) {
                    ImportMenu(songDao, songScoreDao, artistDao, navController.context) {
                        showMenu.value = false
                    }
                    ExportMenu(songDao, songScoreDao, artistDao, navController.context) {
                        showMenu.value = false
                    }
                }
            }
        }
    )

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
            windowInsets = WindowInsets.displayCutout,
        ) {
            // Sheet content
            FilterContents(filterSetting)
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
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterContentGroup(
    label: String,
    selectedStatus: MutableState<Boolean>,
    gameSelected: Map<GameKind, MutableState<Boolean>>,
    modifier: Modifier = Modifier
) {
    Column(
        //modifier = modifier.background(color = Color.Yellow)
    ) {
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
                gameSelected.entries.forEach() { entry ->
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


private fun generateFileName(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = dateFormat.format(Date())
    return "karaoke_note_backup_$date.json"
}

@Composable
fun ExportMenu(
    songDao: SongDao,
    songScoreDao: SongScoreDao,
    artistDao: ArtistDao,
    context: Context,
    onClick: () -> Unit = {}
) {
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedFolderUri = result.data?.data
            // ログに選択されたファイルのパスを表示
            Log.d("FolderPicker", "Selected folder: $selectedFolderUri")
            val songScores = songScoreDao.getAll()
            val songs = songDao.getAllSongs()
            val artists = artistDao.getAllArtists()

            // LocalDate型のカスタムシリアライザ
            val localDateSerializer = JsonSerializer<LocalDate> { src, _, _ ->
                Gson().toJsonTree(src.format(DateTimeFormatter.ISO_LOCAL_DATE))
            }

            // Gsonインスタンスの作成
            val gson = GsonBuilder()
                .registerTypeAdapter(LocalDate::class.java, localDateSerializer)
                .create()

            // エクスポートするデータ構造
            val exportData = mapOf(
                "version" to DATABASE_VERSION,
                "songScores" to songScores,
                "songs" to songs,
                "artists" to artists
            )
            val json = gson.toJson(exportData)

            try {
                val documentUri = DocumentsContract.buildDocumentUriUsingTree(
                    selectedFolderUri,
                    DocumentsContract.getTreeDocumentId(selectedFolderUri)
                )

                val jsonFileUri = DocumentsContract.createDocument(
                    context.contentResolver,
                    documentUri,
                    "application/json",
                    generateFileName()
                )
                if (jsonFileUri == null) {
                    Log.e("FolderPicker", "Error creating JSON file")
                } else {
                    context.contentResolver.openOutputStream(jsonFileUri).use { outputStream ->
                        BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                            writer.write(json)
                        }
                    }
                }
                Log.d("FolderPicker", "JSON file saved successfully")
            } catch (e: Exception) {
                Log.e("FolderPicker", "Error saving JSON file", e)
            }
        }
        onClick()
    }
    DropdownMenuItem(
        text = {
            Text("データのエクスポート")
        },
        onClick = {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            filePickerLauncher.launch(intent)
        }
    )
}

@Composable
fun ImportMenu(
    songDao: SongDao,
    songScoreDao: SongScoreDao,
    artistDao: ArtistDao,
    context: Context,
    onClick: () -> Unit = {}
) {
    val localDateDeserializer = JsonDeserializer { json, _, _ ->
        LocalDate.parse(json.asJsonPrimitive.asString, DateTimeFormatter.ISO_LOCAL_DATE)
    }

    val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, localDateDeserializer)
        .create()

    var showDialog by remember { mutableStateOf(false) }
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedFileUri = result.data?.data
            // ログに選択されたファイルのパスを表示
            Log.d("FilePicker", "Selected file: $selectedFileUri")
            try {
                context.contentResolver.openInputStream(selectedFileUri!!).use { inputStream ->
                    val json = inputStream?.bufferedReader().use { it?.readText() }
                    Log.d("FilePicker", "JSON file loaded successfully")
                    Log.d("FilePicker", json!!)
                    data class JsonVersion(
                        val version: Int
                    )
                    val versionInfo = gson.fromJson(json, JsonVersion::class.java)
                    when (versionInfo.version) {
                        4 -> {
                            data class JsonDataV3(
                                val version: Int,
                                val songScores: List<SongScore>,
                                val songs: List<Song>,
                                val artists: List<Artist>
                            )
                            val jsonDataV3 = gson.fromJson(json, JsonDataV3::class.java)

                            // IDが混在するとおかしくなるのでデータベースをクリア
                            songDao.clearAllSongs()
                            songScoreDao.clearAllSongScores()
                            artistDao.clearAllArtists()

                            // データベースにインポート
                            songDao.insertAll(jsonDataV3.songs)
                            songScoreDao.insertAll(jsonDataV3.songScores)
                            artistDao.insertAll(jsonDataV3.artists)
                        }
                        else -> {
                            throw IllegalArgumentException("Unsupported version: ${versionInfo.version}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("FilePicker", "Error loading JSON file", e)
            }
        }
        onClick()
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("確認") },
            text = { Text("すべてのデータは失われますがよろしいですか？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        // ファイルピッカーを起動
                        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                            type = "application/json"
                        }
                        filePickerLauncher.launch(intent)
                    }
                ) {
                    Text("はい")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("いいえ")
                }
            }
        )
    }
    DropdownMenuItem(
        text = {
            Text("データのインポート")
        },
        onClick = {
            showDialog = true
        }
    )
}