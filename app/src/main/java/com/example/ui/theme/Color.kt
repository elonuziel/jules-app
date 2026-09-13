package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// ================= DARK THEME PALETTE =================
val DarkJulesSurface = Color(0xFF10141A)
val DarkJulesSurfaceDim = Color(0xFF10141A)
val DarkJulesSurfaceBright = Color(0xFF353940)
val DarkJulesSurfaceLowest = Color(0xFF0A0E14)
val DarkJulesSurfaceLow = Color(0xFF181C22)
val DarkJulesSurfaceContainer = Color(0xFF1C2026)
val DarkJulesSurfaceHigh = Color(0xFF262A31)
val DarkJulesSurfaceHighest = Color(0xFF31353C)

val DarkJulesOnSurface = Color(0xFFDFE2EB)
val DarkJulesOnSurfaceVariant = Color(0xFFC1C6D6)
val DarkJulesOutline = Color(0xFF8B909F)
val DarkJulesOutlineVariant = Color(0xFF414754)

val DarkJulesPrimary = Color(0xFFADC7FF)
val DarkJulesOnPrimary = Color(0xFF002E68)
val DarkJulesPrimaryContainer = Color(0xFF1A73E8)
val DarkJulesOnPrimaryContainer = Color(0xFFFFFFFF)

val DarkJulesSecondary = Color(0xFF4EDEA3)
val DarkJulesOnSecondary = Color(0xFF003824)
val DarkJulesSecondaryContainer = Color(0xFF00A572)
val DarkJulesOnSecondaryContainer = Color(0xFF00311F)

val DarkJulesTertiary = Color(0xFFD0BCFF)
val DarkJulesOnTertiary = Color(0xFF3C0091)
val DarkJulesTertiaryContainer = Color(0xFF8657F1)
val DarkJulesOnTertiaryContainer = Color(0xFFFFFFFF)

val DarkJulesError = Color(0xFFFFB4AB)
val DarkJulesOnError = Color(0xFF690005)
val DarkJulesErrorContainer = Color(0xFF93000A)
val DarkJulesOnErrorContainer = Color(0xFFFFDAD6)

// ================= LIGHT THEME PALETTE =================
val LightJulesSurface = Color(0xFFF8F9FA)
val LightJulesSurfaceDim = Color(0xFFE8EAED)
val LightJulesSurfaceBright = Color(0xFFFFFFFF)
val LightJulesSurfaceLowest = Color(0xFFFFFFFF)
val LightJulesSurfaceLow = Color(0xFFF1F3F4)
val LightJulesSurfaceContainer = Color(0xFFFFFFFF)
val LightJulesSurfaceHigh = Color(0xFFE8EAED)
val LightJulesSurfaceHighest = Color(0xFFDFE1E5)

val LightJulesOnSurface = Color(0xFF202124)
val LightJulesOnSurfaceVariant = Color(0xFF5F6368)
val LightJulesOutline = Color(0xFF70757A)
val LightJulesOutlineVariant = Color(0xFFDADCE0)

val LightJulesPrimary = Color(0xFF1A73E8)
val LightJulesOnPrimary = Color(0xFFFFFFFF)
val LightJulesPrimaryContainer = Color(0xFFD2E3FC)
val LightJulesOnPrimaryContainer = Color(0xFF174EA6)

val LightJulesSecondary = Color(0xFF137333)
val LightJulesOnSecondary = Color(0xFFFFFFFF)
val LightJulesSecondaryContainer = Color(0xFFCEEAD6)
val LightJulesOnSecondaryContainer = Color(0xFF0D652D)

val LightJulesTertiary = Color(0xFF8430CE)
val LightJulesOnTertiary = Color(0xFFFFFFFF)
val LightJulesTertiaryContainer = Color(0xFFF3E8FD)
val LightJulesOnTertiaryContainer = Color(0xFF5A1A9A)

val LightJulesError = Color(0xFFD93025)
val LightJulesOnError = Color(0xFFFFFFFF)
val LightJulesErrorContainer = Color(0xFFFCE8E6)
val LightJulesOnErrorContainer = Color(0xFFC5221F)

// ================= DYNAMIC TOKENS RESOLVED VIA MATERIALTHEME =================
val JulesSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surface

val JulesSurfaceLowest: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceContainerLowest

val JulesSurfaceLow: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceContainerLow

val JulesSurfaceContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceContainer

val JulesSurfaceHigh: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceContainerHigh

val JulesSurfaceHighest: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceContainerHighest

val JulesSurfaceContainerHigh: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceContainerHigh

val JulesSurfaceContainerHighest: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceContainerHighest

val JulesOnSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurface

val JulesOnSurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val JulesOutline: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outline

val JulesOutlineVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outlineVariant

val JulesPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primary

val JulesOnPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onPrimary

val JulesPrimaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primaryContainer

val JulesOnPrimaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onPrimaryContainer

val JulesSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.secondary

val JulesOnSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSecondary

val JulesSecondaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.secondaryContainer

val JulesOnSecondaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSecondaryContainer

val JulesTertiary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.tertiary

val JulesOnTertiary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onTertiary

val JulesTertiaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.tertiaryContainer

val JulesOnTertiaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onTertiaryContainer

val JulesError: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.error

val JulesOnError: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onError

val JulesErrorContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.errorContainer

val JulesOnErrorContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onErrorContainer

// Code Diff specific
val JulesDiffAdditionBg: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.surface == DarkJulesSurface) Color(0x2800A572) else Color(0x22137333)

val JulesDiffDeletionBg: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.surface == DarkJulesSurface) Color(0x3593000A) else Color(0x20D93025)

val JulesGlowBlue: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.surface == DarkJulesSurface) Color(0x1AADC7FF) else Color(0x181A73E8)

val JulesGlowTeal: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.surface == DarkJulesSurface) Color(0x1A4EDEA3) else Color(0x18137333)


