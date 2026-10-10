package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LuxuryDarkColorScheme = darkColorScheme(
    primary = InkWhite,
    onPrimary = PitchBlack,
    secondary = InkMuted,
    onSecondary = InkWhite,
    tertiary = EditorialBlue,
    background = PitchBlack,
    onBackground = InkWhite,
    surface = DarkSurface,
    onSurface = InkWhite,
    surfaceVariant = DarkElevated,
    onSurfaceVariant = InkMuted,
    outline = HairlineBorder,
    outlineVariant = HairlineBorderLight
)

// Refined modern geometry with soft card radii
val AtmosphereShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun AtmosphereTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LuxuryDarkColorScheme,
        typography = Typography,
        shapes = AtmosphereShapes,
        content = content
    )
}
