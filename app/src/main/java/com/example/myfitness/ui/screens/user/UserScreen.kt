package com.example.myfitness.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfitness.data.models.User
import com.example.myfitness.ui.composables.BottomBar
import com.example.myfitness.ui.composables.TopBar
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

    LaunchedEffect(state.user) {
        val userId = authViewModel.actions.getCurrentUserId()
        if (userId.isNotEmpty()) {
            viewModel.loadUser(userId)
        }
        state.user?.let { user ->
            firstName = user.firstName
            lastName = user.lastName
            weight = if (user.weight > 0) user.weight.toString() else ""
            height = if (user.height > 0) user.height.toString() else ""
            email = user.email
        }
    }

    Scaffold(
        topBar = { TopBar(title = "Profilo Utente") }, // ✅ Aggiungi la tua TopBar
        bottomBar = { BottomBar(navController) }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Centra orizzontalmente i contenuti
        ) {
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
                            readOnly = !isEditing,
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) } // ✅ Icona
                        )

                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { if (isEditing) lastName = it },
                            label = { Text("Cognome") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = !isEditing,
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) } // ✅ Icona
                        )

                        OutlinedTextField(
                            value = weight,
                            onValueChange = { if (isEditing) weight = it },
                            label = { Text("Peso (kg)") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = !isEditing,
                            leadingIcon = { Icon(Icons.Default.Scale, contentDescription = null) } // ✅ Icona
                        )

                        OutlinedTextField(
                            value = height,
                            onValueChange = { if (isEditing) height = it },
                            label = { Text("Altezza (cm)") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = !isEditing,
                            leadingIcon = { Icon(Icons.Default.Height, contentDescription = null) } // ✅ Icona
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) } // ✅ Icona
                        )
                    }
                }
            }

            // Bottoni di gestione
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // ✅ Bottone "Modifica" più bello
                FilledTonalButton(
                    onClick = { isEditing = true },
                    modifier = Modifier.weight(1f),
                    enabled = !isEditing
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Modifica")
                    Spacer(Modifier.width(8.dp))
                    Text("Modifica")
                }

                // ✅ Bottone "Salva" più bello
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
                    enabled = isEditing
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Salva")
                    Spacer(Modifier.width(8.dp))
                    Text("Salva")
                }
            }
        }
    }
}