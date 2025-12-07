package com.bharatkrishi.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BharatKrishiLightGreen,
    onPrimary = Color.Black,
    secondary = BharatKrishiOrange,
    onSecondary = Color.Black,
    tertiary = BharatKrishiYellow,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BharatKrishiGreen,
    onPrimary = Color.White,
    secondary = BharatKrishiOrange,
    onSecondary = Color.White,
    tertiary = BharatKrishiYellow,
    onTertiary = Color.Black,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun BharatKrishiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}