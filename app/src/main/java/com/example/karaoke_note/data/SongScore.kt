package com.example.karaoke_note.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate


@Entity(
    foreignKeys = [ForeignKey(
        entity = Song::class,
        parentColumns = ["id"],
        childColumns = ["songId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("songId")]
)
data class SongScore(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "songId") val songId: Long,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "score") val score: Float,
    @ColumnInfo(name = "key") val key: Int,
    @ColumnInfo(name = "comment") val comment: String,
    @ColumnInfo(name = "gameKind") val gameKind: GameKind,
)
