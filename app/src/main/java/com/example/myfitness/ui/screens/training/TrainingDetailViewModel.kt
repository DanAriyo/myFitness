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
import android.util.Log // Ensure this is imported

// --- STATO ---
data class TrainingDetailState(
    val training: Training? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

// --- AZIONI ---
interface TrainingDetailActions {
    fun loadTraining(userId: String, trainingId: String)
    fun updateTrainingExercises(userId: String, trainingId: String, newExercises: List<Exercise>)
    fun addExercise() // ✅ New action
}

// --- VIEWMODEL ---
class TrainingDetailViewModel(private val repository: TrainingRepository) : ViewModel() {

    private val _state = MutableStateFlow(TrainingDetailState())
    val state = _state.asStateFlow()

    val actions: TrainingDetailActions = object : TrainingDetailActions {
        override fun loadTraining(userId: String, trainingId: String) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    val training = repository.getTrainingById(userId, trainingId)
                    if (training != null) {
                        _state.update { it.copy(training = training, isLoading = false) }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Allenamento non trovato."
                            )
                        }
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Errore durante il caricamento dell'allenamento: ${e.message}"
                        )
                    }
                }
            }
        }

        override fun updateTrainingExercises(userId: String, trainingId: String, newExercises: List<Exercise>) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    val success = repository.updateTrainingExercises(userId, trainingId, newExercises)
                    if (success) {
                        loadTraining(userId, trainingId)
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Errore durante l'aggiornamento degli esercizi."
                            )
                        }
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Errore durante l'aggiornamento degli esercizi: ${e.message}"
                        )
                    }
                }
            }
        }

        override fun addExercise() {
            _state.update {
                val updatedExercises = it.training?.esercizi?.plus(Exercise()) ?: listOf(Exercise())
                it.copy(
                    training = it.training?.copy(esercizi = updatedExercises),
                    // Set a temp flag or update logic to reflect the change on the UI
                    // A better approach is to manage the editable list on the UI itself
                )
            }
        }
    }
}