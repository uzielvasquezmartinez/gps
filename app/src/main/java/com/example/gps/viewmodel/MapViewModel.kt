package com.example.gps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.gps.data.repository.TripRepository

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TripRepository(application)

    // ¡¡¡LLAMADA CORREGIDA!!!
    val allPoints = repository.getAllLocationPoints()
}
