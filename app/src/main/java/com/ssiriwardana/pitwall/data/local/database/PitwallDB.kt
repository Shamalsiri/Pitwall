package com.ssiriwardana.pitwall.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ssiriwardana.pitwall.data.local.dao.DriverDao
import com.ssiriwardana.pitwall.data.local.entity.DriverEntity

/**
 * Room db for the app
 */
@Database(
    entities = [DriverEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PitwallDB: RoomDatabase() {

    abstract fun driverDao(): DriverDao

    companion object {
        @Volatile
        private var INSTANCE: PitwallDB? = null

        /**
         * Get database singleton instance
         */
        fun getInstance(context: Context): PitwallDB {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {INSTANCE == it}
            }
        }

        /**
         * Build database
         */
        private fun buildDatabase(context: Context): PitwallDB {
            return Room.databaseBuilder(
                context.applicationContext,
                PitwallDB::class.java,
                "f1_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}