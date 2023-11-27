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

    @Query("SELECT id FROM Song WHERE title = :title AND artist = :artist LIMIT 1")
    fun getSongId(title: String, artist: String): Long?

    @Query("SELECT * FROM Song WHERE title = :title AND artist = :artist LIMIT 1")
    fun getSong(title: String, artist: String): Song?

    @Query("SELECT * FROM Song WHERE id = :id")
    fun getSong(id: Long): Song?

    @Query("SELECT * FROM Song WHERE artist = :artist")
    fun getSongsByArtist(artist: String): Flow<List<Song>>

    @Query("DELETE FROM Song WHERE id = :id")
    fun delete(id: Long)

    @Transaction
    fun insertSong(song: Song): Long {
        return getSongId(song.title, song.artist) ?: insertUniqueSong(song)
    }

    @Insert
    fun insertUniqueSong(song: Song): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(songs: List<Song>)

    @Query("DELETE FROM Song")
    fun clearAllSongs()
}