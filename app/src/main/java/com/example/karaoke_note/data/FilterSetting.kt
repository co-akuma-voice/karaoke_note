package com.example.karaoke_note.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class FilterSetting {
    val joySelected: MutableState<Boolean> = mutableStateOf(true)
    val damSelected: MutableState<Boolean> = mutableStateOf(true)
}