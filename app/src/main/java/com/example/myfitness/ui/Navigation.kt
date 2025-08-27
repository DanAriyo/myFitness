package com.example.myfitness.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myfitness.data.repositories.AuthRepository
import com.example.myfitness.ui.screens.auth.AuthScreen
import com.example.myfitness.ui.screens.auth.AuthViewModel
import com.example.myfitness.ui.screens.auth.RegisterScreen
import com.example.myfitness.ui.screens.exercise.ExerciseViewModel
import com.example.myfitness.ui.screens.exercise.ExerciseScreen
import com.example.myfitness.ui.screens.home.HomeViewModel
import com.example.myfitness.ui.screens.home.HomeScreen
import com.example.myfitness.ui.screens.training.TrainingScreen
import com.example.myfitness.ui.screens.training.TrainingViewModel
import com.example.myfitness.ui.screens.user.UserViewModel
import com.example.myfitness.ui.screens.user.UserScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel


sealed interface FitnessScreen {
    @Serializable data object Auth : FitnessScreen
    @Serializable data object Home : FitnessScreen
    @Serializable data object Profile: FitnessScreen
    @Serializable data object Training: FitnessScreen
    @Serializable data object Exercise: FitnessScreen
    @Serializable data object Register: FitnessScreen

}

// Funzione di estensione per ottenere la route come stringa
fun FitnessScreen.toRoute(): String = when (this) {
    FitnessScreen.Auth -> "auth"
    FitnessScreen.Home -> "home"
    FitnessScreen.Profile -> "user"
    FitnessScreen.Training -> "train"
    FitnessScreen.Exercise -> "exercise"
    FitnessScreen.Register -> "register"



}

@Composable
fun MyFitnessNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = FitnessScreen.Auth.toRoute() // usa la stringa
    ) {

        composable("auth") {
            val vm = koinViewModel<AuthViewModel>()
            val state by vm.state.collectAsStateWithLifecycle()
            AuthScreen(
                state = state,
                actions = vm.actions,
                navController = navController
            )
        }

        composable("home") {
            val vm = koinViewModel<HomeViewModel>()
            val state by vm.state.collectAsStateWithLifecycle()
            HomeScreen(
                state = state,
                actions = vm.actions,
                navController = navController
            )
        }

        composable("user") {
            val vm = koinViewModel<UserViewModel>()
            val authVm = koinViewModel<AuthViewModel>()
            UserScreen(
                viewModel = vm,
                authViewModel = authVm,
                navController = navController
            )
        }


        composable("train") {
            val vm = koinViewModel<TrainingViewModel>()
            val state by vm.state.collectAsStateWithLifecycle()
            TrainingScreen(
                state = state,
                actions = vm.actions,
                navController = navController,
                userId = "12"
            )
        }

        composable("exercise") {
            val vm = koinViewModel<ExerciseViewModel>()
            val state by vm.state.collectAsStateWithLifecycle()
            ExerciseScreen(
                state = state,
                actions = vm.actions,
                navController = navController,
                trainingId = "1"
            )
        }

        composable("register") {
            val vm = koinViewModel<AuthViewModel>()
            val state by vm.state.collectAsStateWithLifecycle()
            RegisterScreen(
                state = state,
                actions = vm.actions,
                navController = navController
            )
        }




    }
}
