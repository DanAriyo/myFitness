package com.example.myfitness.ui.screens.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfitness.data.models.Exercise
import com.example.myfitness.data.models.Training
import com.example.myfitness.data.repositories.TrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// --- STATE ---
data class TrainingState(
    val trainingName: String = "",
    val exercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// --- ACTIONS ---
interface TrainingActions {
    fun onTrainingNameChange(name: String)
    fun addExercise()
    fun onExerciseNameChange(index: Int, name: String)
    fun onExerciseSetsChange(index: Int, sets: Int)
    fun onExerciseRepsChange(index: Int, reps: Int)
    fun onExerciseDurationChange(index: Int, duration: String)
    fun saveTraining(userId: String)
}

// --- VIEWMODEL ---
class TrainingViewModel(
    private val trainingRepository: TrainingRepository // <-- Dependency
) : ViewModel() {

    private val _state = MutableStateFlow(TrainingState())
    val state = _state.asStateFlow()

    val actions: TrainingActions = object : TrainingActions {

        override fun onTrainingNameChange(name: String) {
            _state.update { it.copy(trainingName = name) }
        }

        override fun addExercise() {
            _state.update {
                it.copy(exercises = it.exercises + Exercise())
            }
        }

        override fun onExerciseNameChange(index: Int, name: String) {
            _state.update {
                val updatedExercises = it.exercises.toMutableList().apply {
                    this[index] = this[index].copy(name = name)
                }
                it.copy(exercises = updatedExercises)
            }
        }

        override fun onExerciseSetsChange(index: Int, sets: Int) {
            _state.update {
                val updatedExercises = it.exercises.toMutableList().apply {
                    this[index] = this[index].copy(sets = sets)
                }
                it.copy(exercises = updatedExercises)
            }
        }

        override fun onExerciseRepsChange(index: Int, reps: Int) {
            _state.update {
                val updatedExercises = it.exercises.toMutableList().apply {
                    this[index] = this[index].copy(reps = reps)
                }
                it.copy(exercises = updatedExercises)
            }
        }

        override fun onExerciseDurationChange(index: Int, duration: String) {
            _state.update {
                val updatedExercises = it.exercises.toMutableList().apply {
                    this[index] = this[index].copy(duration = duration)
                }
                it.copy(exercises = updatedExercises)
            }
        }

        override fun saveTraining(userId: String) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    val newTraining = Training(
                        titolo = _state.value.trainingName,
                        esercizi = _state.value.exercises
                    )

                    // Call the repository to save the training
                    val isSuccess = trainingRepository.createTraining(userId, newTraining)

                    if (isSuccess) {
                        // Clear the state after a successful save
                        _state.update { TrainingState(isLoading = false) }
                        // You can also add navigation logic here to go back to a previous screen
                    } else {
                        _state.update { it.copy(isLoading = false, errorMessage = "Errore durante il salvataggio") }
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            }
        }
    }
}