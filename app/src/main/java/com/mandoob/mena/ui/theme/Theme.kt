package com.mandoob.mena.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
    onPrimary = Color(0xFF082F49), // Deep sky dark
    primaryContainer = Color(0xFF0284C7), // Sky 600
    onPrimaryContainer = Color(0xFFE0F2FE), // Sky 100
    secondary = Color(0xFFE5F1FC),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF1E293B), // Slate 800
    background = Color(0xFF0F172A), // Slate 900 - deep slate navy night background
    surface = Color(0xFF1E293B), // Slate 800 - elegant card surface
    onBackground = Color(0xFFF8FAFC), // Slate 50 - soft readable white
    onSurface = Color(0xFFF8FAFC), // Slate 50
    onSurfaceVariant = Color(0xFF94A3B8), // Slate 400 - nice muted text
    surfaceVariant = Color(0xFF334155), // Slate 700 - container background
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
