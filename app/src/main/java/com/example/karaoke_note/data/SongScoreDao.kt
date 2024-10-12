package com.example.karaoke_note.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface SongScoreDao {
    @Query("SELECT * FROM SongScore WHERE songId = :songId AND score != 0.0")
    fun getScoresForSong(songId: Long): Flow<List<SongScore>>

    @Query("SELECT * FROM SongScore WHERE score != 0.0 ORDER BY date DESC, id DESC LIMIT :limit OFFSET :offset")
    fun getLatestScores(limit: Int, offset: Int = 0): List<SongScore>

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
        SELECT * FROM SongScore 
        WHERE songId = :songId AND gameKind IN (:gameKinds)
        ORDER BY score DESC 
        LIMIT 1
        
    """)
    fun getHighestScoreBySongIdAndGameKinds(songId: Long, gameKinds: List<GameKind>): SongScore?

    @Query("""
        SELECT date FROM SongScore 
        WHERE songId = :songId 
        ORDER BY date DESC 
        LIMIT 1
    """)
    fun getMostRecentDate(songId: Long): LocalDate?

    @Query("""
    SELECT * FROM SongScore
    INNER JOIN Song ON SongScore.songID = Song.id
    INNER JOIN Artist ON Song.artistID = Artist.iD
    WHERE (Artist.name LIKE :searchQuery OR Song.title LIKE :searchQuery)
      AND score != 0.0
    ORDER BY date DESC, id DESC
    LIMIT :limit OFFSET :offset
""")
    fun getLatestScoresByText(searchQuery: String, limit: Int, offset: Int): List<SongScore>

    @Update
    fun update(songScore: SongScore): Int

    @Insert
    fun insert(songScore: SongScore)

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

    @Query("SELECT * FROM SongScore WHERE score = 0.0")
    fun getAll0Scores(): Flow<List<SongScore>>
}