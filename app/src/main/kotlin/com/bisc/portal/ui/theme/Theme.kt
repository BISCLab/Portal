package com.bisc.portal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

enum class ThemePreference { DARK, LIGHT, SYSTEM }

private val DarkColors = darkColorScheme(
    background        = DarkBackground,
    surface           = DarkSurface,
    surfaceVariant    = DarkSurfaceVar,
    onBackground      = DarkOnBg,
    onSurface         = DarkOnBg,
    onSurfaceVariant  = DarkSubtle,
    primary           = NeonRed,
    onPrimary         = PearlWhite,
    primaryContainer  = DarkPrimaryContainer,
    onPrimaryContainer = PearlWhite,
    secondary         = DarkSubtle,
    onSecondary       = PearlWhite,
    outline           = DarkSubtle.copy(alpha = 0.45f),
    outlineVariant    = DarkSubtle.copy(alpha = 0.2f),
)

private val LightColors = lightColorScheme(
    background        = LightBackground,
    surface           = LightSurface,
    surfaceVariant    = LightSurfaceVar,
    onBackground      = LightOnBg,
    onSurface         = LightOnBg,
    onSurfaceVariant  = LightSubtle,
    primary           = NeonRed,
    onPrimary         = PearlWhite,
    primaryContainer  = LightPrimaryContainer,
    onPrimaryContainer = NightBlue,
    secondary         = LightSubtle,
    onSecondary       = PearlWhite,
    outline           = LightSubtle.copy(alpha = 0.5f),
    outlineVariant    = LightSubtle.copy(alpha = 0.25f),
)

@Composable
fun PortalTheme(
    themePreference: ThemePreference = ThemePreference.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themePreference) {
        ThemePreference.DARK   -> true
        ThemePreference.LIGHT  -> false
        ThemePreference.SYSTEM -> systemDark
    }
    MaterialTheme(
        colorScheme = if (isDark) DarkColors else LightColors,
        typography  = PortalTypography,
        content     = content
    )
}
