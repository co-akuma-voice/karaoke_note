package com.example.karaoke_note

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.navigation.NavController
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScoreDao
import com.google.gson.Gson
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun AppBar(
    navController: NavController,
    songDao: SongDao,
    songScoreDao: SongScoreDao,
) {
    val canPop = remember { mutableStateOf(false) }
    val showMenu = remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedFolderUri = result.data?.data
            // ログに選択されたファイルのパスを表示
            Log.d("FolderPicker", "Selected folder: $selectedFolderUri")
            export(songDao, songScoreDao, selectedFolderUri, navController.context)

        }
    }

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, _, _ ->
            canPop.value = navController.previousBackStackEntry != null
        }
    }

    TopAppBar(
        title = { Text("カラオケ点数管理") },
        navigationIcon = {
            if (canPop.value) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
            }
        },
        actions = {
            IconButton(onClick = { showMenu.value = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "メニュー")
            }
            DropdownMenu(
                expanded = showMenu.value,
                onDismissRequest = { showMenu.value = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text("データのインポート")
                    },
                    onClick = {
                        //import()
                        showMenu.value = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text("データのエクスポート")
                    },
                    onClick = {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                        filePickerLauncher.launch(intent)
                        showMenu.value = false
                    }
                )
            }
        }
    )
}

private fun generateFileName(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = dateFormat.format(Date())
    return "karaoke_note_backup_$date.json"
}

data class User(val name: String, val age: Int)

private fun export(songDao: SongDao, songScoreDao: SongScoreDao, folderUri: Uri?, context: Context) {0
    val user = User("koiking213", 18)
    val gson = Gson()
    val json = gson.toJson(user)
    try {
        val documentUri = DocumentsContract.buildDocumentUriUsingTree(
            folderUri,
            DocumentsContract.getTreeDocumentId(folderUri)
        )

        val jsonFileUri = DocumentsContract.createDocument(
            context.contentResolver,
            documentUri,
            "application/json",
            generateFileName()
        )
        if (jsonFileUri == null) {
            Log.e("FolderPicker", "Error creating JSON file")
            return
        }

        context.contentResolver.openOutputStream(jsonFileUri).use { outputStream ->
            BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                writer.write(json)
            }
        }
        Log.d("FolderPicker", "JSON file saved successfully")
    } catch (e: Exception) {
        Log.e("FolderPicker", "Error saving JSON file", e)
    }
}