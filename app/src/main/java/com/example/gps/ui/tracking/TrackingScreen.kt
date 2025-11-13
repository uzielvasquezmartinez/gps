package com.example.gps.ui.tracking

import android.Manifest
import android.content.Context
import android.net.Uri
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gps.viewmodel.TrackingViewModel
import java.io.File

@Composable
fun TrackingScreen(
    viewModel: TrackingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // --- Lógica de Cámara ---
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) } 

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            // Se llama cuando la cámara termina
            if (success) {
                tempPhotoUri?.let { uri ->
                    viewModel.savePhotoAndUpdateTrip(uri)
                }
            }
        }
    )

    // --- Lógica de Permisos ---
    val permissionsToRequest = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            // Aquí puedes manejar si el usuario deniega los permisos
            val allGranted = permissions.values.all { it }
            if (!allGranted) {
                // Mostrar un mensaje al usuario, por ejemplo, con un Snackbar.
            }
        }
    )

    // Pedir permisos en cuanto la pantalla aparece
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
                        viewModel.onStartStopClick() // Llama a stopRecording()
                        // 2. Preparamos la URI y lanzamos la cámara para la foto final
                        generateTempUri(context)?.let {
                            tempPhotoUri = it
                            cameraLauncher.launch(it)
                        }
                    } else {
                        // Si no está grabando, empezamos
                        viewModel.onStartStopClick() // Llama a startRecording()
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

// Función helper para crear una URI temporal donde guardar la foto
private fun generateTempUri(context: Context): Uri? {
    val file = File(context.filesDir, "trip_photo_${System.currentTimeMillis()}.jpg")
    val authority = "${context.packageName}.provider"
    return try {
        FileProvider.getUriForFile(context, authority, file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
