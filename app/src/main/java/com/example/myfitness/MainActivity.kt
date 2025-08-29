package com.example.myfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.myfitness.ui.MyFitnessNavGraph
import com.example.myfitness.ui.theme.MyFitnessTheme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Usa il package name dell'applicazione come User-Agent.
        Configuration.getInstance().userAgentValue = applicationContext.packageName

        setContent {
            MyFitnessTheme {
                val navController = rememberNavController()
                MyFitnessNavGraph(navController)
            }
        }
    }
}