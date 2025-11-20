package com.example.gps.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gps.data.repository.TripRepository
import kotlinx.coroutines.launch

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TripRepository(application)

    // --- FUNCIÓN CORREGIDA ---
    // Ahora recibe el ID del viaje y llama a la función correcta del repositorio
    fun savePhoto(tripId: Long, photoUri: Uri) {
        viewModelScope.launch {
            repository.stopTripAndAddPhoto(tripId, photoUri.toString())
        }
    }
}
