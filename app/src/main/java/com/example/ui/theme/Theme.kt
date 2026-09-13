package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val JulesDarkColorScheme = darkColorScheme(
    primary = DarkJulesPrimary,
    onPrimary = DarkJulesOnPrimary,
    primaryContainer = DarkJulesPrimaryContainer,
    onPrimaryContainer = DarkJulesOnPrimaryContainer,
    secondary = DarkJulesSecondary,
    onSecondary = DarkJulesOnSecondary,
    secondaryContainer = DarkJulesSecondaryContainer,
    onSecondaryContainer = DarkJulesOnSecondaryContainer,
    tertiary = DarkJulesTertiary,
    onTertiary = DarkJulesOnTertiary,
    tertiaryContainer = DarkJulesTertiaryContainer,
    onTertiaryContainer = DarkJulesOnTertiaryContainer,
    error = DarkJulesError,
    onError = DarkJulesOnError,
    errorContainer = DarkJulesErrorContainer,
    onErrorContainer = DarkJulesOnErrorContainer,
    background = DarkJulesSurface,
    onBackground = DarkJulesOnSurface,
    surface = DarkJulesSurface,
    onSurface = DarkJulesOnSurface,
    surfaceVariant = DarkJulesSurfaceHighest,
    onSurfaceVariant = DarkJulesOnSurfaceVariant,
    outline = DarkJulesOutline,
    outlineVariant = DarkJulesOutlineVariant,
    surfaceContainerLowest = DarkJulesSurfaceLowest,
    surfaceContainerLow = DarkJulesSurfaceLow,
    surfaceContainer = DarkJulesSurfaceContainer,
    surfaceContainerHigh = DarkJulesSurfaceHigh,
    surfaceContainerHighest = DarkJulesSurfaceHighest
)

val JulesLightColorScheme = lightColorScheme(
    primary = LightJulesPrimary,
    onPrimary = LightJulesOnPrimary,
    primaryContainer = LightJulesPrimaryContainer,
    onPrimaryContainer = LightJulesOnPrimaryContainer,
    secondary = LightJulesSecondary,
    onSecondary = LightJulesOnSecondary,
    secondaryContainer = LightJulesSecondaryContainer,
    onSecondaryContainer = LightJulesOnSecondaryContainer,
    tertiary = LightJulesTertiary,
    onTertiary = LightJulesOnTertiary,
    tertiaryContainer = LightJulesTertiaryContainer,
    onTertiaryContainer = LightJulesOnTertiaryContainer,
    error = LightJulesError,
    onError = LightJulesOnError,
    errorContainer = LightJulesErrorContainer,
    onErrorContainer = LightJulesOnErrorContainer,
    background = LightJulesSurface,
    onBackground = LightJulesOnSurface,
    surface = LightJulesSurface,
    onSurface = LightJulesOnSurface,
    surfaceVariant = LightJulesSurfaceHighest,
    onSurfaceVariant = LightJulesOnSurfaceVariant,
    outline = LightJulesOutline,
    outlineVariant = LightJulesOutlineVariant,
    surfaceContainerLowest = LightJulesSurfaceLowest,
    surfaceContainerLow = LightJulesSurfaceLow,
    surfaceContainer = LightJulesSurfaceContainer,
    surfaceContainerHigh = LightJulesSurfaceHigh,
    surfaceContainerHighest = LightJulesSurfaceHighest
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) JulesDarkColorScheme else JulesLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.surfaceContainerLowest.toArgb()
                window.navigationBarColor = colorScheme.surfaceContainerLowest.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


