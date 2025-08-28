package com.example.myfitness.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfitness.ui.FitnessScreen
import com.example.myfitness.ui.toRoute

@Composable
fun RegisterScreen(
    state: AuthState,
    actions: AuthActions,
    navController: NavController
) {

    val snackbarHostState = remember { SnackbarHostState() }


    // Navigazione quando l'utente è registrato e loggato
    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            navController.navigate(FitnessScreen.Home.toRoute()) {
                popUpTo(FitnessScreen.Register.toRoute()) { inclusive = true }
            }
        }
    }

    // Mostra snackbar in caso di errore
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Nome
            TextField(
                value = state.firstName,
                onValueChange = actions::setFirstName,
                label = { Text("Nome", color = Color.White) },
                singleLine = true,
                trailingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) },
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Cognome
            TextField(
                value = state.lastName,
                onValueChange = actions::setLastName,
                label = { Text("Cognome", color = Color.White) },
                singleLine = true,
                trailingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) },
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Email
            TextField(
                value = state.email,
                onValueChange = actions::setEmail,
                label = { Text("Email", color = Color.White) },
                singleLine = true,
                trailingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.White) },
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Password
            TextField(
                value = state.password,
                onValueChange = actions::setPassword,
                label = { Text("Password", color = Color.White) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White) },
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Altezza
            TextField(
                value = state.height,
                onValueChange = actions::setHeight,
                label = { Text("Altezza (cm)", color = Color.White) },
                singleLine = true,
                trailingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) },
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Peso
            TextField(
                value = state.weight,
                onValueChange = actions::setWeight,
                label = { Text("Peso (kg)", color = Color.White) },
                singleLine = true,
                trailingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) },
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Bottone registrazione
            Button(
                onClick = { actions.register() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Registrati")
            }

            Spacer(Modifier.height(8.dp))

            // Vai al login
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Hai già un account? Accedi", color = Color.Green)
            }
        }
    }
}

@Composable
private fun textFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = Color.Green,
    focusedContainerColor = Color.DarkGray,
    unfocusedContainerColor = Color.DarkGray,
    focusedPlaceholderColor = Color.White,
    unfocusedPlaceholderColor = Color.White,
    focusedLeadingIconColor = Color.White,
    unfocusedLeadingIconColor = Color.White,
    focusedTrailingIconColor = Color.White,
    unfocusedTrailingIconColor = Color.White
)
