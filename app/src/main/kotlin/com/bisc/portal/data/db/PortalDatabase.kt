package com.bisc.portal.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bisc.portal.data.model.Section
import com.bisc.portal.data.model.Tile

@Database(
    entities = [Section::class, Tile::class, com.bisc.portal.data.model.TileClick::class],
    version = 15,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PortalDatabase : RoomDatabase() {
    abstract fun sectionDao(): SectionDao
    abstract fun tileDao(): TileDao
    abstract fun tileClickDao(): TileClickDao

    companion object {
        val MIGRATION_3_4 = object : androidx.room.migration.Migration(3, 4) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tiles ADD COLUMN colSpan INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE tiles ADD COLUMN rowSpan INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_4_5 = object : androidx.room.migration.Migration(4, 5) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tiles ADD COLUMN iconShape TEXT NOT NULL DEFAULT 'square'")
                db.execSQL("ALTER TABLE tiles ADD COLUMN iconScale TEXT NOT NULL DEFAULT 'crop'")
                db.execSQL("ALTER TABLE tiles ADD COLUMN iconBgColor TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE tiles ADD COLUMN iconBgEnabled INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_5_6 = object : androidx.room.migration.Migration(5, 6) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sections ADD COLUMN headerBgColor TEXT NOT NULL DEFAULT '000B29'")
                db.execSQL("ALTER TABLE sections ADD COLUMN headerIcon TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE sections ADD COLUMN headerHeight INTEGER NOT NULL DEFAULT 1")
            }
        }

        val MIGRATION_6_7 = object : androidx.room.migration.Migration(6, 7) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sections ADD COLUMN sectionColumns INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE sections ADD COLUMN headerRadius INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_7_8 = object : androidx.room.migration.Migration(7, 8) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS tile_clicks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        tileId INTEGER NOT NULL,
                        url TEXT NOT NULL,
                        label TEXT NOT NULL,
                        timestamp INTEGER NOT NULL
                    )"""
                )
            }
        }

        val MIGRATION_8_9 = object : androidx.room.migration.Migration(8, 9) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tiles ADD COLUMN invertIcon INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE tiles ADD COLUMN autoInvertIcon INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_9_10 = object : androidx.room.migration.Migration(9, 10) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                // Populate colSpan/rowSpan from legacy size field for tiles that still have 0
                db.execSQL("""
                    UPDATE tiles SET colSpan = CASE size
                        WHEN 'TINY'   THEN 1
                        WHEN 'SQUARE' THEN 2
                        WHEN 'WIDE'   THEN 4
                        WHEN 'LARGE'  THEN 4
                        ELSE 2
                    END WHERE colSpan = 0
                """.trimIndent())
                db.execSQL("""
                    UPDATE tiles SET rowSpan = CASE size
                        WHEN 'TINY'   THEN 1
                        WHEN 'SQUARE' THEN 2
                        WHEN 'WIDE'   THEN 2
                        WHEN 'LARGE'  THEN 4
                        ELSE 2
                    END WHERE rowSpan = 0
                """.trimIndent())
            }
        }

        val MIGRATION_10_11 = object : androidx.room.migration.Migration(10, 11) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sections ADD COLUMN headerGridCol INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE sections ADD COLUMN headerGridRow INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE sections ADD COLUMN headerColSpan INTEGER NOT NULL DEFAULT 2")
                db.execSQL("ALTER TABLE sections ADD COLUMN headerRowSpan INTEGER NOT NULL DEFAULT 10")
            }
        }

        val MIGRATION_11_12 = object : androidx.room.migration.Migration(11, 12) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tiles ADD COLUMN isTextTile INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE tiles ADD COLUMN labelColor TEXT NOT NULL DEFAULT 'FFFFFF'")
                db.execSQL("ALTER TABLE tiles ADD COLUMN labelAlign INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE tiles ADD COLUMN labelFontSize INTEGER NOT NULL DEFAULT 0")
                // Mark existing text-mode tiles (no icon, had bg enabled, has a label)
                db.execSQL("UPDATE tiles SET isTextTile = 1 WHERE iconAsset = '' AND iconUri = '' AND iconBgEnabled = 1 AND label != ''")
            }
        }

        val MIGRATION_12_13 = object : androidx.room.migration.Migration(12, 13) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tiles ADD COLUMN isIconTextTile INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE tiles ADD COLUMN iconTextPosition INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_13_14 = object : androidx.room.migration.Migration(13, 14) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tiles ADD COLUMN labelBold INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE tiles ADD COLUMN labelItalic INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_14_15 = object : androidx.room.migration.Migration(14, 15) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tiles ADD COLUMN iconZoom REAL NOT NULL DEFAULT 1.0")
                db.execSQL("ALTER TABLE sections ADD COLUMN gridPreset INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
