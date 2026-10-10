package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun HeroAqiCard(
    location: LocationItem,
    current: CurrentAirQuality,
    standard: AqiStandard = AqiStandard.US_EPA,
    drivers: AtmosphericDrivers? = null,
    onToggleSaveLocation: (LocationItem) -> Unit,
    onCycleStandard: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Resolve score readout and status based on active international standard
    val (targetIntValue, displayValue, unitLabel, level) = remember(
        standard,
        current.usAqi,
        current.europeanAqi,
        current.pm25,
        current.level
    ) {
        when (standard) {
            AqiStandard.US_EPA -> {
                val aqiInt = current.usAqi.toInt()
                Quadruple(aqiInt, aqiInt.toString(), "US AQI", current.level)
            }
            AqiStandard.EUROPEAN -> {
                val aqiInt = current.europeanAqi.toInt()
                Quadruple(aqiInt, aqiInt.toString(), "EUR AQI", AqiLevel.fromEuropeanAqi(current.europeanAqi))
            }
            AqiStandard.WHO -> {
                val ratio = current.pm25 / 15f
                val lvl = when {
                    ratio <= 1.0f -> AqiLevel.GOOD
                    ratio <= 2.0f -> AqiLevel.MODERATE
                    ratio <= 3.5f -> AqiLevel.SENSITIVE
                    else -> AqiLevel.UNHEALTHY
                }
                Quadruple(null, String.format(Locale.US, "%.1fx", ratio), "WHO LIMIT", lvl)
            }
        }
    }

    // Microinteraction: Animated numeric count-up for US and EUR standards
    val animatedAqiInt by animateIntAsState(
        targetValue = targetIntValue ?: 0,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "heroAqiCountUp"
    )

    val finalDisplayScore = if (targetIntValue != null) animatedAqiInt.toString() else displayValue

    // Microinteraction: Bookmark button bounce animation
    var bookmarkTrigger by remember { mutableStateOf(false) }
    val bookmarkScale by animateFloatAsState(
        targetValue = if (bookmarkTrigger) 1.25f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        finishedListener = { bookmarkTrigger = false },
        label = "bookmarkBounce"
    )

    // Microinteraction: Standard switch tap scale
    var standardTapTrigger by remember { mutableStateOf(false) }
    val standardTapScale by animateFloatAsState(
        targetValue = if (standardTapTrigger) 0.96f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        finishedListener = { standardTapTrigger = false },
        label = "standardTapScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurface)
            .border(1.dp, HairlineBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
            .testTag("hero_aqi_card")
    ) {
        // Top Header: City Name and Bookmark CTA
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.displayMedium.copy(
                        color = InkWhite,
                        fontSize = 23.sp,
                        letterSpacing = (-0.3).sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("hero_location_name")
                )
                if (location.country.isNotBlank()) {
                    Text(
                        text = location.country,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = InkMuted,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(
                onClick = {
                    bookmarkTrigger = true
                    onToggleSaveLocation(location)
                },
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer {
                        scaleX = bookmarkScale
                        scaleY = bookmarkScale
                    }
                    .clip(CircleShape)
                    .background(DarkElevated)
                    .testTag("toggle_save_location_button")
            ) {
                Icon(
                    imageVector = if (location.isSaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = if (location.isSaved) "Remove from saved" else "Save location",
                    tint = if (location.isSaved) EditorialBlue else InkMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Center AQI Readout with Status Pill (Tappable with microinteraction)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = standardTapScale
                        scaleY = standardTapScale
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        standardTapTrigger = true
                        onCycleStandard()
                    }
                    .padding(vertical = 4.dp, horizontal = 4.dp)
                    .testTag("hero_aqi_interactive_readout")
            ) {
                Text(
                    text = finalDisplayScore,
                    style = MaterialTheme.typography.displayLarge.copy(
                        color = InkWhite,
                        fontSize = 50.sp,
                        lineHeight = 50.sp
                    ),
                    modifier = Modifier.testTag("hero_aqi_value")
                )

                Column(
                    modifier = Modifier.padding(bottom = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = unitLabel,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = EditorialBlue,
                                fontSize = 12.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Tap to switch standard",
                            tint = InkMuted,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        text = "Tap to switch",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = InkFaint,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            // Clean status badge with breathing glow animation (draw-phase only, zero recompositions)
            HeroStatusBadge(level = level)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Clean 1-sentence health advisory
        Text(
            text = level.detailedAdvisory,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = InkMuted,
                lineHeight = 19.sp,
                fontSize = 12.sp
            )
        )

        // Integrated Environmental Intelligence Pills (Cleanest Window & Wind)
        if (drivers != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(HairlineBorder)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Best Activity Window
                drivers.cleanestWindow?.let { window ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkElevated)
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsRun,
                                contentDescription = null,
                                tint = window.aqiLevel.color,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Best Window",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = InkMuted,
                                    fontSize = 10.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "${window.startHour} - ${window.endHour}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = InkWhite,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Wind & Dispersal
                drivers.windDispersion?.let { wind ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkElevated)
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Air,
                                contentDescription = null,
                                tint = EditorialBlue,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Wind Dispersal",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = InkMuted,
                                    fontSize = 10.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = String.format(
                                Locale.US,
                                "%.0f km/h %s",
                                wind.windSpeedKmh,
                                wind.windDirectionCompass
                            ),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = InkWhite,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

@Composable
private fun HeroStatusBadge(
    level: AqiLevel,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "statusBadgeAura")
    val badgeAlpha by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.24f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badgeAuraAlpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .drawBehind {
                drawRoundRect(
                    color = level.color.copy(alpha = badgeAlpha),
                    cornerRadius = CornerRadius(10.dp.toPx())
                )
            }
            .border(1.dp, level.color.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
            .padding(horizontal = 11.dp, vertical = 6.dp)
            .testTag("hero_aqi_status_badge")
    ) {
        Text(
            text = level.label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = level.color,
                letterSpacing = 0.5.sp,
                fontSize = 12.sp
            )
        )
    }
}
