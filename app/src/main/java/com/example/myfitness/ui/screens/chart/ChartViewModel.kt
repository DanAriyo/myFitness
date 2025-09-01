package com.example.myfitness.ui.screens.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfitness.data.repositories.TrainingRepository
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Date

// --- STATO ---
data class ChartState(
    val barChartEntries: List<BarEntry> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

// --- VIEWMODEL ---
class ChartViewModel(private val repository: TrainingRepository) : ViewModel() {

    private val _state = MutableStateFlow(ChartState())
    val state = _state.asStateFlow()

    init {
        // Carica i dati non appena il ViewModel viene creato
        loadTrainingData()
    }

    private fun loadTrainingData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Sostituisci con la tua logica per ottenere l'userId
                val userId = "id_utente_corrente"
                val allTrainings = repository.getAllTrainingsForUser(userId)

                // ✅ Mappa i dati in BarEntry
                val entries = allTrainings.mapIndexed { index, training ->
                    BarEntry(
                        index.toFloat(), // Asse X
                        training.calorie.toFloat(), // Asse Y
                        training.titolo // Dato aggiuntivo per l'etichetta
                    )
                }

                _state.update {
                    it.copy(
                        barChartEntries = entries,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Errore durante il caricamento dei dati: ${e.message}"
                    )
                }
            }
        }
    }
}