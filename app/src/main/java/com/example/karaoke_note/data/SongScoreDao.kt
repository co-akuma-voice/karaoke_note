package com.example.karaoke_note.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface SongScoreDao {
    @Query("SELECT * FROM SongScore WHERE songId = :songId")
    fun getScoresForSong(songId: Long): Flow<List<SongScore>>

    @Query("SELECT * FROM SongScore ORDER BY date DESC, id DESC LIMIT :limit")
    fun getLatestScores(limit: Int): List<SongScore>

    @Query("SELECT * FROM SongScore WHERE songId = :songId AND date = :date AND score = :score AND \"key\" = :key AND comment = :comment AND gameKind = :gameKind")
    fun findSongScore(songId: Long, date: LocalDate, score: Float, key: Int, comment: String, gameKind: GameKind): SongScore?

    @Query("""
        SELECT * FROM SongScore 
        WHERE songId = :songId 
        ORDER BY score DESC 
        LIMIT 1
    """)
    fun getHighestScoreBySongId(songId: Long): SongScore?

    @Query("""
        SELECT date FROM SongScore 
        WHERE songId = :songId 
        ORDER BY date DESC 
        LIMIT 1
    """)
    fun getMostRecentDate(songId: Long): LocalDate?

    @Transaction
    fun insertSongScore(score: SongScore) {
        val existingEntry = findSongScore(
            score.songId,
            score.date,
            score.score,
            score.key,
            score.comment,
            score.gameKind
        )
        if (existingEntry == null) {
            insertUniqueSongScore(score)
        }
    }

    @Insert
    fun insertUniqueSongScore(songScore: SongScore)

    @Query("DELETE FROM SongScore WHERE id = :id")
    fun deleteSongScore(id: Long)

    @Query("SELECT COUNT(*) FROM SongScore WHERE songId = :songId")
    suspend fun countScoresForSong(songId: Long): Int

    @Query("SELECT * FROM SongScore")
    fun getAll(): List<SongScore>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(songScores: List<SongScore>)

    @Query("DELETE FROM SongScore")
    fun clearAllSongScores()
}