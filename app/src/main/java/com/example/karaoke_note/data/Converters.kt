package com.example.karaoke_note.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun toGameKind(value: Int) = enumValues<GameKind>()[value]

    @TypeConverter
    fun fromGameKind(value: GameKind) = value.ordinal
}