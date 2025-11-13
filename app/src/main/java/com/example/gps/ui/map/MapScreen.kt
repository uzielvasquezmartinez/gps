package com.example.gps.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gps.viewmodel.MapViewModel
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.Polyline
import org.osmdroid.compose.OpenStreetMap
import org.osmdroid.compose.rememberMapController
import org.osmdroid.util.GeoPoint


@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel()
) {
    val allPoints by viewModel.allTripPoints.collectAsState()
    // Esto se mantiene igual
    val pointsByTrip = allPoints.groupBy { it.tripId }
    // CAMBIO: Usamos el controlador de OSM en lugar de CameraPositionState
    val mapController = rememberMapController()

    // NUEVO: Usamos LaunchedEffect para centrar el mapa cuando los puntos carguen
    LaunchedEffect(allPoints) {
        allPoints.firstOrNull()?.let {
            // Centrar la cámara en el primer punto (si existe)
            // 1. Convertimos a GeoPoint
            val firstGeoPoint = GeoPoint(it.latitude, it.longitude)
            // 2. Le decimos al controlador que se mueva allí
            mapController.setZoom(10.0) // OSM usa Double para el zoom
            mapController.setCenter(firstGeoPoint)
        }
    }

    // CAMBIO: Usamos el Composable de OpenStreetMap
    OpenStreetMap(
        modifier = Modifier.fillMaxSize(),
        controller = mapController // Le pasamos el controlador
    ) {
        pointsByTrip.forEach { (tripId, points) ->
            // CAMBIO: Convertimos la lista de puntos a GeoPoint en lugar de LatLng
            val geoPointList = points.map { GeoPoint(it.latitude, it.longitude) }
            if (geoPointList.size >= 2) {
                // CAMBIO: Usamos el Composable Polyline de OSM
                Polyline(
                    points = geoPointList
                    // Puedes añadir colores, etc., de forma similar
                )
            }
        }
    }
}
