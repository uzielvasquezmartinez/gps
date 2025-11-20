package com.example.gps.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gps.viewmodel.MapViewModel
// IMPORTS CORRECTOS
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.Polyline
import com.utsman.osmandcompose.rememberCameraState
import org.osmdroid.util.GeoPoint

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel()
) {
    // ¡¡¡VARIABLE CORREGIDA!!!
    val allPoints by viewModel.allPoints.collectAsState(initial = emptyList())
    val pointsByTrip = allPoints.groupBy { it.tripId }

    val cameraState = rememberCameraState {
        geoPoint = GeoPoint(40.416775, -3.703790) // Por defecto en Madrid
        zoom = 6.0
    }

    // Efecto para centrar el mapa cuando los puntos cargan
    LaunchedEffect(allPoints) {
        allPoints.firstOrNull()?.let { firstPoint ->
            val firstGeoPoint = GeoPoint(firstPoint.latitude, firstPoint.longitude)
            cameraState.geoPoint = firstGeoPoint // Actualizamos la posición
            cameraState.zoom = 15.0             // y el zoom
        }
    }

    OpenStreetMap(
        modifier = Modifier.fillMaxSize(),
        cameraState = cameraState
    ) {
        // Dibujamos una línea por cada viaje que tengamos
        pointsByTrip.forEach { (_, points) ->
            val geoPointList = points.map { point -> GeoPoint(point.latitude, point.longitude) }
            if (geoPointList.size >= 2) {
                Polyline(
                    geoPoints = geoPointList
                )
            }
        }
    }
}
