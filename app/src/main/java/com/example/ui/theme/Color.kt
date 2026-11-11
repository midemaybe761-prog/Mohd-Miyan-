package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val StudioDarkBg = Color(0xFF090C15)
val StudioCardBg = Color(0xFF12172A)
val StudioCardBorder = Color(0xFF222C4E)
val StudioElevated = Color(0xFF182038)

val NeonPurple = Color(0xFF8A55FE)
val NeonCyan = Color(0xFF00D4FF)
val NeonPink = Color(0xFFFF2A85)
val NeonAmber = Color(0xFFFFB020)
val NeonGreen = Color(0xFF10E79D)

val TextPrimary = Color(0xFFF1F5F9)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val PurpleGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF8A55FE), Color(0xFF00D4FF))
)

val GlowGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF8A55FE), Color(0xFFFF2A85))
)

val DarkSurfaceGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF161D36), Color(0xFF0C101F))
)
