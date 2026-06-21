package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define local dark colors
val ColorDarkGray = Color(0xFF1E293B)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = SurfaceWhite,
    secondary = BlueDark,
    background = BackgroundGray,
    surface = SurfaceWhite,
    onBackground = TextDark,
    onSurface = TextDark,
    error = CancelledRed
)

private val DarkColorScheme = darkColorScheme(
    primary = BlueLight,
    onPrimary = TextDark,
    secondary = BluePrimary,
    background = TextDark,
    surface = ColorDarkGray,
    onBackground = SurfaceWhite,
    onSurface = SurfaceWhite,
    error = CancelledRed
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
