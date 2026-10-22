package com.streak.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        fun fromName(value: String?): ThemeMode = when (value) {
            "LIGHT" -> LIGHT
            "DARK" -> DARK
            else -> SYSTEM
        }
    }
}

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = IndigoOnPrimary,
    primaryContainer = IndigoContainer,
    onPrimaryContainer = IndigoOnContainer,
    secondary = TealSecondary,
    onSecondary = IndigoOnPrimary,
    secondaryContainer = TealContainer,
    onSecondaryContainer = TealContainerDark,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = Color(0xFFE5E1EC),
    onSurfaceVariant = OnSurfaceVariantLight,
    error = ErrorLight,
    onError = IndigoOnPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimaryDark,
    onPrimary = IndigoOnPrimaryDark,
    primaryContainer = IndigoContainerDark,
    onPrimaryContainer = IndigoOnContainerDark,
    secondary = TealSecondaryDark,
    onSecondary = TealContainerDark,
    secondaryContainer = TealContainerDark,
    onSecondaryContainer = TealContainer,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = Color(0xFF47464F),
    onSurfaceVariant = OnSurfaceVariantDark,
    error = ErrorDark,
    onError = Color(0xFF690005)
)

val StreakTypography = Typography()

@Composable
fun StreakTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val useDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    MaterialTheme(
        colorScheme = if (useDark) DarkColorScheme else LightColorScheme,
        typography = StreakTypography,
        content = content
    )
}
