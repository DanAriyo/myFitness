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
import android.util.Log
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

// --- STATO ---
data class TrainingDetailState(
    val training: Training? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val trainingTitle: String = "",
    val calories: String = "",
    val date: LocalDate? = null,
    val isSaving: Boolean = false,
    val exercises: List<Exercise> = emptyList() // ✅ Aggiunto il campo esercizi per la UI
)

// --- AZIONI ---
interface TrainingDetailActions {
    fun loadTraining(userId: String, trainingId: String)
    fun updateTrainingExercises(userId: String, trainingId: String, newExercises: List<Exercise>)
    fun addExercise()
    fun onTitleChange(newTitle: String)
    fun onCaloriesChange(calories: String)
    fun onDateChange(date: LocalDate)
    fun saveChanges(userId: String, trainingId: String)
    // ✅ Nuove azioni per gestire le modifiche agli esercizi
    fun onExerciseNameChange(index: Int, name: String)
    fun onExerciseSetsChange(index: Int, sets: Int)
    fun onExerciseRepsChange(index: Int, reps: Int)
    fun onExerciseDurationChange(index: Int, duration: String)
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
                        _state.update {
                            it.copy(
                                training = training,
                                isLoading = false,
                                trainingTitle = training.titolo,
                                calories = training.calorie.toString(),
                                date = training.data.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                                exercises = training.esercizi // ✅ Inizializza la lista di esercizi nello stato
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(isLoading = false, errorMessage = "Allenamento non trovato.")
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

        // Metodo per aggiornare l'intera lista di esercizi (utilizzato solo se si passa una lista completa)
        override fun updateTrainingExercises(userId: String, trainingId: String, newExercises: List<Exercise>) {
            viewModelScope.launch {
                _state.update { it.copy(isSaving = true, errorMessage = null) }
                try {
                    val success = repository.updateTrainingExercises(userId, trainingId, newExercises)
                    if (success) {
                        loadTraining(userId, trainingId)
                    } else {
                        _state.update {
                            it.copy(
                                isSaving = false,
                                errorMessage = "Errore durante l'aggiornamento degli esercizi."
                            )
                        }
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = "Errore durante l'aggiornamento degli esercizi: ${e.message}"
                        )
                    }
                }
            }
        }

        override fun addExercise() {
            _state.update {
                val updatedExercises = it.exercises.plus(Exercise()) // ✅ Aggiunge un nuovo esercizio alla lista
                it.copy(exercises = updatedExercises)
            }
        }

        override fun onTitleChange(newTitle: String) {
            _state.update { it.copy(trainingTitle = newTitle) }
        }

        override fun onCaloriesChange(calories: String) {
            _state.update { it.copy(calories = calories) }
        }

        override fun onDateChange(date: LocalDate) {
            _state.update { it.copy(date = date) }
        }

        // ✅ Implementazioni delle azioni per i singoli esercizi
        override fun onExerciseNameChange(index: Int, name: String) {
            _state.update { currentState ->
                val updatedExercises = currentState.exercises.toMutableList()
                if (index in updatedExercises.indices) {
                    updatedExercises[index] = updatedExercises[index].copy(name = name)
                }
                currentState.copy(exercises = updatedExercises)
            }
        }

        override fun onExerciseSetsChange(index: Int, sets: Int) {
            _state.update { currentState ->
                val updatedExercises = currentState.exercises.toMutableList()
                if (index in updatedExercises.indices) {
                    updatedExercises[index] = updatedExercises[index].copy(sets = sets)
                }
                currentState.copy(exercises = updatedExercises)
            }
        }

        override fun onExerciseRepsChange(index: Int, reps: Int) {
            _state.update { currentState ->
                val updatedExercises = currentState.exercises.toMutableList()
                if (index in updatedExercises.indices) {
                    updatedExercises[index] = updatedExercises[index].copy(reps = reps)
                }
                currentState.copy(exercises = updatedExercises)
            }
        }

        override fun onExerciseDurationChange(index: Int, duration: String) {
            _state.update { currentState ->
                val updatedExercises = currentState.exercises.toMutableList()
                if (index in updatedExercises.indices) {
                    updatedExercises[index] = updatedExercises[index].copy(duration = duration)
                }
                currentState.copy(exercises = updatedExercises)
            }
        }

        override fun saveChanges(userId: String, trainingId: String) {
            viewModelScope.launch {
                _state.update { it.copy(isSaving = true, errorMessage = null) }

                try {
                    val currentTraining = _state.value.training

                    if (currentTraining == null) {
                        _state.update { it.copy(isSaving = false, errorMessage = "Nessun allenamento da salvare.") }
                        return@launch
                    }

                    val caloriesInt = _state.value.calories.toIntOrNull()
                    if (caloriesInt == null) {
                        _state.update { it.copy(isSaving = false, errorMessage = "Inserisci un valore valido per le calorie.") }
                        return@launch
                    }

                    val dateTimestamp = _state.value.date?.let {
                        Timestamp(Date.from(it.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    } ?: currentTraining.data

                    val updatedTraining = currentTraining.copy(
                        titolo = _state.value.trainingTitle,
                        calorie = caloriesInt,
                        data = dateTimestamp,
                        esercizi = _state.value.exercises // ✅ Includi la lista aggiornata di esercizi nel modello da salvare
                    )

                    val isSuccess = repository.updateTraining(userId, trainingId, updatedTraining)

                    if (isSuccess) {
                        loadTraining(userId, trainingId)
                    } else {
                        _state.update { it.copy(isSaving = false, errorMessage = "Errore durante il salvataggio delle modifiche.") }
                    }

                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = "Errore durante il salvataggio: ${e.message}"
                        )
                    }
                }
            }
        }
    }
}