package com.youssef.weatherforcast.Data.LocalDataSource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.youssef.weatherforcast.Model.FavoriteLocation

@Database(entities = [FavoriteLocation::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE favorite_locations ADD COLUMN countryCode TEXT DEFAULT '' NOT NULL")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weather_database"
                )
                    .addMigrations(MIGRATION_1_2) // ✅ الترحيل يعمل الآن بدون مشاكل
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
