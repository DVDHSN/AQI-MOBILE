package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Inter is the standard open-source counterpart for Apple SF Pro on Android
val InterFontFamily = FontFamily(
    Font(R.font.inter, FontWeight.Normal)
)

// =======================================================================
// Apple iOS / macOS Typography Scale
// =======================================================================

// Giant Hero AQI Value
val TypographyHeroDisplay = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 68.sp,
    lineHeight = 72.sp,
    letterSpacing = (-1.2).sp,
    color = AppleTextPrimary
)

// Large City Title
val TypographyDisplayLg = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 32.sp,
    lineHeight = 38.sp,
    letterSpacing = (-0.5).sp,
    color = AppleTextPrimary
)

// Card Title / Section Headings
val TypographyDisplayMd = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 22.sp,
    lineHeight = 28.sp,
    letterSpacing = (-0.3).sp,
    color = AppleTextPrimary
)

// Apple Card Uppercase Category Header (e.g. "AIR QUALITY", "POLLUTANTS")
val TypographySectionHeader = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 11.5.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.8.sp,
    color = AppleTextSecondary
)

// Large Metric Values (e.g. 16.8 µg/m³)
val TypographyMetricValue = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 28.sp,
    lineHeight = 32.sp,
    letterSpacing = (-0.4).sp,
    color = AppleTextPrimary
)

// Standard Body Text
val TypographyBodyStrong = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    letterSpacing = (-0.3).sp,
    color = AppleTextPrimary
)

val TypographyBody = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 22.sp,
    letterSpacing = (-0.2).sp,
    color = AppleTextSecondary
)

val TypographyTagline = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 26.sp,
    letterSpacing = (-0.3).sp,
    color = AppleTextPrimary
)

val TypographyCaption = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = (-0.1).sp,
    color = AppleTextSecondary
)

val TypographyCaptionStrong = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = (-0.1).sp,
    color = AppleTextPrimary
)

val TypographyButtonUtility = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 13.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.sp,
    color = AppleTextPrimary
)

val TypographyFinePrint = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 11.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.sp,
    color = AppleTextTertiary
)

val TypographyMicroLegal = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 10.sp,
    lineHeight = 13.sp,
    letterSpacing = 0.sp,
    color = AppleTextTertiary
)

val TypographyDenseLink = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp,
    color = AppleSkyBlue
)

val TypographyLead = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 24.sp,
    lineHeight = 30.sp,
    letterSpacing = 0.sp,
    color = AppleTextPrimary
)

val TypographyLeadAiry = TypographyLead
val TypographyButtonLarge = TypographyBodyStrong
val TypographyNavLink = TypographyButtonUtility

// Material 3 System Typography Alignment
val Typography = Typography(
    displayLarge = TypographyHeroDisplay,
    displayMedium = TypographyDisplayLg,
    displaySmall = TypographyDisplayMd,
    headlineLarge = TypographyDisplayMd,
    headlineMedium = TypographyTagline,
    headlineSmall = TypographyTagline,
    titleLarge = TypographyBodyStrong,
    titleMedium = TypographyCaptionStrong,
    titleSmall = TypographyCaptionStrong,
    bodyLarge = TypographyBody,
    bodyMedium = TypographyBody,
    bodySmall = TypographyCaption,
    labelLarge = TypographyButtonUtility,
    labelMedium = TypographyFinePrint,
    labelSmall = TypographyMicroLegal
)
