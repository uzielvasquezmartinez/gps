package com.example.gps.ui.edit

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gps.viewmodel.EditTripViewModel

@Composable
fun EditTripScreen(
    navController: NavController,
    tripId: Long,
    photoUri: String?,
    viewModel: EditTripViewModel = viewModel()
) {
    val trip by viewModel.trip.collectAsState()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val isNewTrip = photoUri != null

    LaunchedEffect(tripId) {
        if (!isNewTrip) { // Si NO es un viaje nuevo, cargamos sus datos
            viewModel.loadTrip(tripId)
        }
    }

    LaunchedEffect(trip) {
        trip?.let {
            title = it.title
            description = it.description
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val imageToShow = photoUri ?: trip?.photoUri
            imageToShow?.let {
                AsyncImage(
                    model = Uri.parse(it),
                    contentDescription = "Foto del viaje",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f),
                    contentScale = ContentScale.Crop
                )
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isNewTrip) {
                        viewModel.saveNewTrip(tripId, photoUri!!, title, description)
                        // Navegación para un viaje NUEVO: volvemos a la galería, limpiando el stack
                        navController.navigate("gallery") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    } else {
                        viewModel.updateTrip(tripId, title, description)
                        // Navegación para una EDICIÓN: simplemente volvemos atrás
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isNewTrip) "Guardar Viaje" else "Guardar Cambios")
            }
        }
    }
}
