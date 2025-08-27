package com.example.myfitness.ui.screens.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Stato di un singolo esercizio
data class Exercise(
    val id: String = "",
    val nome: String = "",
    val descrizione: String = "",
    val ripetizioni: Int = 0,
    val serie: Int = 0,
    val categoria: String = ""
)

// Stato globale per la schermata degli esercizi
data class ExerciseState(
    val esercizi: List<Exercise> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

interface ExerciseActions {
    fun loadExercises(trainingId: String)
    fun addExercise(exercise: Exercise)
    fun removeExercise(exerciseId: String)
}

class ExerciseViewModel : ViewModel() {

    private val _state = MutableStateFlow(ExerciseState())
    val state = _state.asStateFlow()

    val actions: ExerciseActions = object : ExerciseActions {

        override fun loadExercises(trainingId: String) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    // 🔹 Qui in futuro caricheremo i dati da Firebase
                    // Ora simuliamo con dati finti
                    val eserciziFake = listOf(
                        Exercise("1", "Push-up", "Piegamenti sulle braccia", 15, 3, "Forza"),
                        Exercise("2", "Squat", "Squat a corpo libero", 20, 4, "Gambe"),
                        Exercise("3", "Plank", "Plank 60 secondi", 1, 3, "Core")
                    )
                    _state.update { it.copy(esercizi = eserciziFake, isLoading = false) }
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            }
        }

        override fun addExercise(exercise: Exercise) {
            _state.update { it.copy(esercizi = it.esercizi + exercise) }
        }

        override fun removeExercise(exerciseId: String) {
            _state.update { it.copy(esercizi = it.esercizi.filterNot { e -> e.id == exerciseId }) }
        }
    }
}
