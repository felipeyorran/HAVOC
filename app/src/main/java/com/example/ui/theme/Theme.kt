package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = HavocCrimson,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3B0007),
    onPrimaryContainer = Color(0xFFFFD9DC),
    secondary = HavocRedLight,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF2A1518),
    onSecondaryContainer = Color(0xFFFFD9DC),
    tertiary = HavocGold,
    onTertiary = Color.Black,
    background = HavocDarkBackground,
    onBackground = HavocDarkTextPrimary,
    surface = HavocDarkSurface,
    onSurface = HavocDarkTextPrimary,
    surfaceVariant = HavocDarkSurfaceVariant,
    onSurfaceVariant = HavocDarkTextSecondary,
    outline = HavocDarkSurfaceBorder,
    outlineVariant = Color(0xFF24242E)
)

private val LightColorScheme = lightColorScheme(
    primary = HavocRedDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = HavocCrimson,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFECEE),
    onSecondaryContainer = Color(0xFF380005),
    tertiary = Color(0xFF8C6B00),
    onTertiary = Color.White,
    background = HavocLightBackground,
    onBackground = HavocLightTextPrimary,
    surface = HavocLightSurface,
    onSurface = HavocLightTextPrimary,
    surfaceVariant = HavocLightSurfaceVariant,
    onSurfaceVariant = HavocLightTextSecondary,
    outline = HavocLightSurfaceBorder,
    outlineVariant = Color(0xFFE2E2EA)
)

@Composable
fun HavocSensiTheme(
    darkTheme: Boolean = true, // Default to sleek esports dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
