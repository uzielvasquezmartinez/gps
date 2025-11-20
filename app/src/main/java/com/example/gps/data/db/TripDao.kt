package com.example.gps.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    // --- Operaciones de Escritura ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun startNewTrip(trip: Trip): Long

    @Update
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationPoint(point: LocationPoint)

    @Query("DELETE FROM location_points WHERE tripId = :tripId")
    suspend fun deletePointsForTrip(tripId: Long)

    // --- Operaciones de Lectura ---
    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: Long): Trip?

    @Query("SELECT * FROM trips WHERE photoUri IS NOT NULL ORDER BY endTime DESC")
    fun getAllCompletedTrips(): Flow<List<Trip>>

    @Query("SELECT * FROM location_points WHERE tripId = :tripId ORDER BY timestamp ASC")
    fun getLocationPointsForTrip(tripId: Long): Flow<List<LocationPoint>>

    // ¡¡¡FUNCIÓN RESTAURADA PARA EL MAPA GLOBAL!!!
    @Query("SELECT * FROM location_points ORDER BY timestamp ASC")
    fun getAllLocationPoints(): Flow<List<LocationPoint>>
}
