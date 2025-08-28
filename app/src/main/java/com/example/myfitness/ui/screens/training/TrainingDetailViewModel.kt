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

// --- STATO ---
data class TrainingDetailState(
    val training: Training? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

// --- AZIONI ---
interface TrainingDetailActions {
    fun loadTraining(userId: String, trainingId: String)
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
    }
}