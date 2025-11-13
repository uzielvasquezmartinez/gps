package com.example.gps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gps.data.db.Trip
import com.example.gps.data.repository.TripRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TripRepository(application)

    val completedTrips: StateFlow<List<Trip>> =
        repository.getAllCompletedTrips()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
}
