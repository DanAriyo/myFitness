package com.example.myfitness.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomBar(navController: NavController) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            selected = false,
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.FitnessCenter, contentDescription = "Allenamento") },
            selected = false,
            onClick = { navController.navigate("traininglist") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profilo") },
            selected = false,
            onClick = { navController.navigate("user") }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Mappa"
                )
            },
            selected = false,
            onClick = { navController.navigate("maps") }
        )
    }
}
