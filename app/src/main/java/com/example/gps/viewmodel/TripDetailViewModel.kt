package com.example.gps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gps.data.db.LocationPoint
import com.example.gps.data.db.Trip
import com.example.gps.data.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TripDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TripRepository(application)

    private val _trip = MutableStateFlow<Trip?>(null)
    val trip = _trip.asStateFlow()

    private val _route = MutableStateFlow<List<LocationPoint>>(emptyList())
    val route = _route.asStateFlow()

    fun loadTripDetails(tripId: Long) {
        viewModelScope.launch {
            // Cargar los datos del viaje
            _trip.value = repository.getTrip(tripId)
            // Cargar los puntos de la ruta
            repository.getLocationPointsForTrip(tripId).collect {
                _route.value = it
            }
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            repository.deleteTrip(trip)
        }
    }
}
