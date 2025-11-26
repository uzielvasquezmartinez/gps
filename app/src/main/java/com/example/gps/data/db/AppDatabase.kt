package com.example.gps.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// ¡¡¡VERSIÓN INCREMENTADA A 3 PARA FORZAR LA RECONSTRUCCIÓN TOTAL DE LA BD!!!
@Database(entities = [Trip::class, LocationPoint::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trip_database"
                )
                .fallbackToDestructiveMigration() // Permite reconstruir la BD si el esquema cambia
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
