package com.aura.app.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════
// Extended theme properties beyond Material3
// ═══════════════════════════════════════════════════
data class AuraExtendedColors(
    val xpGradientStart: Color,
    val xpGradientEnd: Color,
    val cardGlow: Color,
    val accentGlow: Color,
    val skillEmpathy: Color = AuraColors.SkillEmpathy,
    val skillConfidence: Color = AuraColors.SkillConfidence,
    val skillCommunication: Color = AuraColors.SkillCommunication,
    val skillLeadership: Color = AuraColors.SkillLeadership,
    val skillResilience: Color = AuraColors.SkillResilience,
)

val LocalAuraColors = staticCompositionLocalOf {
    AuraExtendedColors(
        xpGradientStart = Color.Cyan,
        xpGradientEnd = Color.Magenta,
        cardGlow = Color.Transparent,
        accentGlow = Color.Transparent,
    )
}

val LocalAgeGroup = staticCompositionLocalOf { "adults" }

// ═══════════════════════════════════════════════════
// Material3 ColorSchemes
// ═══════════════════════════════════════════════════
private val KidsColorScheme = lightColorScheme(
    primary = KidsColors.Primary,
    secondary = KidsColors.Secondary,
    tertiary = KidsColors.Tertiary,
    background = KidsColors.Background,
    surface = KidsColors.Surface,
    surfaceVariant = KidsColors.SurfaceVariant,
    onBackground = KidsColors.OnBackground,
    onSurface = KidsColors.OnSurface,
    onPrimary = KidsColors.OnPrimary,
    error = KidsColors.Error,
)

private val TeensColorScheme = darkColorScheme(
    primary = TeensColors.Primary,
    secondary = TeensColors.Secondary,
    tertiary = TeensColors.Tertiary,
    background = TeensColors.Background,
    surface = TeensColors.Surface,
    surfaceVariant = TeensColors.SurfaceVariant,
    onBackground = TeensColors.OnBackground,
    onSurface = TeensColors.OnSurface,
    onPrimary = TeensColors.OnPrimary,
    error = TeensColors.Error,
)

private val AdultsColorScheme = darkColorScheme(
    primary = AdultsColors.Primary,
    secondary = AdultsColors.Secondary,
    tertiary = AdultsColors.Tertiary,
    background = AdultsColors.Background,
    surface = AdultsColors.Surface,
    surfaceVariant = AdultsColors.SurfaceVariant,
    onBackground = AdultsColors.OnBackground,
    onSurface = AdultsColors.OnSurface,
    onPrimary = AdultsColors.OnPrimary,
    error = AdultsColors.Error,
)

// ═══════════════════════════════════════════════════
// Extended color sets
// ═══════════════════════════════════════════════════
private val KidsExtendedColors = AuraExtendedColors(
    xpGradientStart = KidsColors.XPGradientStart,
    xpGradientEnd = KidsColors.XPGradientEnd,
    cardGlow = KidsColors.CardGlow,
    accentGlow = KidsColors.AccentGlow,
)

private val TeensExtendedColors = AuraExtendedColors(
    xpGradientStart = TeensColors.XPGradientStart,
    xpGradientEnd = TeensColors.XPGradientEnd,
    cardGlow = TeensColors.CardGlow,
    accentGlow = TeensColors.AccentGlow,
)

private val AdultsExtendedColors = AuraExtendedColors(
    xpGradientStart = AdultsColors.XPGradientStart,
    xpGradientEnd = AdultsColors.XPGradientEnd,
    cardGlow = AdultsColors.CardGlow,
    accentGlow = AdultsColors.AccentGlow,
)

// ═══════════════════════════════════════════════════
// Theme composable
// ═══════════════════════════════════════════════════
@Composable
fun AuraTheme(
    ageGroup: String = "adults",
    content: @Composable () -> Unit
) {
    val colorScheme = when (ageGroup) {
        "kids" -> KidsColorScheme
        "teens" -> TeensColorScheme
        else -> AdultsColorScheme
    }

    val extendedColors = when (ageGroup) {
        "kids" -> KidsExtendedColors
        "teens" -> TeensExtendedColors
        else -> AdultsExtendedColors
    }

    val typography = when (ageGroup) {
        "kids" -> kidsTypography()
        "teens" -> teensTypography()
        else -> adultsTypography()
    }

    val shapes = when (ageGroup) {
        "kids" -> kidsShapes()
        "teens" -> teensShapes()
        else -> adultsShapes()
    }

    // Animate color transitions when theme switches
    val animatedColorScheme = colorScheme.copy(
        primary = animateColorAsState(colorScheme.primary, tween(600)).value,
        secondary = animateColorAsState(colorScheme.secondary, tween(600)).value,
        tertiary = animateColorAsState(colorScheme.tertiary, tween(600)).value,
        background = animateColorAsState(colorScheme.background, tween(600)).value,
        surface = animateColorAsState(colorScheme.surface, tween(600)).value,
        onBackground = animateColorAsState(colorScheme.onBackground, tween(600)).value,
        onSurface = animateColorAsState(colorScheme.onSurface, tween(600)).value,
    )

    CompositionLocalProvider(
        LocalAuraColors provides extendedColors,
        LocalAgeGroup provides ageGroup,
    ) {
        MaterialTheme(
            colorScheme = animatedColorScheme,
            typography = typography,
            shapes = shapes,
            content = content,
        )
    }
}

// ═══════════════════════════════════════════════════
// Theme accessor extension
// ═══════════════════════════════════════════════════
object AuraTheme {
    val extendedColors: AuraExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAuraColors.current

    val ageGroup: String
        @Composable
        @ReadOnlyComposable
        get() = LocalAgeGroup.current
}

// Helper to animate ColorScheme copy
private fun ColorScheme.copy(
    primary: Color = this.primary,
    secondary: Color = this.secondary,
    tertiary: Color = this.tertiary,
    background: Color = this.background,
    surface: Color = this.surface,
    onBackground: Color = this.onBackground,
    onSurface: Color = this.onSurface,
): ColorScheme = this.copy(
    primary = primary,
    onPrimary = this.onPrimary,
    primaryContainer = this.primaryContainer,
    onPrimaryContainer = this.onPrimaryContainer,
    inversePrimary = this.inversePrimary,
    secondary = secondary,
    onSecondary = this.onSecondary,
    secondaryContainer = this.secondaryContainer,
    onSecondaryContainer = this.onSecondaryContainer,
    tertiary = tertiary,
    onTertiary = this.onTertiary,
    tertiaryContainer = this.tertiaryContainer,
    onTertiaryContainer = this.onTertiaryContainer,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = this.surfaceVariant,
    onSurfaceVariant = this.onSurfaceVariant,
    surfaceTint = this.surfaceTint,
    inverseSurface = this.inverseSurface,
    inverseOnSurface = this.inverseOnSurface,
    error = this.error,
    onError = this.onError,
    errorContainer = this.errorContainer,
    onErrorContainer = this.onErrorContainer,
    outline = this.outline,
    outlineVariant = this.outlineVariant,
    scrim = this.scrim,
)
