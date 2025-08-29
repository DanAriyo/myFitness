// File: ui/screens/maps/MapViewModel.kt
package com.example.myfitness.ui.screens.maps

import android.location.Location
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location.asStateFlow()

    // Questa nuova funzione aggiorna semplicemente la posizione nello stato del ViewModel.
    // Viene chiamata dalla UI quando la posizione è disponibile e sicura da usare.
    fun updateLocation(newLocation: Location) {
        _location.value = newLocation
    }
}