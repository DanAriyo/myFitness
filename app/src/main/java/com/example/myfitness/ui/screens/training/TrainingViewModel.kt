package com.example.myfitness.ui.screens.training


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- MODEL ---
data class Training(
    val id: String = "",
    val titolo: String = "",
    val descrizione: String = "",
    val categoria: String = "",
    val eserciziIds: List<String> = emptyList() //
)

// --- STATE ---
data class TrainingState(
    val allenamenti: List<Training> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// --- ACTIONS ---
interface TrainingActions {
    fun loadTrainings(userId: String)
    fun addTraining(training: Training)
    fun removeTraining(trainingId: String)
    fun updateTraining(training: Training)
}

// --- VIEWMODEL ---
class TrainingViewModel : ViewModel() {

    private val _state = MutableStateFlow(TrainingState())
    val state = _state.asStateFlow()

    val actions: TrainingActions = object : TrainingActions {

        override fun loadTrainings(userId: String) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    // 🔹 MOCK: simuliamo dati finti
                    val fakeTrainings = listOf(
                        Training(
                            id = "1",
                            titolo = "Allenamento cardio",
                            descrizione = "15 minuti di corsa leggera",
                            categoria = "Cardio",
                            eserciziIds = listOf("e1") // esercizi da ExerciseViewModel
                        ),
                        Training(
                            id = "2",
                            titolo = "Allenamento forza",
                            descrizione = "Pesi e resistenza",
                            categoria = "Forza",
                            eserciziIds = listOf("e2", "e3")
                        )
                    )

                    _state.update { it.copy(allenamenti = fakeTrainings, isLoading = false) }

                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            }
        }

        override fun addTraining(training: Training) {
            _state.update { it.copy(allenamenti = it.allenamenti + training) }
        }

        override fun removeTraining(trainingId: String) {
            _state.update { it.copy(allenamenti = it.allenamenti.filterNot { tr -> tr.id == trainingId }) }
        }

        override fun updateTraining(training: Training) {
            _state.update {
                it.copy(
                    allenamenti = it.allenamenti.map { tr ->
                        if (tr.id == training.id) training else tr
                    }
                )
            }
        }
    }
}
