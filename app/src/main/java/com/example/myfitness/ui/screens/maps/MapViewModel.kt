// File: ui/screens/maps/MapViewModel.kt
package com.example.myfitness.ui.screens.maps

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfitness.data.models.OverpassResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.osmdroid.util.GeoPoint
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Priority

class MapViewModel : ViewModel() {

    // Stato della posizione dell'utente (ora aggiornato in tempo reale)
    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location.asStateFlow()

    private val _pois = MutableStateFlow<List<GeoPoint>>(emptyList())
    val pois: StateFlow<List<GeoPoint>> = _pois.asStateFlow()

    private val _isPermissionDenied = MutableStateFlow(false)
    val isPermissionDenied: StateFlow<Boolean> = _isPermissionDenied.asStateFlow()

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                _location.value = location
            }
        }
    }

    // ✅ Nuovo metodo per avviare gli aggiornamenti della posizione
    fun startLocationUpdates(context: Context, locationClient: FusedLocationProviderClient) {
        // Se il permesso non è stato concesso, non fare nulla
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000 // interval
        ).apply {
            setMinUpdateIntervalMillis(3000) // fastestInterval
        }.build()

        locationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    // ✅ Nuovo metodo per fermare gli aggiornamenti della posizione quando il ViewModel non è più in uso
    fun stopLocationUpdates(locationClient: FusedLocationProviderClient) {
        locationClient.removeLocationUpdates(locationCallback)
    }

    fun setPermissionDenied(denied: Boolean) {
        _isPermissionDenied.value = denied
    }

    fun searchNearbyPois(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                // Aggiungi un log per verificare che la funzione venga chiamata
                Log.d("MapViewModel", "Cercando POI per: Lat=$latitude, Lon=$longitude")

                val overpassQuery = "[out:json];node(around:5000,$latitude,$longitude)[\"leisure\"=\"fitness_centre\"];node(around:5000,$latitude,$longitude)[\"amenity\"=\"gym\"];out body;"

                val response: OverpassResponse = httpClient.get("https://overpass-api.de/api/interpreter") {
                    parameter("data", overpassQuery)
                }.body()

                // Aggiungi un log per verificare che la risposta sia valida
                Log.d("MapViewModel", "Risposta da Overpass: ${response.elements.size} elementi trovati.")

                val poiList = response.elements.map { GeoPoint(it.lat, it.lon) }
                _pois.value = poiList
            } catch (e: Exception) {
                // Aggiungi un log per catturare e visualizzare l'errore
                Log.e("MapViewModel", "Errore nella ricerca dei POI: ${e.message}", e)
                _pois.value = emptyList()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        httpClient.close()
    }
}