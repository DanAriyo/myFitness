package com.example.myfitness.ui.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfitness.data.models.User
import com.example.myfitness.data.repositories.AuthRepository
import com.example.myfitness.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthState(
    val email: String = "",
    val password: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val height: String = "",
    val weight: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)

interface AuthActions {
    fun setEmail(email: String)
    fun setPassword(password: String)
    fun setFirstName(firstName: String)
    fun setLastName(lastName: String)
    fun setHeight(height: String)
    fun setWeight(weight: String)
    fun login()
    fun register()
    fun logout()
    fun getCurrentUserId() : String
}

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    val actions: AuthActions = object : AuthActions {
        override fun setEmail(email: String) =
            _state.update { it.copy(email = email) }

        override fun setPassword(password: String) =
            _state.update { it.copy(password = password) }

        override fun setFirstName(firstName: String) =
            _state.update { it.copy(firstName = firstName) }

        override fun setLastName(lastName: String) =
            _state.update { it.copy(lastName = lastName) }

        override fun setHeight(height: String) =
            _state.update { it.copy(height = height) }

        override fun setWeight(weight: String) =
            _state.update { it.copy(weight = weight) }

        override fun login() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                val success = authRepository.login(
                    _state.value.email,
                    _state.value.password
                )
                _state.update {
                    it.copy(
                        isLoggedIn = success,
                        isLoading = false,
                        errorMessage = if (!success) "Login fallito" else null
                    )
                }
            }
        }

        override fun register() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }

                val success = authRepository.register(
                    _state.value.email,
                    _state.value.password
                )

                val userid = authRepository.getLastCreatedUserId();
                if (success) {
                    val user = User(
                        id = userid,
                        firstName = _state.value.firstName,
                        lastName = _state.value.lastName,
                        height = _state.value.height.toIntOrNull() ?: 0,
                        weight = _state.value.weight.toIntOrNull() ?: 0,
                        email = _state.value.email
                    )


                    val firestoreSuccess = userRepository.createUser(user)
                    Log.d("AuthViewModel", "firestoreSuccess = $firestoreSuccess")

                    if (firestoreSuccess) {
                        _state.update { it.copy(isLoggedIn = true, isLoading = false) }
                    } else {
                        _state.update {
                            it.copy(
                                isLoggedIn = false,
                                isLoading = false,
                                errorMessage = "Registrazione su Firestore fallita"
                            )
                        }
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoggedIn = false,
                            isLoading = false,
                            errorMessage = "Registrazione fallita"
                        )
                    }
                }
            }
        }


        override fun logout() {
            authRepository.logout()
            _state.update { it.copy(isLoggedIn = false) }
        }

        override fun getCurrentUserId(): String {
            return authRepository.getCurrentUserId()
        }
    }
}
