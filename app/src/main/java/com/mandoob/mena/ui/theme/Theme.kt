package com.mandoob.mena.ui.theme

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

// Define local dark colors
val ColorDarkGray = Color(0xFF1E293B)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7), // Sky blue 600 - elegant modern blue
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0F2FE), // Sky 100
    onPrimaryContainer = Color(0xFF0369A1), // Sky 700
    secondary = Color(0xFF004E7C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF1F5F9), // Slate 100
    background = Color(0xFFF8FAFC), // Slate 50 - extremely clean, modern background
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0F172A), // Slate 900 - professional deep slate
    onSurface = Color(0xFF0F172A), // Slate 900
    onSurfaceVariant = Color(0xFF64748B), // Slate 500 - elegant muted/disabled text
    surfaceVariant = Color(0xFFF1F5F9), // Slate 100 - beautiful card background or input box
    error = Color(0xFFD32F2F)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8), // Sky 400 - luminous soft blue
    onPrimary = Color(0xFF000000), // Pure black text over primary
    primaryContainer = Color(0xFF059669), // Emerald dark container for positive highlights
    onPrimaryContainer = Color(0xFFECFDF5),
    secondary = Color(0xFF10B981), // Beautiful emerald secondary
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF121212),
    background = Color(0xFF000000), // Pure black background for max battery savings
    surface = Color(0xFF121212), // Elegant very dark surface for containers
    onBackground = Color(0xFFF8FAFC), // Off-white text
    onSurface = Color(0xFFF8FAFC), // Off-white surface text
    onSurfaceVariant = Color(0xFF94A3B8), // Sleek slate gray text
    surfaceVariant = Color(0xFF1E1E1E), // Slightly lighter gray for nested containers
    error = Color(0xFFEF4444)
)


@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic colors by default to preserve custom branding
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
