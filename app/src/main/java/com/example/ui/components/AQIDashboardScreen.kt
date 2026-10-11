package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.animation.bounceClick
import com.example.ui.icons.LucideIcons
import com.example.ui.theme.*
import com.example.ui.viewmodel.AqiUiState
import java.util.Locale

/**
 * Strongly-typed pollutant card model for Apple UI.
 */
data class PollutantCardModel(
    val formula: String,
    val name: String,
    val value: Float,
    val unit: String,
    val whoLimit: Float,
    val icon: ImageVector
)

/**
 * Apple Air Quality Index Dashboard.
 * Strict Apple Weather / Health minimalism:
 * - Pure Black (#000000) canvas with System Gray 6 (#1C1C1E) cards and hairline 0.5dp borders.
 * - Monochrome typographic hierarchy with purposeful system color accents.
 * - Continuous multi-stop AQI gradient track with animated white indicator dot.
 * - 2-column pollutant card grid with clean values, status dots, and WHO benchmark bars.
 * - Full suite of Lucide hairline vector iconography.
 * - Tactile spring feedback, sliding pill physics, and native haptics throughout.
 */
@Composable
fun AQIDashboardScreen(
    uiState: AqiUiState,
    onRefresh: () -> Unit,
    onSelectAppMode: (AppMode) -> Unit,
    onSelectTimelineMode: (TimelineMode) -> Unit,
    onSelectLocation: (LocationItem) -> Unit,
    onToggleSaveLocation: (LocationItem) -> Unit,
    onRequestCurrentLocation: () -> Unit,
    onOpenSearch: () -> Unit,
    onSelectMetric: (ChartMetric) -> Unit,
    onCycleStandard: () -> Unit,
    onScrubPoint: (HourlyDataPoint?) -> Unit,
    onDismissFeedback: () -> Unit,
    modifier: Modifier = Modifier
) {
    val report = uiState.report

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppleSystemBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top Quick City Selector Bar
            CitySelectorBar(
                locations = uiState.pinnedLocations,
                activeLocation = uiState.activeLocation,
                isLocating = uiState.isLocating,
                onSelectLocation = onSelectLocation,
                onRequestCurrentLocation = onRequestCurrentLocation,
                onOpenSearch = onOpenSearch,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            // Feedback Toast if present
            AnimatedVisibility(visible = uiState.locationFeedbackMessage != null) {
                uiState.locationFeedbackMessage?.let { msg ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppleCardSecondary)
                            .border(0.5.dp, AppleBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = msg,
                            style = TypographyCaption.copy(color = AppleTextPrimary),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = LucideIcons.X,
                            contentDescription = "Dismiss notice",
                            tint = AppleTextSecondary,
                            modifier = Modifier
                                .size(16.dp)
                                .bounceClick { onDismissFeedback() }
                        )
                    }
                }
            }

            // Sync loading line
            AnimatedVisibility(visible = uiState.isLoading || uiState.isRefreshing || uiState.isLocating) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    color = AppleActionBlue,
                    trackColor = AppleSystemBackground
                )
            }

            // Main Content Area
            when {
                uiState.isLoading && report == null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = AppleActionBlue,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "Loading air quality…",
                                style = TypographyBody.copy(color = AppleTextSecondary)
                            )
                        }
                    }
                }

                uiState.errorMessage != null && report == null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(22.dp))
                                .background(AppleCardBackground)
                                .border(0.5.dp, AppleBorder, RoundedCornerShape(22.dp))
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = LucideIcons.Activity,
                                contentDescription = null,
                                tint = AppleTextSecondary,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "Unable to load data",
                                style = TypographyDisplayMd
                            )
                            Text(
                                text = uiState.errorMessage,
                                style = TypographyBody.copy(color = AppleTextSecondary)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(AppleActionBlue)
                                    .bounceClick { onRefresh() }
                                    .padding(horizontal = 24.dp, vertical = 12.dp)
                                    .testTag("retry_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Retry",
                                    style = TypographyBodyStrong.copy(color = AppleTextPrimary)
                                )
                            }
                        }
                    }
                }

                report != null -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("main_content_list"),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Hero Apple Weather AQI Card (No 3D image, pure Apple minimalism)
                        item(key = "hero_aqi_card") {
                            AppleWeatherHeroCard(
                                location = report.location,
                                current = report.current,
                                standard = uiState.aqiStandard,
                                onToggleSaveLocation = onToggleSaveLocation,
                                onCycleStandard = onCycleStandard,
                                modifier = Modifier.animateItem()
                            )
                        }

                        // 2. Health Recommendations (Apple Health Style Chips)
                        item(key = "health_recommendations_card") {
                            AppleHealthAdvisoryCard(
                                level = report.current.level,
                                modifier = Modifier.animateItem()
                            )
                        }

                        // 3. Pollutant Breakdown Grid (Clean 2-Column Cards)
                        item(key = "pollutant_parameters_grid") {
                            ApplePollutantsGridSection(
                                current = report.current,
                                modifier = Modifier.animateItem()
                            )
                        }

                        // 4. 24-Hour Forecast & Trend Chart
                        item(key = "hourly_forecast_card") {
                            TrendChart(
                                historyData = report.hourlyHistory,
                                forecastData = report.hourlyForecast,
                                timelineMode = uiState.timelineMode,
                                onSelectTimelineMode = onSelectTimelineMode,
                                selectedMetric = uiState.selectedMetric,
                                onSelectMetric = onSelectMetric,
                                scrubbedPoint = uiState.scrubbedPoint,
                                onScrubPoint = onScrubPoint,
                                modifier = Modifier.animateItem()
                            )
                        }

                        // 5. 30-Day Air Quality Retrospective
                        report.heatmapReport?.let { heatmap ->
                            item(key = "retrospective_heatmap_card") {
                                MonthlyHeatmapCard(
                                    report = heatmap,
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }

                        // 6. Botanical Allergen Tracker
                        report.allergenReport?.let { allergen ->
                            item(key = "botanical_allergen_card") {
                                PollenAllergenCard(
                                    report = allergen,
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }

                        // 7. Apple Clean Minimal Footer
                        item(key = "apple_footer") {
                            AppleMinimalFooter(
                                lastUpdated = report.current.time,
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Signature Apple Weather Hero AQI Card.
 * Clean, uncluttered layout matching the official Apple Weather Air Quality tile:
 * - Uppercase "AIR QUALITY" category label with icon.
 * - Bold City Name.
 * - Giant numeric AQI with animated count-up.
 * - Condition badge with colored status dot.
 * - Signature continuous Apple Weather spectrum gradient bar with animated thumb indicator.
 * - Minimal, plain English health statement.
 * - Standard switcher pill ("US EPA" | "EU CAQI") with animated sliding indicator.
 */
@Composable
fun AppleWeatherHeroCard(
    location: LocationItem,
    current: CurrentAirQuality,
    standard: AqiStandard,
    onToggleSaveLocation: (LocationItem) -> Unit,
    onCycleStandard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val level = current.level

    val score = when (standard) {
        AqiStandard.US_EPA -> current.usAqi.toInt()
        AqiStandard.EUROPEAN -> current.europeanAqi.toInt()
        AqiStandard.WHO -> (current.pm25 / 15f * 50f).toInt().coerceAtLeast(1)
    }

    // Smooth animated number count-up
    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "heroAqiScoreCountUp"
    )

    // Animated indicator position along gradient bar
    val targetProgress = (score.toFloat() / 250f).coerceIn(0.04f, 0.96f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "gradientThumbSpring"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(AppleCardBackground)
            .border(0.5.dp, AppleBorder, RoundedCornerShape(22.dp))
            .padding(20.dp)
            .testTag("hero_aqi_card")
    ) {
        // Top Row: Category Header & Bookmark Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = LucideIcons.Wind,
                    contentDescription = null,
                    tint = AppleTextSecondary,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "AIR QUALITY",
                    style = TypographySectionHeader
                )
            }

            // Save / Bookmark Pill
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(AppleCardSecondary)
                    .bounceClick { onToggleSaveLocation(location) }
                    .testTag("toggle_save_location_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (location.isSaved) LucideIcons.BookmarkFilled else LucideIcons.Bookmark,
                    contentDescription = if (location.isSaved) "Saved" else "Save",
                    tint = if (location.isSaved) AppleActionBlue else AppleTextSecondary,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Location Name (SF Pro Display bold)
        Text(
            text = location.name,
            style = TypographyDisplayLg.copy(
                fontSize = 28.sp,
                letterSpacing = (-0.4).sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.testTag("hero_location_name")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // AQI Score & Status Pill Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Big Numeric Readout
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.testTag("hero_aqi_interactive_readout")
            ) {
                Text(
                    text = "$animatedScore",
                    style = TypographyHeroDisplay,
                    modifier = Modifier.testTag("hero_aqi_value")
                )

                Text(
                    text = standard.shortLabel,
                    style = TypographyCaptionStrong.copy(
                        color = AppleTextSecondary,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Status Pill (e.g. Green dot + "Good")
            AppleStatusBadge(level = level)
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Signature Apple Weather Continuous Gradient AQI Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        ) {
            // Multi-stop gradient bar
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(9999.dp))
            ) {
                val gradient = Brush.horizontalGradient(
                    colors = listOf(
                        AppleGreen,    // Good (0-50)
                        AppleYellow,   // Moderate (51-100)
                        AppleOrange,   // Sensitive (101-150)
                        AppleRed,      // Unhealthy (151-200)
                        ApplePurple,   // Very Unhealthy (201-300)
                        AppleMaroon    // Hazardous (301+)
                    )
                )
                drawRect(brush = gradient)
            }

            // Floating White Indicator Thumb with Spring Physics
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .align(Alignment.Center)
            ) {
                val clampedFrac = animatedProgress.coerceIn(0.04f, 0.96f)
                val thumbX = size.width * clampedFrac
                val centerY = size.height / 2f

                // Subtle shadow halo
                drawCircle(
                    color = Color.Black.copy(alpha = 0.5f),
                    radius = 8.dp.toPx(),
                    center = Offset(thumbX, centerY)
                )
                // White circular thumb
                drawCircle(
                    color = Color.White,
                    radius = 5.5.dp.toPx(),
                    center = Offset(thumbX, centerY)
                )
            }
        }

        // Scale Markers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "0", style = TypographyFinePrint.copy(color = AppleTextTertiary))
            Text(text = "50", style = TypographyFinePrint.copy(color = AppleTextTertiary))
            Text(text = "100", style = TypographyFinePrint.copy(color = AppleTextTertiary))
            Text(text = "150", style = TypographyFinePrint.copy(color = AppleTextTertiary))
            Text(text = "200+", style = TypographyFinePrint.copy(color = AppleTextTertiary))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Minimal 1-line plain English guidance
        Text(
            text = level.detailedAdvisory,
            style = TypographyBody.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = AppleTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Standard Switcher (Animated sliding pill control)
        AppleSlidingSegmentedControl(
            items = AqiStandard.entries,
            selectedItem = standard,
            onItemSelected = { onCycleStandard() },
            height = 32.dp,
            modifier = Modifier.fillMaxWidth(),
            itemLabel = { it.shortLabel }
        )
    }
}

/**
 * Apple Status Badge with colored dot
 */
@Composable
fun AppleStatusBadge(
    level: AqiLevel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(level.color.copy(alpha = 0.16f))
            .border(0.5.dp, level.color.copy(alpha = 0.3f), RoundedCornerShape(9999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(level.color)
        )
        Text(
            text = level.label,
            style = TypographyCaptionStrong.copy(
                fontSize = 13.sp,
                color = level.color,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun AppleStatusPill(
    level: AqiLevel,
    modifier: Modifier = Modifier
) = AppleStatusBadge(level = level, modifier = modifier)

/**
 * Precautionary Health Recommendations (Apple Health Style Chips)
 */
@Composable
fun AppleHealthAdvisoryCard(
    level: AqiLevel,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val haptic = LocalHapticFeedback.current

    val advisoryOptions = remember(level) {
        listOf(
            Triple("Mask", level.maskAdvice, LucideIcons.Shield),
            Triple("Purifier", level.ventilationAdvice, LucideIcons.Wind),
            Triple("Windows", "Keep windows closed when outdoor pollution is elevated.", LucideIcons.Sparkles),
            Triple("Exercise", level.exerciseAdvice, LucideIcons.Activity),
            Triple("Sensitive", level.shortAdvice, LucideIcons.ShieldCheck)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(AppleCardBackground)
            .border(0.5.dp, AppleBorder, RoundedCornerShape(22.dp))
            .padding(20.dp)
    ) {
        // Card Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = LucideIcons.ShieldCheck,
                    contentDescription = null,
                    tint = AppleTextSecondary,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "RECOMMENDED ACTIONS",
                    style = TypographySectionHeader
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal scrolling option pills with spring bounce
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            advisoryOptions.forEachIndexed { index, (title, _, icon) ->
                val isSelected = selectedIndex == index

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(if (isSelected) AppleActionBlue else AppleCardSecondary)
                        .border(
                            0.5.dp,
                            if (isSelected) AppleActionBlue else AppleBorder,
                            RoundedCornerShape(9999.dp)
                        )
                        .bounceClick(scaleDown = 0.94f) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedIndex = index
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = TypographyCaptionStrong.copy(
                            fontSize = 11.5.sp,
                            color = if (isSelected) AppleTextPrimary else AppleTextSecondary,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Guidance Callout Box
        val activeAdvice = advisoryOptions.getOrNull(selectedIndex)?.second ?: level.detailedAdvisory
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AppleCardSecondary)
                .border(0.5.dp, AppleBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Text(
                text = activeAdvice,
                style = TypographyBody.copy(
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp,
                    color = AppleTextPrimary
                )
            )
        }
    }
}

/**
 * 2-Column Pollutant Breakdown Grid (Clean Apple Utility Cards)
 */
@Composable
fun ApplePollutantsGridSection(
    current: CurrentAirQuality,
    modifier: Modifier = Modifier
) {
    val pollutants = remember(current) {
        listOf(
            PollutantCardModel(
                formula = "PM2.5",
                name = "Fine Particles",
                value = current.pm25,
                unit = "μg/m³",
                whoLimit = 15f,
                icon = LucideIcons.Wind
            ),
            PollutantCardModel(
                formula = "PM10",
                name = "Inhalable Dust",
                value = current.pm10,
                unit = "μg/m³",
                whoLimit = 45f,
                icon = LucideIcons.Droplets
            ),
            PollutantCardModel(
                formula = "NO₂",
                name = "Nitrogen Dioxide",
                value = current.nitrogenDioxide,
                unit = "μg/m³",
                whoLimit = 25f,
                icon = LucideIcons.Activity
            ),
            PollutantCardModel(
                formula = "SO₂",
                name = "Sulfur Dioxide",
                value = current.sulphurDioxide,
                unit = "μg/m³",
                whoLimit = 40f,
                icon = LucideIcons.Sliders
            ),
            PollutantCardModel(
                formula = "CO",
                name = "Carbon Monoxide",
                value = current.carbonMonoxide,
                unit = "μg/m³",
                whoLimit = 4000f,
                icon = LucideIcons.Gauge
            ),
            PollutantCardModel(
                formula = "O₃",
                name = "Ground Ozone",
                value = current.ozone,
                unit = "μg/m³",
                whoLimit = 100f,
                icon = LucideIcons.Sparkles
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pollutant_matrix_section")
    ) {
        // Section Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = LucideIcons.Wind,
                    contentDescription = null,
                    tint = AppleTextSecondary,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "POLLUTANTS",
                    style = TypographySectionHeader
                )
            }

            Text(
                text = "WHO Benchmarks",
                style = TypographyCaption.copy(
                    fontSize = 12.sp,
                    color = AppleSkyBlue
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2-Column Grid
        val chunked = pollutants.chunked(2)
        chunked.forEach { rowCards ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowCards.forEach { cardModel ->
                    AppleIndividualPollutantCard(
                        model = cardModel,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Individual Pollutant Utility Card
 */
@Composable
fun AppleIndividualPollutantCard(
    model: PollutantCardModel,
    modifier: Modifier = Modifier
) {
    val ratio = model.value / model.whoLimit
    val isElevated = ratio > 1.0f

    val animatedValue by animateFloatAsState(
        targetValue = model.value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pollutantValueAnimation"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AppleCardBackground)
            .border(0.5.dp, AppleBorder, RoundedCornerShape(20.dp))
            .bounceClick { /* tactile bounce */ }
            .padding(16.dp)
            .testTag("pollutant_card_${model.formula}")
    ) {
        // Row 1: Icon + Formula + Status Dot
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = model.icon,
                    contentDescription = null,
                    tint = AppleTextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = model.formula,
                    style = TypographyBodyStrong.copy(
                        fontSize = 16.sp,
                        color = AppleTextPrimary
                    )
                )
            }

            // Colored status dot
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(if (isElevated) AppleOrange else AppleGreen)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Parameter Name
        Text(
            text = model.name,
            style = TypographyFinePrint.copy(
                fontSize = 11.5.sp,
                color = AppleTextSecondary
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Big Metric Value Readout
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = String.format(Locale.US, "%.1f", animatedValue),
                style = TypographyMetricValue
            )
            Text(
                text = model.unit,
                style = TypographyFinePrint.copy(
                    fontSize = 11.sp,
                    color = AppleTextSecondary
                ),
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Benchmark Bar vs WHO limit
        val fraction = (ratio / 2.0f).coerceIn(0.05f, 1.0f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(9999.dp))
                .background(AppleCardSecondary)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(9999.dp))
                    .background(if (isElevated) AppleOrange else AppleActionBlue)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Benchmark label
        Text(
            text = if (isElevated) "${String.format(Locale.US, "%.1fx", ratio)} of WHO limit" else "Within safe limit",
            style = TypographyFinePrint.copy(
                color = if (isElevated) AppleOrange else AppleTextTertiary,
                fontSize = 10.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Clean Minimal Apple Footer
 */
@Composable
fun AppleMinimalFooter(
    lastUpdated: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Air quality data by Copernicus & US EPA",
            style = TypographyFinePrint.copy(color = AppleTextSecondary, fontSize = 12.sp)
        )
        Text(
            text = "Updated $lastUpdated",
            style = TypographyFinePrint.copy(color = AppleTextTertiary, fontSize = 11.sp)
        )
    }
}

@Composable
fun AppleMuseumFooter(
    lastUpdated: String,
    modifier: Modifier = Modifier
) = AppleMinimalFooter(lastUpdated = lastUpdated, modifier = modifier)
