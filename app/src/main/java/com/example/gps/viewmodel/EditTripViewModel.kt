package com.example.gps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gps.data.db.Trip
import com.example.gps.data.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditTripViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TripRepository(application)

    private val _trip = MutableStateFlow<Trip?>(null)
    val trip = _trip.asStateFlow()

    fun loadTrip(tripId: Long) {
        viewModelScope.launch {
            _trip.value = repository.getTrip(tripId)
        }
    }

    // Se llama al finalizar un nuevo viaje (desde la cámara)
    fun saveNewTrip(tripId: Long, photoUri: String, title: String, description: String) {
        viewModelScope.launch {
            repository.saveTripDetails(tripId, photoUri, title, description)
        }
    }

    // Se llamará en el futuro para editar desde la galería
    fun updateTrip(tripId: Long, title: String, description: String) {
        viewModelScope.launch {
            repository.updateTripDetails(tripId, title, description)
        }
    }

    // Se llamará en el futuro para borrar desde la galería o el detalle
    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            repository.deleteTrip(trip)
        }
    }
}
