package com.example.myfitness


import com.example.myfitness.data.repositories.AuthRepository
import com.example.myfitness.data.repositories.HomeRepository
import com.example.myfitness.data.repositories.TrainingRepository
import com.example.myfitness.data.repositories.UserRepository
import com.example.myfitness.ui.screens.auth.AuthViewModel
import com.example.myfitness.ui.screens.home.HomeViewModel
import com.example.myfitness.ui.screens.training.TrainingListViewModel
import com.example.myfitness.ui.screens.training.TrainingViewModel
import com.example.myfitness.ui.screens.user.UserViewModel
import org.koin.dsl.module
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.module.dsl.viewModel


val appModule = module {

    single { FirebaseAuth.getInstance() }

    single { FirebaseFirestore.getInstance() }

    single { AuthRepository(get()) }

    single{ UserRepository(get()) }

    single { TrainingRepository(get()) }

    // ViewModel
    viewModel { AuthViewModel(get(), get()) }

    viewModel { HomeViewModel() }

    viewModel{ UserViewModel(get()) }

    viewModel{ TrainingViewModel(get()) }

    viewModel { TrainingListViewModel(get()) }

}
