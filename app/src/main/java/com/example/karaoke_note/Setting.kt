package com.example.karaoke_note

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.karaoke_note.data.Artist
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.DATABASE_VERSION
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScore
import com.example.karaoke_note.data.SongScoreDao
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
fun SettingScreen(
    navController: NavController,
    songDao: SongDao,
    songScoreDao: SongScoreDao,
    artistDao: ArtistDao,
    isBackKeyDisabled: MutableState<Boolean>,
    onNavigateBack: () -> Unit // 戻るボタンが押されたときのコールバック
) {
    // 各Switchの状態を管理する
    val switch2 = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("設定") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "戻る"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // 1つ目のSwitch設定
            SettingItemRow(
                title = "登録画面において戻るボタンを無効化する",
                checked = isBackKeyDisabled,
                onCheckedChanged = { isBackKeyDisabled.value = it }
            )

            // 2つ目のSwitch設定
            SettingItemRow(
                title = "(仮)ダークモードを有効にする",
                checked = switch2,
                onCheckedChanged = { switch2.value = it }
            )
            Spacer(modifier = Modifier.height(32.dp))

            ImportMenu(songDao, songScoreDao, artistDao, navController.context) {}
            Spacer(modifier = Modifier.height(16.dp))

            ExportMenu(songDao, songScoreDao, artistDao, navController.context) {}

        }
    }
}

@Composable
fun SettingItemRow(
    title: String,
    checked: MutableState<Boolean>,
    onCheckedChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = checked.value,
            onCheckedChange = onCheckedChanged
        )
    }
}

/*
@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    // プレビュー用に空のコールバックを渡す
    SettingScreen(songDao, songScoreDao, artistDao, onNavigateBack = {})
}

 */

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
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(
            onClick = {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                filePickerLauncher.launch(intent)
            },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("データのエクスポート")
        }
    }
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

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(
            onClick = {
                showDialog = true
            },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("データのインポート")
        }
    }
}
