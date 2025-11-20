package com.example.gps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gps.data.db.LocationPoint
import com.example.gps.data.location.LocationClient
import com.example.gps.data.repository.TripRepository
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrackingUiState(
    val isRecording: Boolean = false,
    val currentTripId: Long? = null
)

class TrackingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TripRepository(application)
    private val locationClient = LocationClient(
        application,
        LocationServices.getFusedLocationProviderClient(application)
    )

    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState = _uiState.asStateFlow()

    private var locationJob: Job? = null

    fun onStartStopClick() {
        if (_uiState.value.isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        locationJob?.cancel() // Cancela cualquier trabajo anterior
        viewModelScope.launch {
            val newTripId = repository.startNewTrip()
            _uiState.update { TrackingUiState(isRecording = true, currentTripId = newTripId) }

            locationJob = locationClient.getLocationUpdates(5000L) // cada 5 seg
                .catch { e -> e.printStackTrace() } // Manejar error
                .onEach { location ->
                    val point = LocationPoint(
                        tripId = newTripId,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        timestamp = System.currentTimeMillis()
                    )
                    repository.saveLocationPoint(point)
                }
                .launchIn(viewModelScope) // Lanza la corutina
        }
    }

    private fun stopRecording() {
        locationJob?.cancel()
        // Ya no detenemos el viaje aquí, solo la grabación de puntos.
        // El viaje se finaliza en la pantalla de edición.
        _uiState.update { it.copy(isRecording = false) }
    }
}
