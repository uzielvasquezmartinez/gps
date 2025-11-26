package com.example.gps.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gps.ui.camera.CameraScreen
import com.example.gps.ui.detail.TripDetailScreen
import com.example.gps.ui.edit.EditTripScreen
import com.example.gps.ui.gallery.GalleryScreen
import com.example.gps.ui.map.MapScreen
import com.example.gps.ui.tracking.TrackingScreen

// Definimos las rutas
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Tracking : Screen("tracking", "Grabar", Icons.Default.LocationOn)
    object MapScreen : Screen("map", "Mapa", Icons.Default.Place)
    object Gallery : Screen("gallery", "Galería", Icons.AutoMirrored.Filled.List)
}

// --- RUTAS CON ARGUMENTOS ---
const val CAMERA_ROUTE_TEMPLATE = "camera/{tripId}"
// La URI de la foto es ahora un ARGUMENTO OPCIONAL para poder reutilizar la pantalla
const val EDIT_TRIP_ROUTE_TEMPLATE = "edit_trip/{tripId}?photoUri={photoUri}"
const val TRIP_DETAIL_ROUTE_TEMPLATE = "trip_detail/{tripId}"

val bottomNavItems = listOf(
    Screen.Tracking,
    Screen.MapScreen,
    Screen.Gallery
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Tracking.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Tracking.route) { TrackingScreen(navController = navController) }
            composable(Screen.MapScreen.route) { MapScreen() }
            composable(Screen.Gallery.route) { GalleryScreen(navController = navController) }

            composable(
                route = CAMERA_ROUTE_TEMPLATE,
                arguments = listOf(navArgument("tripId") { type = NavType.LongType })
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getLong("tripId") ?: -1L
                CameraScreen(navController, tripId)
            }

            composable(
                route = EDIT_TRIP_ROUTE_TEMPLATE,
                arguments = listOf(
                    navArgument("tripId") { type = NavType.LongType },
                    navArgument("photoUri") {
                        type = NavType.StringType
                        nullable = true // Marcamos como opcional
                    }
                )
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getLong("tripId") ?: -1L
                val photoUri = backStackEntry.arguments?.getString("photoUri")
                EditTripScreen(navController, tripId, photoUri)
            }

            composable(
                route = TRIP_DETAIL_ROUTE_TEMPLATE,
                arguments = listOf(navArgument("tripId") { type = NavType.LongType })
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getLong("tripId") ?: -1L
                TripDetailScreen(navController, tripId)
            }
        }
    }
}
