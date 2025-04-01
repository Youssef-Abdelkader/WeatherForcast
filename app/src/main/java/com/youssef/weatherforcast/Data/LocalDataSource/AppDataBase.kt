package com.youssef.weatherforcast.Data.LocalDataSource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.youssef.weatherforcast.Model.FavoriteLocation
import com.youssef.weatherforcast.Model.HomeData
import com.youssef.weatherforcast.Model.HomeDataConverters
import com.youssef.weatherforcast.Model.WeatherAlert
@Database(
    entities = [FavoriteLocation::class, WeatherAlert::class, HomeData::class],
    version = 4
)
@TypeConverters(HomeDataConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 1 to 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE favorite_locations ADD COLUMN countryCode TEXT DEFAULT '' NOT NULL"
                )
            }
        }

        // Migration from version 2 to 3
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS weather_alerts (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        startTime TEXT NOT NULL,
                        endTime TEXT NOT NULL,
                        type TEXT NOT NULL,
                        message TEXT NOT NULL,
                        timestamp INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        // Migration from version 3 to 4 (create Home_Data table)
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS Home_Data (
                        id INTEGER PRIMARY KEY NOT NULL,
                        weather TEXT,
                        forecast TEXT
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weather_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}