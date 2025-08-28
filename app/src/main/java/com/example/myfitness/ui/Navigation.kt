package com.example.myfitness.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.myfitness.ui.screens.auth.AuthScreen
import com.example.myfitness.ui.screens.auth.AuthViewModel
import com.example.myfitness.ui.screens.auth.RegisterScreen
import com.example.myfitness.ui.screens.exercise.ExerciseViewModel
import com.example.myfitness.ui.screens.exercise.ExerciseScreen
import com.example.myfitness.ui.screens.home.HomeViewModel
import com.example.myfitness.ui.screens.home.HomeScreen
import com.example.myfitness.ui.screens.training.TrainingListViewModel
import com.example.myfitness.ui.screens.training.TrainingScreen
import com.example.myfitness.ui.screens.training.TrainingViewModel
import com.example.myfitness.ui.screens.training.TrainingListScreen
import com.example.myfitness.ui.screens.training.TrainingDetailScreen
import com.example.myfitness.ui.screens.training.TrainingDetailViewModel
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
    @Serializable data object TrainingList: FitnessScreen
}

fun FitnessScreen.toRoute(): String = when (this) {
    FitnessScreen.Auth -> "auth"
    FitnessScreen.Home -> "home"
    FitnessScreen.Profile -> "user"
    FitnessScreen.Training -> "train"
    FitnessScreen.Exercise -> "exercise"
    FitnessScreen.Register -> "register"
    FitnessScreen.TrainingList -> "traininglist"
}

@Composable
fun MyFitnessNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = FitnessScreen.Auth.toRoute()
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
            val authVm = koinViewModel<AuthViewModel>()
            val state by vm.state.collectAsStateWithLifecycle()
            TrainingScreen(
                state = state,
                actions = vm.actions,
                navController = navController,
                authViewModel = authVm
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

        composable("traininglist") {
            val vm = koinViewModel<TrainingListViewModel>()
            val authVm = koinViewModel<AuthViewModel>()
            val state by vm.state.collectAsStateWithLifecycle()
            TrainingListScreen(
                state = state,
                actions = vm.actions,
                navController = navController,
                authViewModel = authVm
            )
        }

        composable(
            route = "trainingDetail/{trainingId}",
            arguments = listOf(navArgument("trainingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val trainingId = backStackEntry.arguments?.getString("trainingId") ?: ""
            val authVm = koinViewModel<AuthViewModel>()
            val vm = koinViewModel<TrainingDetailViewModel>()

            TrainingDetailScreen(
                navController = navController,
                authViewModel = authVm,
                trainingId = trainingId,
                viewModel = vm
            )
        }
    }
}