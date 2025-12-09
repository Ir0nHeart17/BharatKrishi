package com.bharatkrishi.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkOrangeAccent,
    onPrimary = Color.Black, // Text on Orange
    secondary = DarkSurfaceCard, // Cards
    onSecondary = DarkTextMain,
    tertiary = DarkOrangeAccent,
    background = DarkGreyBackground,
    onBackground = DarkTextMain,
    surface = DarkSurfaceCard,
    onSurface = DarkTextMain,
    surfaceVariant = Color(0xFF333333),
    onSurfaceVariant = DarkTextSecondary,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = UserOrange,
    onPrimary = Color.White,
    secondary = UserBeigeSecondary, // Cards/Secondary elements
    onSecondary = UserDarkText,
    tertiary = UserOrange,
    background = UserBeigeMain, // Main Background
    onBackground = UserDarkText,

    surface = Color.White, // Keeping cards white for crispness against the beige background
    onSurface = UserDarkText,
    surfaceVariant = UserBeigeSecondary, // Used for distinct cards
    onSurfaceVariant = UserDarkText,
    error = ErrorRed
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