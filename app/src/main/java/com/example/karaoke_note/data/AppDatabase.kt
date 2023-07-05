package com.example.karaoke_note.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Song::class, SongScore::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songScoreDao(): SongScoreDao
    abstract fun songDao(): SongDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance
                ?: Room.databaseBuilder(context, AppDatabase::class.java, "karaoke_database")
                    .allowMainThreadQueries().build()
        }
    }
}
