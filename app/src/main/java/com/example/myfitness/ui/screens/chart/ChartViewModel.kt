package com.example.myfitness.ui.screens.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfitness.data.models.Training
import com.example.myfitness.data.repositories.TrainingRepository
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import com.google.firebase.Timestamp // Make sure this import exists
import java.util.Date // Make sure this import exists

// --- STATO ---
data class ChartState(
    val barChartEntries: List<BarEntry> = emptyList(),
    val lineChartEntries: List<Entry> = emptyList(),
    val xAxisLabels: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

// --- AZIONI ---
interface ChartActions {
    fun loadTrainingData(userId: String)
    fun loadWeeklyTrainingData(userId: String)
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
                    val labels = allTrainings.map { it.titolo }

                    _state.update {
                        it.copy(
                            barChartEntries = entries,
                            xAxisLabels = labels,
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

        override fun loadWeeklyTrainingData(userId: String) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    val allTrainings = repository.getAllTrainingsForUser(userId)

                    val weeklyCounts = calculateWeeklyTrainings(allTrainings)

                    val entries = weeklyCounts.keys.sorted().map { weekIndex ->
                        Entry(
                            weekIndex.toFloat(),
                            weeklyCounts[weekIndex]!!.toFloat()
                        )
                    }
                    val labels = listOf("Settimana 4", "Settimana 3", "Settimana 2", "Corrente")

                    _state.update {
                        it.copy(
                            lineChartEntries = entries,
                            xAxisLabels = labels,
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Errore durante il caricamento dei dati settimanali: ${e.message}"
                        )
                    }
                }
            }
        }
    }

    private fun calculateWeeklyTrainings(trainings: List<Training>): Map<Int, Int> {
        val weeklyCounts = mutableMapOf<Int, Int>()
        val calendar = Calendar.getInstance()

        val now = calendar.timeInMillis
        val daysInAWeek = TimeUnit.DAYS.toMillis(7)

        for (i in 0 until 4) {
            val weekStartMillis = now - (i * daysInAWeek)
            val weekEndMillis = now - ((i - 1) * daysInAWeek)

            val count = trainings.count {
                val trainingTime = it.data.toDate().time // Corrected to use toDate().time
                trainingTime >= weekStartMillis && trainingTime < weekEndMillis
            }
            weeklyCounts[3 - i] = count
        }
        return weeklyCounts
    }
}