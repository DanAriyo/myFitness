package com.example.myfitness.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfitness.ui.composables.BottomBar
import com.example.myfitness.ui.screens.auth.AuthViewModel

@Composable
fun UserScreen(
    viewModel: UserViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()

    // Carica i dati dell'utente loggato all'avvio dello screen
    LaunchedEffect(Unit) {
        val userId = authViewModel.actions.getCurrentUserId()
        if (userId.isNotEmpty()) {
            viewModel.loadUser(userId)
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
                            value = user.firstName,
                            onValueChange = {},
                            label = { Text("Nome") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )

                        OutlinedTextField(
                            value = user.lastName,
                            onValueChange = {},
                            label = { Text("Cognome") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )

                        OutlinedTextField(
                            value = if (user.weight > 0) user.weight.toString() else "",
                            onValueChange = {},
                            label = { Text("Peso (kg)") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )

                        OutlinedTextField(
                            value = if (user.height > 0) user.height.toString() else "",
                            onValueChange = {},
                            label = { Text("Altezza (cm)") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )

                        OutlinedTextField(
                            value = user.email,
                            onValueChange = {},
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )
                    }
                }
            }
        }
    }
}
