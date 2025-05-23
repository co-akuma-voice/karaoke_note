package com.example.karaoke_note.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun getLocalizedDate(
    initialDate: LocalDate
): LocalDate {
    val defaultZone = ZoneId.systemDefault()
    // UTC+0 とシステムデフォルトとの時差をミリ秒単位にしたもの
    val mSecondFromUTC = initialDate.atStartOfDay(defaultZone).offset.totalSeconds * 1000
    val datePickerState = rememberDatePickerState(
        // toInstant(): Java の Instant 型 (エポック秒 = UNIX 時間を保持する) に変換する。
        //              ただし、表示形式は UNIX 時間ではない。
        //              このとき、タイムゾーン情報が UTC+0 になる。
        // toEpochMilli(): UNIX 時間形式 (ミリ秒) に変換する。
        // mSecondFromUTC を足すことで無理やり Zoned 時刻にする
        initialSelectedDateMillis = initialDate.atStartOfDay(defaultZone).toInstant()
            .toEpochMilli() + mSecondFromUTC
    )
    var localizedNullableSelectedDate: LocalDate?
    var localizedSelectedDate: LocalDate = initialDate

    var showPicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            localizedNullableSelectedDate = datePickerState.selectedDateMillis?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            }
            localizedSelectedDate = localizedNullableSelectedDate ?: initialDate

            // 現在設定されている日付を描画する
            Text(
                text = localizedSelectedDate.toString(),
                modifier = Modifier.padding(end = 20.dp)
            )
            // カレンダーボタン
            IconButton(
                onClick = { showPicker = true }
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = { showPicker = false }
                ) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPicker = false }
                ) {
                    Text(text = "Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Sung date",
                        modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp)
                    )
                },
                showModeToggle = true
            )
        }
    }

    return localizedSelectedDate
}