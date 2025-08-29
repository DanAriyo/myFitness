package com.example.myfitness

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.myfitness.ui.MyFitnessNavGraph
import com.example.myfitness.ui.theme.MyFitnessTheme
import com.example.myfitness.ui.screens.user.UserViewModel
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // ✅ Added for edge-to-edge display

        // Usa il package name dell'applicazione come User-Agent.
        Configuration.getInstance().userAgentValue = applicationContext.packageName

        setContent {
            // ✅ Ottieni il ViewModel e raccogli lo stato del tema
            val userViewModel: UserViewModel = koinViewModel()
            val themeSettings by userViewModel.currentTheme.collectAsState()
            Log.d("MainActivity", "Tema corrente ricevuto dal ViewModel: ${themeSettings.name}")
            // ✅ Passa lo stato del tema al tuo Composable del tema
            MyFitnessTheme(themeSettings = themeSettings) {
                val navController = rememberNavController()
                MyFitnessNavGraph(navController)
            }
        }
    }
}