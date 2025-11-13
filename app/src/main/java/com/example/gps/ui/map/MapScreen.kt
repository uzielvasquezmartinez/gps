package com.example.gps.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gps.viewmodel.MapViewModel
// IMPORTS CORRECTOS: Solo usamos la librería de Utsman
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.Polyline
import com.utsman.osmandcompose.rememberCameraState
import org.osmdroid.util.GeoPoint

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel()
) {
    val allPoints by viewModel.allTripPoints.collectAsState(initial = emptyList())
    val pointsByTrip = allPoints.groupBy { it.tripId }

    // Usamos rememberCameraState de la librería correcta
    val cameraState = rememberCameraState {
        geoPoint = GeoPoint(40.416775, -3.703790) // Por defecto en Madrid
        zoom = 6.0
    }

    // Efecto para centrar el mapa cuando los puntos cargan
    LaunchedEffect(allPoints) {
        allPoints.firstOrNull()?.let {
            val firstGeoPoint = GeoPoint(it.latitude, it.longitude)
            cameraState.geoPoint = firstGeoPoint // Actualizamos la posición
            cameraState.zoom = 15.0             // y el zoom
        }
    }

    // Usamos el OpenStreetMap correcto, que pide un cameraState
    OpenStreetMap(
        modifier = Modifier.fillMaxSize(),
        cameraState = cameraState
    ) {
        // Dibujamos una línea por cada viaje que tengamos
        pointsByTrip.forEach { (_, points) ->
            val geoPointList = points.map { GeoPoint(it.latitude, it.longitude) }
            if (geoPointList.size >= 2) {
                // Usamos el Polyline correcto, que pide geoPoints
                Polyline(
                    geoPoints = geoPointList
                )
            }
        }
    }
}
