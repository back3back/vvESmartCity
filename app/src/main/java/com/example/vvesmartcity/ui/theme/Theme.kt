package com.example.vvesmartcity.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = SmartCityLightBlue,
    secondary = SmartCityBlue,
    tertiary = SmartCityGreen,
    background = SmartCityOnBackground,
    surface = SmartCitySurface,
    onPrimary = SmartCityDarkBlue,
    onBackground = SmartCityBackground,
    onSurface = SmartCityOnBackground
)

private val LightColorScheme = lightColorScheme(
    primary = SmartCityBlue,
    secondary = SmartCityDarkBlue,
    tertiary = SmartCityGreen,
    background = SmartCityBackground,
    surface = SmartCitySurface,
    onPrimary = Color.White,
    onBackground = SmartCityOnBackground,
    onSurface = SmartCityOnBackground
)

@Composable
fun VvESmartCityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
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
