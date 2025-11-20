package com.example.gps.ui.tracking

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gps.viewmodel.TrackingViewModel


@Composable
fun TrackingScreen(
    navController: NavController, // ¡¡¡PARÁMETRO AÑADIDO!!!
    viewModel: TrackingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // --- Lógica de Permisos ---
    val permissionsToRequest = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val allGranted = permissions.values.all { it }
            if (!allGranted) {
                // Manejar permisos denegados
            }
        }
    )

    // Pedir permisos al inicio
    LaunchedEffect(key1 = true) {
        permissionLauncher.launch(permissionsToRequest)
    }

    // --- UI ---
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (uiState.isRecording) {
                        // 1. Si está grabando, paramos lógicamente
                        viewModel.onStartStopClick()
                        // 2. Navegamos a nuestra propia CameraScreen con el ID del viaje
                        uiState.currentTripId?.let {
                            navController.navigate("camera/$it")
                        }
                    } else {
                        // Si no está grabando, empezamos
                        viewModel.onStartStopClick()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(16.dp)
            ) {
                Text(if (uiState.isRecording) "Detener y tomar foto" else "Iniciar Grabación")
            }

            if (uiState.isRecording) {
                Text("Grabando recorrido #${uiState.currentTripId}...")
            }
        }
    }
}
