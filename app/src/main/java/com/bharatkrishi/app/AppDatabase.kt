package com.bharatkrishi.app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bharatkrishi.app.data.local.PhotoDao
import com.bharatkrishi.app.data.local.PhotoEntity

@Database(entities = [MarketData::class, PhotoEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun marketDao(): MarketDao
    abstract fun photoDao(): PhotoDao

    companion object {
        // Volatile makes sure the value of INSTANCE is always up-to-date
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Return the existing instance if it's there, otherwise create a new one
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database" // Name of the database file
                )
                .fallbackToDestructiveMigration() // Handle version change
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}