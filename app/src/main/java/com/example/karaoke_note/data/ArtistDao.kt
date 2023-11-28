package com.example.karaoke_note.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(artist: Artist): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(artists: List<Artist>)

    @Query("SELECT * FROM Artist WHERE name = :name LIMIT 1")
    fun getByName(name: String): Artist?

    @Query("SELECT name FROM Artist WHERE id = :id LIMIT 1")
    fun getNameById(id: Long): String?

    @Query("SELECT * FROM Artist WHERE id = :id LIMIT 1")
    fun get(id: Long): Artist?

    @Query("DELETE FROM Artist WHERE id = :id")
    fun delete(id: Long)

    @Query("DELETE FROM Artist")
    fun clearAllArtists()

    @Query("SELECT * FROM Artist")
    fun getAllArtists(): List<Artist>
}
