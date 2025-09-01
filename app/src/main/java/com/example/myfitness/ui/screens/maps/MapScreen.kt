// File: ui/screens/maps/MapScreen.kt
package com.example.myfitness.ui.screens.maps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.unit.dp
import com.example.myfitness.ui.composables.TopBar
import com.example.myfitness.ui.composables.BottomBar

@Composable
fun MapScreen(
    viewModel: MapViewModel = koinViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val locationState by viewModel.location.collectAsState()
    val poisState by viewModel.pois.collectAsState()
    val isPermissionDenied by viewModel.isPermissionDenied.collectAsState()

    val locationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                viewModel.startLocationUpdates(context, locationClient)
                viewModel.setPermissionDenied(false)
            } else {
                viewModel.setPermissionDenied(true)
            }
        }
    )

    LaunchedEffect(Unit) {
        val permissionStatus = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            viewModel.startLocationUpdates(context, locationClient)
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopLocationUpdates(locationClient)
        }
    }

    LaunchedEffect(locationState) {
        locationState?.let { userLocation ->
            viewModel.searchNearbyPois(userLocation.latitude, userLocation.longitude)
        }
    }

    Scaffold(
        topBar = { TopBar(title = "Mappa delle Attività") }, // ✅ Aggiungi la TopBar
        bottomBar = { BottomBar(navController) } // ✅ Aggiungi la BottomBar
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isPermissionDenied) {
                Text(
                    "Per visualizzare la mappa, è necessario concedere i permessi di localizzazione.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                locationState?.let { userLocation ->
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            MapView(context).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                controller.setZoom(15.0)
                                controller.setCenter(
                                    GeoPoint(userLocation.latitude, userLocation.longitude)
                                )
                                tag = Marker(this).apply {
                                    position = GeoPoint(userLocation.latitude, userLocation.longitude)
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    title = "Sei qui"
                                }
                                overlays.add(tag as Marker)
                            }
                        },
                        update = { mapView ->
                            mapView.overlays.clear()
                            val userMarker = mapView.tag as? Marker
                            if (userMarker != null) {
                                userMarker.position = GeoPoint(userLocation.latitude, userLocation.longitude)
                                mapView.overlays.add(userMarker)
                            }

                            poisState.forEach { poi ->
                                val poiMarker = Marker(mapView)
                                poiMarker.position = poi
                                poiMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                poiMarker.title = "POI Trovato"
                                mapView.overlays.add(poiMarker)
                            }
                            mapView.controller.animateTo(
                                GeoPoint(userLocation.latitude, userLocation.longitude)
                            )
                            mapView.invalidate()
                        }
                    )
                } ?: run {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Cercando la tua posizione...")
                        }
                    }
                }
            }
        }
    }
}