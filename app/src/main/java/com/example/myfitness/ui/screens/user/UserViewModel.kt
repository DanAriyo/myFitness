package com.example.myfitness.ui.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfitness.data.models.User
import com.example.myfitness.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log

// Stato dell'utente
data class UserState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _state = MutableStateFlow(UserState())
    val state = _state.asStateFlow()

    // Funzione per ottenere un utente dal repository dato un ID
    fun loadUser(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val user = repository.getUser(userId)
                if (user != null) {
                    _state.update { it.copy(user = user, isLoading = false) }
                    Log.d("UserViewModel", "Utente caricato: $user")
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Utente non trovato") }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Errore caricando utente: ${e.message}", e)
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    // Funzione per aggiornare i dati dell'utente
    fun updateUser(userId: String, updatedUser: User) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val success = repository.updateUser(userId, updatedUser)
                if (success) {
                    _state.update { it.copy(user = updatedUser, isLoading = false) }
                    Log.d("UserViewModel", "Utente aggiornato con successo.")
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Errore durante l'aggiornamento dell'utente."
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Errore aggiornando utente: ${e.message}", e)
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    // Funzione per dare in output l'utente corrente
    fun getUser(): User? {
        return _state.value.user
    }
}