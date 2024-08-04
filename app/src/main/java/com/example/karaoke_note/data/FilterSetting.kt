package com.example.karaoke_note.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class FilterSetting {
    val joySelected: MutableState<Boolean> = mutableStateOf(true)
    val damSelected: MutableState<Boolean> = mutableStateOf(true)
    val joyGameSelected: Map<GameKind, MutableState<Boolean>> = GameKind.getJoyGameKinds().associateWith { mutableStateOf(true) }
    val damGameSelected: Map<GameKind, MutableState<Boolean>> = GameKind.getDamGameKinds().associateWith { mutableStateOf(true) }

    fun getSelectedGameKinds(): List<GameKind> {
        return GameKind.values().filter { gameKind ->
            when (gameKind) {
                in joyGameSelected -> joyGameSelected[gameKind]?.value ?: false
                in damGameSelected -> damGameSelected[gameKind]?.value ?: false
                else -> false
            }
        }
    }

    fun isDefault(): Boolean {
        return joyGameSelected.all { it.value.value } && damGameSelected.all { it.value.value }
    }
}