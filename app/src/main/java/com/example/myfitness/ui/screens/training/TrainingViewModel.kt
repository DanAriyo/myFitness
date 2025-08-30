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
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date


data class TrainingState(
    val trainingName: String = "",
    val exercises: List<Exercise> = emptyList(),
    // ✅ Lo stato della UI continua a usare LocalDate per semplicità
    val calories: String = "",
    val date: LocalDate = LocalDate.now(),
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
    fun onCaloriesChange(calories: String)
    fun onDateChange(date: LocalDate)
    fun saveTraining(userId: String)
}

// --- VIEWMODEL ---
class TrainingViewModel(
    private val trainingRepository: TrainingRepository
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

        override fun onCaloriesChange(calories: String) {
            _state.update { it.copy(calories = calories) }
        }

        override fun onDateChange(date: LocalDate) {
            _state.update { it.copy(date = date) }
        }

        override fun saveTraining(userId: String) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    val caloriesInt = _state.value.calories.toIntOrNull()
                    if (caloriesInt == null) {
                        _state.update { it.copy(isLoading = false, errorMessage = "Inserisci un valore valido per le calorie.") }
                        return@launch
                    }

                    // ✅ Converti LocalDate in Timestamp prima di creare l'oggetto Training
                    val dateTimestamp = Timestamp(
                        Date.from(_state.value.date.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    )

                    val newTraining = Training(
                        titolo = _state.value.trainingName,
                        esercizi = _state.value.exercises,
                        calorie = caloriesInt,
                        data = dateTimestamp // ✅ Usa il Timestamp convertito qui
                    )

                    val isSuccess = trainingRepository.createTraining(userId, newTraining)

                    if (isSuccess) {
                        _state.update { TrainingState(isLoading = false) }
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