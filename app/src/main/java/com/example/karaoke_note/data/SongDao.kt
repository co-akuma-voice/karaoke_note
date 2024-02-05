package com.example.karaoke_note.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM song")
    fun getAllSongs(): List<Song>

    @Query("SELECT id FROM Song WHERE title = :title AND artistId = :artistId LIMIT 1")
    fun getSongId(title: String, artistId: Long): Long?

    @Query("SELECT * FROM Song WHERE title = :title AND artistId = :artistId LIMIT 1")
    fun getSong(title: String, artistId: Long): Song?

    @Query("SELECT * FROM Song WHERE id = :id")
    fun getSong(id: Long): Song?

    @Query("SELECT * FROM Song WHERE artistId = :artistId")
    fun getSongsByArtist(artistId: Long): Flow<List<Song>>

    @Query("DELETE FROM Song WHERE id = :id")
    fun delete(id: Long)

    @Query("UPDATE Song SET title = :title WHERE id = :id")
    fun updateTitle(id: Long, title: String)

    @Transaction
    fun insertSong(song: Song): Long {
        return getSongId(song.title, song.artistId) ?: insertUniqueSong(song)
    }

    @Insert
    fun insertUniqueSong(song: Song): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(songs: List<Song>)

    @Query("DELETE FROM Song")
    fun clearAllSongs()

    @Query("SELECT Song.* FROM Song INNER JOIN SongScore ON Song.id = SongScore.songId WHERE Song.artistId = :artistId AND SongScore.score != 0.0 GROUP BY Song.id")
    fun getSongsWithScores(artistId: Long): Flow<List<Song>>
}