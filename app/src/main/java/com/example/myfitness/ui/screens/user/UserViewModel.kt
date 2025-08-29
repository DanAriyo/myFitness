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
import com.example.myfitness.data.models.local.ThemeSettings
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

// Stato dell'utente
data class UserState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _state = MutableStateFlow(UserState())
    val state = _state.asStateFlow()

    private val _currentTheme = MutableStateFlow(ThemeSettings.SYSTEM)
    val currentTheme: StateFlow<ThemeSettings> = _currentTheme.asStateFlow()


    fun loadUser(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val user = repository.getUser(userId)
                if (user != null) {
                    _state.update { it.copy(user = user, isLoading = false) }

                    // ✅ MODIFICA: gestisci il caso in cui user.theme è nullo
                    _currentTheme.value = user.theme?.let {
                        ThemeSettings.valueOf(it)
                    } ?: ThemeSettings.SYSTEM // Usa un valore di default sicuro
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

    // ✅ Nuova funzione per aggiornare solo il tema
    fun updateTheme(newTheme: ThemeSettings) {
        Log.d("UserViewModel", "Ricevuta richiesta di aggiornamento tema: ${newTheme.name}")
        viewModelScope.launch {
            _state.value.user?.let { currentUser ->
                val success = repository.updateTheme(currentUser.id, newTheme)
                if (success) {
                    // Aggiorna lo stato locale del tema
                    _currentTheme.value = newTheme
                    Log.d("UserViewModel", "Tema aggiornato con successo.")
                } else {
                    Log.e("UserViewModel", "Errore nell'aggiornamento del tema su Firebase.")
                }
            }
        }
    }
}