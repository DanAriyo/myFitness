package com.example.myfitness.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.myfitness.data.models.local.ThemeSettings

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun MyFitnessTheme(
    // ✅ Use themeSettings to control the theme dynamically
    themeSettings: ThemeSettings,
    content: @Composable () -> Unit
) {
    // 1. Determine if dark theme should be applied
    val darkTheme = when (themeSettings) {
        ThemeSettings.LIGHT -> false
        ThemeSettings.DARK -> true
        ThemeSettings.SYSTEM -> isSystemInDarkTheme()
    }

    // 2. Determine if dynamic color should be applied based on system settings
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && themeSettings == ThemeSettings.SYSTEM

    val colorScheme = when {
        dynamicColor && darkTheme -> {
            val context = LocalContext.current
            dynamicDarkColorScheme(context)
        }
        dynamicColor -> {
            val context = LocalContext.current
            dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}