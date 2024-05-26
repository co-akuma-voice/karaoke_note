package com.example.karaoke_note.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {
    @Transaction
    fun insert(artist: Artist): Long {
        return getByName(artist.name)?.id ?: insertUnique(artist)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUnique(artist: Artist): Long

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

    @Query("UPDATE Artist SET name = :name WHERE id = :id")
    fun updateName(id: Long, name: String)

    @Query("UPDATE Artist SET icon_color = :icon WHERE id = :id")
    fun updateIcon(id: Long, icon: Int)

    @Query("DELETE FROM Artist")
    fun clearAllArtists()

    @Query("SELECT * FROM Artist")
    fun getAllArtists(): Flow<List<Artist>>

    @Query("SELECT Artist.* FROM Artist JOIN Song ON Artist.id = Song.artistId JOIN SongScore ON Song.id = SongScore.songId GROUP BY Artist.id HAVING COUNT(SongScore.id) > 0")
    fun getArtistsWithSongs(): Flow<List<Artist>>
}
