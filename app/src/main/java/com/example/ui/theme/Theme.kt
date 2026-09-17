package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CinemaColorScheme = darkColorScheme(
    primary = CinemaRed,
    onPrimary = Color.White,
    primaryContainer = CinemaRedDark,
    onPrimaryContainer = Color.White,
    secondary = CinemaGold,
    onSecondary = Color.Black,
    secondaryContainer = CinemaSurfaceLightDark,
    onSecondaryContainer = CinemaGold,
    tertiary = CinemaAccentBlue,
    onTertiary = Color.Black,
    background = CinemaBackgroundDark,
    onBackground = CinemaTextPrimary,
    surface = CinemaSurfaceDark,
    onSurface = CinemaTextPrimary,
    surfaceVariant = CinemaSurfaceLightDark,
    onSurfaceVariant = CinemaTextSecondary,
    outline = CinemaBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CinemaColorScheme,
        typography = Typography,
        content = content
    )
}
