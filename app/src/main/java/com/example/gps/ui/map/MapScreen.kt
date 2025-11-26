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
    val allPoints by viewModel.allPoints.collectAsState(initial = emptyList())
    val pointsByTrip = allPoints.groupBy { it.tripId }

    // --- COORDENADAS Y ZOOM CAMBIADOS ---
    // Coordenadas: 18°51'04.0"N 99°12'02.3"W -> 18.851111, -99.200639
    val cameraState = rememberCameraState {
        geoPoint = GeoPoint(18.851111, -99.200639) // UTEZ
        zoom = 17.0 // Zoom para ver la universidad
    }

    // Este efecto centrará el mapa en el último viaje si existe alguno.
    // Si no hay viajes, se quedará en la UTEZ.
    LaunchedEffect(allPoints) {
        allPoints.firstOrNull()?.let { firstPoint ->
            val firstGeoPoint = GeoPoint(firstPoint.latitude, firstPoint.longitude)
            cameraState.geoPoint = firstGeoPoint
            cameraState.zoom = 15.0
        }
    }

    OpenStreetMap(
        modifier = Modifier.fillMaxSize(),
        cameraState = cameraState
    ) {
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
