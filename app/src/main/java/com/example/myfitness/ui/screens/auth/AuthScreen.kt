package com.example.myfitness.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfitness.ui.FitnessScreen
import com.example.myfitness.ui.toRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    state: AuthState,
    actions: AuthActions,
    navController: NavController
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Navigazione quando l'utente è loggato
    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            navController.navigate(FitnessScreen.Home.toRoute()) {
                popUpTo(FitnessScreen.Auth.toRoute()) { inclusive = true }
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
            TextField(
                value = state.email,
                onValueChange = actions::setEmail,
                label = { Text("Email", color = Color.White) },
                singleLine = true,
                trailingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.White) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.Green,
                    focusedContainerColor = Color.DarkGray,
                    unfocusedContainerColor = Color.DarkGray,
                    focusedPlaceholderColor = Color.White,
                    unfocusedPlaceholderColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            TextField(
                value = state.password,
                onValueChange = actions::setPassword,
                label = { Text("Password", color = Color.White) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.Green,
                    focusedContainerColor = Color.DarkGray,
                    unfocusedContainerColor = Color.DarkGray,
                    focusedPlaceholderColor = Color.White,
                    unfocusedPlaceholderColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bottone Login
                Button(
                    onClick = { actions.login() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Login")
                }

                // Bottone Registrati → naviga a RegisterScreen
                Button(
                    onClick = { navController.navigate(FitnessScreen.Register.toRoute()) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Registrati")
                }
            }

            if (state.isLoading) {
                Spacer(Modifier.height(16.dp))
                CircularProgressIndicator(color = Color.Green)
            }
        }
    }
}
