package com.example.gps.data.repository

import android.content.Context
import com.example.gps.data.db.AppDatabase
import com.example.gps.data.db.LocationPoint
import com.example.gps.data.db.Trip
import kotlinx.coroutines.flow.Flow

class TripRepository(context: Context) {
    private val tripDao = AppDatabase.getDatabase(context).tripDao()

    // --- Flujos de Datos para la UI ---
    fun getAllCompletedTrips(): Flow<List<Trip>> = tripDao.getAllCompletedTrips()

    fun getLocationPointsFor(tripId: Long): Flow<List<LocationPoint>> {
        return tripDao.getLocationPointsForTrip(tripId)
    }

    // --- Acciones de la UI ---
    suspend fun startNewTrip(): Long {
        return tripDao.startNewTrip(Trip())
    }

    suspend fun saveLocationPoint(point: LocationPoint) {
        tripDao.insertLocationPoint(point)
    }

    suspend fun getTrip(tripId: Long): Trip? {
        return tripDao.getTripById(tripId)
    }

    suspend fun saveTripDetails(tripId: Long, photoUri: String, title: String, description: String) {
        val trip = tripDao.getTripById(tripId)
        trip?.let {
            it.endTime = System.currentTimeMillis()
            it.photoUri = photoUri
            it.title = title
            it.description = description
            tripDao.updateTrip(it)
        }
    }

    suspend fun updateTripDetails(tripId: Long, title: String, description: String) {
        val trip = tripDao.getTripById(tripId)
        trip?.let {
            it.title = title
            it.description = description
            tripDao.updateTrip(it)
        }
    }

    suspend fun deleteTrip(trip: Trip) {
        // Borramos los puntos y luego el viaje
        tripDao.deletePointsForTrip(trip.id)
        tripDao.deleteTrip(trip)
    }
}
