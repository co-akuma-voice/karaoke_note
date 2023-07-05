package com.example.karaoke_note.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongScoreDao {
    @Query("SELECT * FROM SongScore WHERE songId = :songId")
    fun getScoresForSong(songId: Long): Flow<List<SongScore>>

    @Query("SELECT * FROM SongScore ORDER BY date DESC LIMIT 5")
    fun getLatestScores(): List<SongScore>

    @Insert
    fun insertSongScore(score: SongScore)

    @Query("DELETE FROM SongScore WHERE id = :id")
    fun deleteSongScore(id: Long)

}