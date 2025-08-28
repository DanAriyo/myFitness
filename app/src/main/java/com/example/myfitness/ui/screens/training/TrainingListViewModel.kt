package com.example.myfitness.ui.screens.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfitness.data.models.Training
import com.example.myfitness.data.repositories.TrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- STATO ---
data class TrainingListState(
    val trainings: List<Training> = emptyList(),
    val selectedTraining: Training? = null, // ✅ Nuovo stato per il singolo allenamento
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// --- AZIONI ---
interface TrainingListActions {
    fun loadTrainings(userId: String)
    fun loadTrainingById(userId: String, trainingId: String) // ✅ Nuova azione
}

// --- VIEWMODEL ---
class TrainingListViewModel(
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TrainingListState())
    val state = _state.asStateFlow()

    val actions: TrainingListActions = object : TrainingListActions {
        override fun loadTrainings(userId: String) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    val trainings = trainingRepository.getAllTrainingsForUser(userId)
                    _state.update { it.copy(trainings = trainings, isLoading = false) }
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false, errorMessage = "Impossibile caricare gli allenamenti.") }
                }
            }
        }

        override fun loadTrainingById(userId: String, trainingId: String) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null, selectedTraining = null) }
                try {
                    val training = trainingRepository.getTrainingById(userId, trainingId)
                    if (training != null) {
                        _state.update { it.copy(selectedTraining = training, isLoading = false) }
                    } else {
                        _state.update { it.copy(isLoading = false, errorMessage = "Allenamento non trovato.") }
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false, errorMessage = "Errore durante il caricamento dell'allenamento.") }
                }
            }
        }
    }
}