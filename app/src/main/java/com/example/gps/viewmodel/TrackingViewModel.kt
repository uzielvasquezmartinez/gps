package com.example.gps.viewmodel

import android.app.Application
import android.net.Uri
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
            // Solo para la lógica de detener la grabación de puntos
            locationJob?.cancel()
            _uiState.update { it.copy(isRecording = false) }
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        locationJob?.cancel()
        viewModelScope.launch {
            val newTripId = repository.startNewTrip()
            _uiState.update { TrackingUiState(isRecording = true, currentTripId = newTripId) }

            locationJob = locationClient.getLocationUpdates(5000L)
                .catch { e -> e.printStackTrace() } 
                .onEach { location ->
                    val point = LocationPoint(
                        tripId = newTripId,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        timestamp = System.currentTimeMillis()
                    )
                    repository.saveLocationPoint(point)
                }
                .launchIn(viewModelScope)
        }
    }

    // --- FUNCIÓN UNIFICADA Y CORREGIDA ---
    // Se llama DESPUÉS de que la cámara toma la foto
    fun savePhotoAndUpdateTrip(photoUri: Uri) {
        val tripId = _uiState.value.currentTripId ?: return
        viewModelScope.launch {
            repository.stopTripAndAddPhoto(tripId, photoUri.toString())
            // Reseteamos el estado para el próximo viaje
            _uiState.update { TrackingUiState(isRecording = false, currentTripId = null) }
        }
    }
}
