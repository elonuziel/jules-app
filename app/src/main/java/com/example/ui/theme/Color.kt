package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// ================= DARK THEME PALETTE (jules.google.com Purple) =================
val DarkJulesSurface = Color(0xFF0F0C1B)
val DarkJulesSurfaceDim = Color(0xFF0F0C1B)
val DarkJulesSurfaceBright = Color(0xFF322B48)
val DarkJulesSurfaceLowest = Color(0xFF090712)
val DarkJulesSurfaceLow = Color(0xFF161223)
val DarkJulesSurfaceContainer = Color(0xFF1D182E)
val DarkJulesSurfaceHigh = Color(0xFF27213C)
val DarkJulesSurfaceHighest = Color(0xFF322B4D)

val DarkJulesOnSurface = Color(0xFFEDE8F7)
val DarkJulesOnSurfaceVariant = Color(0xFFC6BFD8)
val DarkJulesOutline = Color(0xFF8D83A6)
val DarkJulesOutlineVariant = Color(0xFF463E5E)

val DarkJulesPrimary = Color(0xFFC084FC)
val DarkJulesOnPrimary = Color(0xFF2E0854)
val DarkJulesPrimaryContainer = Color(0xFF7C3AED)
val DarkJulesOnPrimaryContainer = Color(0xFFFFFFFF)

val DarkJulesSecondary = Color(0xFF34D399)
val DarkJulesOnSecondary = Color(0xFF064E3B)
val DarkJulesSecondaryContainer = Color(0xFF059669)
val DarkJulesOnSecondaryContainer = Color(0xFFFFFFFF)

val DarkJulesTertiary = Color(0xFFE879F9)
val DarkJulesOnTertiary = Color(0xFF4A044E)
val DarkJulesTertiaryContainer = Color(0xFF9333EA)
val DarkJulesOnTertiaryContainer = Color(0xFFFFFFFF)

val DarkJulesError = Color(0xFFFFB4AB)
val DarkJulesOnError = Color(0xFF690005)
val DarkJulesErrorContainer = Color(0xFF93000A)
val DarkJulesOnErrorContainer = Color(0xFFFFDAD6)

// ================= LIGHT THEME PALETTE (jules.google.com Purple) =================
val LightJulesSurface = Color(0xFFFBF9FE)
val LightJulesSurfaceDim = Color(0xFFECE6F6)
val LightJulesSurfaceBright = Color(0xFFFFFFFF)
val LightJulesSurfaceLowest = Color(0xFFFFFFFF)
val LightJulesSurfaceLow = Color(0xFFF5F0FB)
val LightJulesSurfaceContainer = Color(0xFFFFFFFF)
val LightJulesSurfaceHigh = Color(0xFFEFE9F8)
val LightJulesSurfaceHighest = Color(0xFFE4DCFA)

val LightJulesOnSurface = Color(0xFF1E172E)
val LightJulesOnSurfaceVariant = Color(0xFF5D5470)
val LightJulesOutline = Color(0xFF7E7395)
val LightJulesOutlineVariant = Color(0xFFDAD2EB)

val LightJulesPrimary = Color(0xFF7C3AED)
val LightJulesOnPrimary = Color(0xFFFFFFFF)
val LightJulesPrimaryContainer = Color(0xFFEDE9FE)
val LightJulesOnPrimaryContainer = Color(0xFF4C1D95)

val LightJulesSecondary = Color(0xFF059669)
val LightJulesOnSecondary = Color(0xFFFFFFFF)
val LightJulesSecondaryContainer = Color(0xFFD1FAE5)
val LightJulesOnSecondaryContainer = Color(0xFF064E3B)

val LightJulesTertiary = Color(0xFF9333EA)
val LightJulesOnTertiary = Color(0xFFFFFFFF)
val LightJulesTertiaryContainer = Color(0xFFFAE8FF)
val LightJulesOnTertiaryContainer = Color(0xFF581C87)

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
    get() = if (MaterialTheme.colorScheme.surface == DarkJulesSurface) Color(0x2810B981) else Color(0x20059669)

val JulesDiffDeletionBg: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.surface == DarkJulesSurface) Color(0x35E11D48) else Color(0x20D93025)

val JulesGlowPurple: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.surface == DarkJulesSurface) Color(0x337C3AED) else Color(0x207C3AED)

// Backward compatible aliases
val JulesGlowBlue: Color
    @Composable
    @ReadOnlyComposable
    get() = JulesGlowPurple

val JulesGlowTeal: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.surface == DarkJulesSurface) Color(0x2034D399) else Color(0x18059669)


