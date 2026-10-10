package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// Strict Wired Dark Editorial Color Scheme
private val WiredDarkColorScheme = darkColorScheme(
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

// Sharp editorial square geometry (0.dp border radius)
val WiredShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small = RoundedCornerShape(0.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp)
)

@Composable
fun AtmosphereTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WiredDarkColorScheme,
        typography = Typography,
        shapes = WiredShapes,
        content = content
    )
}
