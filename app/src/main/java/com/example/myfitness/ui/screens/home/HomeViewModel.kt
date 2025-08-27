package com.example.myfitness.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Stato della Home
data class HomeState(
    val steps: Int = 0,
    val calories: Int = 0,
    val workoutOfTheDay: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)


interface HomeActions {
    fun setSteps(steps: Int)
    fun setCalories(calories: Int)
    fun setWorkoutOfTheDay(workout: String)
    fun loadDashboard() // simula caricamento dati
}

// ViewModel
class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    val actions: HomeActions = object : HomeActions {

        override fun setSteps(steps: Int) {
            _state.update { it.copy(steps = steps) }
        }

        override fun setCalories(calories: Int) {
            _state.update { it.copy(calories = calories) }
        }

        override fun setWorkoutOfTheDay(workout: String) {
            _state.update { it.copy(workoutOfTheDay = workout) }
        }

        override fun loadDashboard() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    // Qui potresti leggere i dati da un repository (Firebase, Room, ecc.)
                    // Per ora simuliamo dati finti
                    _state.update {
                        it.copy(
                            steps = 7234,
                            calories = 450,
                            workoutOfTheDay = "Allenamento cardio 15 min",
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            }
        }
    }
}
