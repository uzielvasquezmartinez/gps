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
    photoUri: String,
    viewModel: EditTripViewModel = viewModel()
) {
    val trip by viewModel.trip.collectAsState()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Cargar los datos del viaje cuando la pantalla se inicia
    LaunchedEffect(tripId) {
        if (tripId != -1L) { // -1L es el valor por defecto si es un viaje nuevo
            viewModel.loadTrip(tripId)
        }
    }

    // Actualizar los campos de texto cuando se cargan los datos del viaje
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
            AsyncImage(
                model = Uri.parse(photoUri),
                contentDescription = "Foto del viaje",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f),
                contentScale = ContentScale.Crop
            )

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
                    // Guardamos y volvemos a la galería
                    viewModel.saveOrUpdateTrip(tripId, photoUri, title, description)
                    navController.popBackStack(navController.graph.startDestinationId, false)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Viaje")
            }
        }
    }
}
