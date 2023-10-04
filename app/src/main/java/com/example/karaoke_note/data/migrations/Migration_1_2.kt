package com.example.karaoke_note.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Song ADD COLUMN icon_color INTEGER NOT NULL DEFAULT 0")
    }
}
