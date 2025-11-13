package com.example.gps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gps.data.db.LocationPoint
import com.example.gps.data.repository.TripRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TripRepository(application)

    // Simplemente obtenemos TODOS los puntos y los convertimos en un StateFlow
    val allTripPoints: StateFlow<List<LocationPoint>> =
        repository.getAllPoints()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
}
