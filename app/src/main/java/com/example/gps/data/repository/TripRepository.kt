package com.example.gps.data.repository

import android.content.Context
import com.example.gps.data.db.AppDatabase
import com.example.gps.data.db.LocationPoint
import com.example.gps.data.db.Trip
import kotlinx.coroutines.flow.Flow

class TripRepository(context: Context) {
    private val tripDao = AppDatabase.getDatabase(context).tripDao()

    // --- Funciones para los ViewModels ---

    fun getAllCompletedTrips(): Flow<List<Trip>> = tripDao.getAllCompletedTrips()

    fun getLocationPointsForTrip(tripId: Long): Flow<List<LocationPoint>> {
        return tripDao.getLocationPointsForTrip(tripId)
    }

    // ¡¡¡FUNCIÓN CORREGIDA PARA EL MAPA GLOBAL!!!
    fun getAllLocationPoints(): Flow<List<LocationPoint>> = tripDao.getAllLocationPoints()

    suspend fun startNewTrip(): Long {
        return tripDao.startNewTrip(Trip())
    }

    suspend fun saveLocationPoint(point: LocationPoint) {
        tripDao.insertLocationPoint(point)
    }

    suspend fun getTrip(tripId: Long): Trip? {
        return tripDao.getTripById(tripId)
    }

    // Se usa al guardar un viaje nuevo desde la pantalla de edición
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

    // Se usará en el futuro para editar
    suspend fun updateTripDetails(tripId: Long, title: String, description: String) {
        val trip = tripDao.getTripById(tripId)
        trip?.let {
            it.title = title
            it.description = description
            tripDao.updateTrip(it)
        }
    }

    suspend fun deleteTrip(trip: Trip) {
        tripDao.deletePointsForTrip(trip.id)
        tripDao.deleteTrip(trip)
    }
}
