package com.example.karaoke_note.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Artistテーブルを作成
        database.execSQL("CREATE TABLE `Artist` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `icon_color` INTEGER NOT NULL)")

        // 既存のSongデータからArtistデータを生成し、Artistテーブルに挿入
        database.execSQL("INSERT INTO Artist (name, icon_color) SELECT DISTINCT artist, icon_color FROM Song")

        // Songテーブルから不要なカラムを削除し、artistIdカラムを追加
        database.execSQL("ALTER TABLE Song ADD COLUMN artistId INTEGER NOT NULL DEFAULT 0")

        // 更新したArtistテーブルのIDを使用して、SongテーブルのartistIdを更新
        database.execSQL("UPDATE Song SET artistId = (SELECT id FROM Artist WHERE Song.artist = Artist.name)")

        // 不要になったカラムを削除
        database.execSQL("CREATE TABLE new_Song (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL, artistId INTEGER NOT NULL)")
        database.execSQL("INSERT INTO new_Song (id, title, artistId) SELECT id, title, artistId FROM Song")
        database.execSQL("DROP TABLE Song")
        database.execSQL("ALTER TABLE new_Song RENAME TO Song")
    }
}
