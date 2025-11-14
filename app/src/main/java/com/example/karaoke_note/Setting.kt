package com.example.karaoke_note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onNavigateBack: () -> Unit // 戻るボタンが押されたときのコールバック
) {
    // 各Switchの状態を管理する
    var switch1 by remember { mutableStateOf(true) }
    var switch2 by remember { mutableStateOf(false) }

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
                title = "通知を有効にする",
                checked = switch1,
                onCheckedChanged = { switch1 = it }
            )

            HorizontalDivider()

            // 2つ目のSwitch設定
            SettingItemRow(
                title = "ダークモードを有効にする",
                checked = switch2,
                onCheckedChanged = { switch2 = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 1つ目のTextButton
            TextButton(
                onClick = { /* アカウント設定の処理 */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("アカウント設定")
            }

            HorizontalDivider()

            // 2つ目のTextButton
            TextButton(
                onClick = { /* 利用規約の表示処理 */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("利用規約")
            }
        }
    }
}

@Composable
fun SettingItemRow(
    title: String,
    checked: Boolean,
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
            checked = checked,
            onCheckedChange = onCheckedChanged
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    // プレビュー用に空のコールバックを渡す
    SettingScreen(onNavigateBack = {})
}
