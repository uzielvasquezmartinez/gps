package com.example.gps.data.repository

import android.content.Context
import com.example.gps.data.db.AppDatabase
import com.example.gps.data.db.LocationPoint
import com.example.gps.data.db.Trip

class TripRepository(context: Context) {
    private val tripDao = AppDatabase.getDatabase(context).tripDao()

    fun getAllPoints() = tripDao.getAllLocationPoints()
    fun getAllCompletedTrips() = tripDao.getAllCompletedTrips()

    suspend fun startNewTrip(): Long {
        return tripDao.startNewTrip(Trip())
    }

    suspend fun saveLocationPoint(point: LocationPoint) {
        tripDao.insertLocationPoint(point)
    }

    // --- FUNCIÓN UNIFICADA Y CORREGIDA ---
    suspend fun stopTripAndAddPhoto(tripId: Long, photoUri: String) {
        val trip = tripDao.getTripById(tripId)
        trip?.let {
            it.endTime = System.currentTimeMillis()
            it.photoUri = photoUri
            tripDao.updateTrip(it)
        }
    }
}
