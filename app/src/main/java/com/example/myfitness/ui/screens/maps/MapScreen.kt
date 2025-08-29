// File: ui/screens/maps/MapScreen.kt
package com.example.myfitness.ui.screens.maps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController // ✅ Import NavController
import com.google.android.gms.location.LocationServices
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(
    viewModel: MapViewModel = koinViewModel(),
    navController: NavController // ✅ Aggiungi il NavController qui
) {
    val context = LocalContext.current
    val locationState by viewModel.location.collectAsState()

    // Lanciatore di permessi. Viene creato una sola volta.
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                // Se il permesso è concesso, procedi a ottenere la posizione
                val locationClient = LocationServices.getFusedLocationProviderClient(context)
                locationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        viewModel.updateLocation(location)
                    }
                }
            } else {
                // TODO: Gestisci il caso in cui i permessi sono negati
            }
        }
    )

    // Esegui questo codice una sola volta all'avvio della schermata
    LaunchedEffect(Unit) {
        val permissionStatus = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            // Se il permesso è già concesso, ottieni subito la posizione
            val locationClient = LocationServices.getFusedLocationProviderClient(context)
            locationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.updateLocation(location)
                }
            }
        } else {
            // Altrimenti, lancia la richiesta di permesso
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Check if location data is available
        locationState?.let { userLocation ->
            // Create an AndroidView to host the OSMDroid MapView
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    MapView(context).apply {
                        // Set up the map controller with the user's location
                        controller.setZoom(15.0) // Set initial zoom level
                        controller.setCenter(
                            GeoPoint(userLocation.latitude, userLocation.longitude)
                        )

                        // Add a marker for the user's position
                        val userMarker = Marker(this)
                        userMarker.position = GeoPoint(userLocation.latitude, userLocation.longitude)
                        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        userMarker.title = "Sei qui"
                        overlays.add(userMarker)
                    }
                }
            )
        } ?: run {
            // Show a loading indicator if the location is not yet available
            CircularProgressIndicator()
            Text("Cercando la tua posizione...")
        }
    }

}