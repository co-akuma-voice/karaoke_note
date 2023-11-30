package com.example.karaoke_note.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.karaoke_note.data.GameKind

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val default = GameKind.JOY_NATIONAL_SCORING_GP.ordinal
        database.execSQL("ALTER TABLE SongScore ADD COLUMN gameKind INTEGER NOT NULL DEFAULT $default")
    }
}
