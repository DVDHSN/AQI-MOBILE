package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =======================================================================
// Apple iOS / macOS Dark Mode Design Tokens
// =======================================================================

// Canvas & Surfaces (Apple System Dark)
val AppleSystemBackground = Color(0xFF000000)  // Pure Black Canvas
val AppleCardBackground = Color(0xFF1C1C1E)    // iOS System Gray 6 Dark (Base Card)
val AppleCardSecondary = Color(0xFF2C2C2E)     // iOS System Gray 5 Dark (Nested Cards / Inputs)
val AppleCardTertiary = Color(0xFF3A3A3C)      // iOS System Gray 4 Dark (Pills / Dividers)

// Hairlines & Borders
val AppleBorder = Color(0x22FFFFFF)            // 0.5dp subtle white hairline border
val AppleBorderFocus = Color(0xFF0A84FF)       // Focused border

// Monochrome Typography
val AppleTextPrimary = Color(0xFFFFFFFF)        // 100% White (Headlines, large values)
val AppleTextSecondary = Color(0xFF8E8E93)      // System Gray (Labels, subtitles, uppercase headers)
val AppleTextTertiary = Color(0xFF636366)       // Dim System Gray (Footers, fine print)

// Apple System Accents
val AppleActionBlue = Color(0xFF007AFF)        // Primary iOS Blue
val AppleSkyBlue = Color(0xFF0A84FF)           // Link Blue on dark
val AppleGreen = Color(0xFF30D158)             // Good AQI (0-50)
val AppleYellow = Color(0xFFFFD60A)            // Moderate AQI (51-100)
val AppleOrange = Color(0xFFFF9F0A)            // Sensitive Groups (101-150)
val AppleRed = Color(0xFFFF453A)               // Unhealthy (151-200)
val ApplePurple = Color(0xFFBF5AF2)            // Very Unhealthy (201-300)
val AppleMaroon = Color(0xFFFF375F)            // Hazardous (301+)

// Backward-compatibility aliases for existing design references
val ActionBlue = AppleActionBlue
val ActionBlueFocus = AppleSkyBlue
val SkyLinkBlue = AppleSkyBlue
val SurfaceBlack = AppleSystemBackground
val SurfaceTile1 = AppleCardBackground
val SurfaceTile2 = AppleCardSecondary
val SurfaceTile3 = AppleCardTertiary
val BodyOnDark = AppleTextPrimary
val BodyMuted = AppleTextSecondary
val FinePrint = AppleTextTertiary
val DarkHairline = AppleBorder
val PitchBlack = AppleSystemBackground
val DarkSurface = AppleCardBackground
val DarkElevated = AppleCardSecondary
val HairlineBorder = AppleBorder
val HairlineBorderLight = AppleBorder
val InkWhite = AppleTextPrimary
val InkMuted = AppleTextSecondary
val InkFaint = AppleTextTertiary
val EditorialBlue = AppleActionBlue
val OnPrimary = AppleTextPrimary
val DarkCardBorder = Color.Transparent

val AqiGood = AppleGreen
val AqiModerate = AppleYellow
val AqiSensitive = AppleOrange
val AqiUnhealthy = AppleRed
val AqiVeryUnhealthy = ApplePurple
val AqiHazardous = AppleMaroon
