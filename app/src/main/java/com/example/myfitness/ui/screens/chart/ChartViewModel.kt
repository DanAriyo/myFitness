// ui/screens/chart/ChartViewModel.kt

package com.example.myfitness.ui.screens.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfitness.data.repositories.TrainingRepository
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- STATO ---
data class ChartState(
    val barChartEntries: List<BarEntry> = emptyList(),
    val xAxisLabels: List<String> = emptyList(), // ✅ Nuova lista per le etichette dell'asse X
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

// --- AZIONI ---
interface ChartActions {
    fun loadTrainingData(userId: String)
}

// --- VIEWMODEL ---
class ChartViewModel(private val repository: TrainingRepository) : ViewModel() {

    private val _state = MutableStateFlow(ChartState())
    val state = _state.asStateFlow()

    val actions: ChartActions = object : ChartActions {
        override fun loadTrainingData(userId: String) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    val allTrainings = repository.getAllTrainingsForUser(userId)

                    val entries = allTrainings.mapIndexed { index, training ->
                        BarEntry(
                            index.toFloat(),
                            training.calorie.toFloat(),
                        )
                    }
                    val labels = allTrainings.map { it.titolo } // ✅ Estrai le etichette

                    _state.update {
                        it.copy(
                            barChartEntries = entries,
                            xAxisLabels = labels, // ✅ Salva le etichette nello stato
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
}