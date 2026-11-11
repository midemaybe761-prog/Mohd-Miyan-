package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF381E72),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = NeonCyan,
    onSecondary = Color(0xFF00363F),
    secondaryContainer = Color(0xFF004E5C),
    onSecondaryContainer = Color(0xFFA5EEFF),
    tertiary = NeonPink,
    onTertiary = Color.White,
    background = StudioDarkBg,
    onBackground = TextPrimary,
    surface = StudioCardBg,
    onSurface = TextPrimary,
    surfaceVariant = StudioElevated,
    onSurfaceVariant = TextSecondary,
    outline = StudioCardBorder,
    outlineVariant = Color(0xFF2D375B)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

