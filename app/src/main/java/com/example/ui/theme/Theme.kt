package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// Strict Apple Dark Theme Palette
private val AppleDarkColorScheme = darkColorScheme(
    primary = ActionBlue,
    onPrimary = OnPrimary,
    primaryContainer = ActionBlue,
    onPrimaryContainer = OnPrimary,
    secondary = SkyLinkBlue,
    onSecondary = OnPrimary,
    tertiary = ActionBlueFocus,
    onTertiary = OnPrimary,
    background = SurfaceBlack,
    onBackground = BodyOnDark,
    surface = SurfaceTile1,
    onSurface = BodyOnDark,
    surfaceVariant = SurfaceTile2,
    onSurfaceVariant = BodyMuted,
    surfaceContainerLowest = SurfaceBlack,
    surfaceContainerLow = SurfaceTile3,
    surfaceContainer = SurfaceTile1,
    surfaceContainerHigh = SurfaceTile2,
    surfaceContainerHighest = SurfaceTile2,
    outline = DarkHairline,
    outlineVariant = DarkCardBorder
)

// Apple Rounded Geometry Scale
val AppleShapes = Shapes(
    extraSmall = RoundedCornerShape(5.dp),     // {rounded.xs} 5px
    small = RoundedCornerShape(8.dp),          // {rounded.sm} 8px
    medium = RoundedCornerShape(11.dp),        // {rounded.md} 11px
    large = RoundedCornerShape(18.dp),         // {rounded.lg} 18px (utility card radius)
    extraLarge = RoundedCornerShape(9999.dp)   // {rounded.pill} 9999px (signature Apple pill)
)

val AtmosphereShapes = AppleShapes

@Composable
fun AtmosphereTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppleDarkColorScheme,
        typography = Typography,
        shapes = AppleShapes,
        content = content
    )
}
