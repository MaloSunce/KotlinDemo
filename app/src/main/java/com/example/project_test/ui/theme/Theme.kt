package com.example.project_test.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2C2B31),
    onSecondary = Color(0xFF60577A),
    secondary = Color(0xFF372F4E),
    tertiary = Color(0xFF60577A),
    background = Color(0xFF2A233F),
    surface = Color(0xFF1C1B1F),
    onBackground = Color(0xFFBDBCCC),
    primaryContainer = Color(0xFF2D283C),
    secondaryContainer = Color(0xFF332F41),
    outline = Color(0xFF403758),
    outlineVariant = Color(0xFF2D233D),
    onSurface = Color(0xFF988CB4),
)

private val LightColorScheme = lightColorScheme(
    primary =  Color(0xFFB5DF70),
    onSecondary = Color(0xFFEEBA00),
    secondary = Color(0xFFE6FFC4),
    tertiary = Color(0xFFAAC873),
    background = Color(0xFFDAF2AD),
    surface = Color(0xFFDAF2AD),
    onBackground = Color(0xFF71520A),
    primaryContainer = Color(0xFFCBEE97),
    secondaryContainer = Color(0xFFD2ECAC),
    outline = Color(0xFFBD7B0A),
    outlineVariant = Color(0xFF8E6012),
    onSurface = Color(0xFF572301),
)

@Composable
fun PLantBuddiesTheme(
    colorTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (colorTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
