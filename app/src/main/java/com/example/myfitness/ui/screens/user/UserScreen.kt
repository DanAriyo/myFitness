package com.example.myfitness.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfitness.data.models.User
import com.example.myfitness.ui.composables.BottomBar
import com.example.myfitness.ui.screens.auth.AuthViewModel

@Composable
fun UserScreen(
    viewModel: UserViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    var isEditing by remember { mutableStateOf(false) }

    // Dati temporanei per la modifica
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Carica i dati dell'utente loggato all'avvio dello screen
    LaunchedEffect(state.user) {
        val userId = authViewModel.actions.getCurrentUserId()
        if (userId.isNotEmpty()) {
            viewModel.loadUser(userId)
        }
        // Aggiorna i campi quando i dati dell'utente cambiano
        state.user?.let { user ->
            firstName = user.firstName
            lastName = user.lastName
            weight = if (user.weight > 0) user.weight.toString() else ""
            height = if (user.height > 0) user.height.toString() else ""
            email = user.email
        }
    }

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Profilo Utente",
                style = MaterialTheme.typography.headlineSmall
            )

            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            state.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            state.user?.let { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { if (isEditing) firstName = it },
                            label = { Text("Nome") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = !isEditing
                        )

                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { if (isEditing) lastName = it },
                            label = { Text("Cognome") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = !isEditing
                        )

                        OutlinedTextField(
                            value = weight,
                            onValueChange = { if (isEditing) weight = it },
                            label = { Text("Peso (kg)") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = !isEditing
                        )

                        OutlinedTextField(
                            value = height,
                            onValueChange = { if (isEditing) height = it },
                            label = { Text("Altezza (cm)") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = !isEditing
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { }, // L'email non dovrebbe essere modificabile
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )
                    }
                }
            }

            // Bottoni di gestione
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { isEditing = true },
                    modifier = Modifier.weight(1f),
                    enabled = !isEditing // Il bottone "Modifica" è attivo solo se non stiamo modificando
                ) {
                    Text("Modifica Dati")
                }

                Button(
                    onClick = {
                        val userId = authViewModel.actions.getCurrentUserId()
                        if (userId.isNotEmpty()) {
                            val updatedUser = User(
                                id = userId,
                                firstName = firstName,
                                lastName = lastName,
                                height = height.toIntOrNull() ?: 0,
                                weight = weight.toIntOrNull() ?: 0,
                                email = email
                            )
                            viewModel.updateUser(userId, updatedUser)
                        }
                        isEditing = false
                    },
                    modifier = Modifier.weight(1f),
                    enabled = isEditing // Il bottone "Salva" è attivo solo se stiamo modificando
                ) {
                    Text("Salva Dati")
                }
            }
        }
    }
}