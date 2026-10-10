package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AirQualityReport
import com.example.data.model.ChartMetric
import com.example.data.model.LocationItem
import com.example.ui.theme.*
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max

@Composable
fun CityDuelScreen(
    availableLocations: List<LocationItem>,
    cityA: LocationItem,
    cityB: LocationItem,
    reportA: AirQualityReport?,
    reportB: AirQualityReport?,
    isLoadingA: Boolean,
    isLoadingB: Boolean,
    onSelectCityA: (LocationItem) -> Unit,
    onSelectCityB: (LocationItem) -> Unit,
    selectedMetric: ChartMetric,
    onSelectMetric: (ChartMetric) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .padding(14.dp)
            .testTag("city_duel_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. City Selectors Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurface)
                    .border(1.dp, HairlineBorder, RoundedCornerShape(20.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Compare Cities",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = InkWhite,
                        fontSize = 15.sp
                    )
                )

                // City A Selector
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = "First City: ${cityA.name}",
                        style = MaterialTheme.typography.labelSmall.copy(color = EditorialBlue, fontSize = 11.sp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(availableLocations, key = { "a_${it.id}" }) { loc ->
                            val isSel = loc.id == cityA.id
                            val chipScale by animateFloatAsState(
                                targetValue = if (isSel) 1.03f else 1.0f,
                                label = "duelChipA_${loc.id}"
                            )
                            Box(
                                modifier = Modifier
                                    .graphicsLayer {
                                        scaleX = chipScale
                                        scaleY = chipScale
                                    }
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) EditorialBlue else DarkElevated)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { onSelectCityA(loc) }
                                    .padding(horizontal = 9.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = loc.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSel) PitchBlack else InkMuted,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // City B Selector
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = "Second City: ${cityB.name}",
                        style = MaterialTheme.typography.labelSmall.copy(color = AqiModerate, fontSize = 11.sp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(availableLocations, key = { "b_${it.id}" }) { loc ->
                            val isSel = loc.id == cityB.id
                            val chipScale by animateFloatAsState(
                                targetValue = if (isSel) 1.03f else 1.0f,
                                label = "duelChipB_${loc.id}"
                            )
                            Box(
                                modifier = Modifier
                                    .graphicsLayer {
                                        scaleX = chipScale
                                        scaleY = chipScale
                                    }
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) AqiModerate else DarkElevated)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { onSelectCityB(loc) }
                                    .padding(horizontal = 9.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = loc.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSel) PitchBlack else InkMuted,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Head-to-Head Hero Comparison Card
        item {
            val aqiA = reportA?.current?.usAqi?.toInt()
            val aqiB = reportB?.current?.usAqi?.toInt()

            val animatedAqiA by animateIntAsState(
                targetValue = aqiA ?: 0,
                animationSpec = tween(450, easing = FastOutSlowInEasing),
                label = "animatedAqiA"
            )
            val animatedAqiB by animateIntAsState(
                targetValue = aqiB ?: 0,
                animationSpec = tween(450, easing = FastOutSlowInEasing),
                label = "animatedAqiB"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurface)
                    .border(1.dp, HairlineBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Winner summary banner
                if (aqiA != null && aqiB != null) {
                    val delta = abs(aqiA - aqiB)
                    val winnerText = when {
                        aqiA < aqiB -> "${cityA.name} is $delta pts cleaner than ${cityB.name}"
                        aqiB < aqiA -> "${cityB.name} is $delta pts cleaner than ${cityA.name}"
                        else -> "Both cities have equivalent air quality"
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkElevated)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = winnerText,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = InkWhite,
                                fontSize = 11.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Two Columns: City A and City B
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // City A Column
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = cityA.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = InkWhite,
                                fontSize = 14.sp
                            ),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (isLoadingA) {
                            CircularProgressIndicator(
                                color = EditorialBlue,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else if (reportA != null) {
                            Text(
                                text = animatedAqiA.toString(),
                                style = MaterialTheme.typography.displayMedium.copy(
                                    color = EditorialBlue,
                                    fontSize = 36.sp
                                )
                            )
                            Text(
                                text = reportA.current.level.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = reportA.current.level.color,
                                    fontSize = 10.sp
                                )
                            )
                        } else {
                            Text(text = "—", style = MaterialTheme.typography.titleLarge.copy(color = InkMuted))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(50.dp)
                            .background(HairlineBorder)
                    )

                    // City B Column
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = cityB.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = InkWhite,
                                fontSize = 14.sp
                            ),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (isLoadingB) {
                            CircularProgressIndicator(
                                color = AqiModerate,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else if (reportB != null) {
                            Text(
                                text = animatedAqiB.toString(),
                                style = MaterialTheme.typography.displayMedium.copy(
                                    color = AqiModerate,
                                    fontSize = 36.sp
                                )
                            )
                            Text(
                                text = reportB.current.level.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = reportB.current.level.color,
                                    fontSize = 10.sp
                                )
                            )
                        } else {
                            Text(text = "—", style = MaterialTheme.typography.titleLarge.copy(color = InkMuted))
                        }
                    }
                }
            }
        }

        // 3. Side-by-Side Pollutant Breakdown with animated comparison bars
        if (reportA != null && reportB != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurface)
                        .border(1.dp, HairlineBorder, RoundedCornerShape(20.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Detailed Comparison",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = InkWhite,
                            fontSize = 14.sp
                        )
                    )

                    ComparisonMetricRow(
                        metric = "PM2.5",
                        unit = "μg/m³",
                        valA = reportA.current.pm25,
                        valB = reportB.current.pm25
                    )
                    ComparisonMetricRow(
                        metric = "PM10",
                        unit = "μg/m³",
                        valA = reportA.current.pm10,
                        valB = reportB.current.pm10
                    )
                    ComparisonMetricRow(
                        metric = "Ozone (O₃)",
                        unit = "μg/m³",
                        valA = reportA.current.ozone,
                        valB = reportB.current.ozone
                    )
                    ComparisonMetricRow(
                        metric = "NO₂",
                        unit = "μg/m³",
                        valA = reportA.current.nitrogenDioxide,
                        valB = reportB.current.nitrogenDioxide
                    )
                }
            }
        }
    }
}

@Composable
private fun ComparisonMetricRow(
    metric: String,
    unit: String,
    valA: Float,
    valB: Float
) {
    val total = max(valA + valB, 0.001f)
    val ratioA = valA / total
    val ratioB = valB / total

    val animatedRatioA by animateFloatAsState(
        targetValue = ratioA,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "duelRatioA_$metric"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkElevated)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = String.format(Locale.US, "%.1f", valA),
                style = MaterialTheme.typography.titleSmall.copy(
                    color = if (valA <= valB) EditorialBlue else InkMuted,
                    fontSize = 12.sp
                )
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = metric,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = InkWhite,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = unit,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = InkFaint,
                        fontSize = 8.sp
                    )
                )
            }

            Text(
                text = String.format(Locale.US, "%.1f", valB),
                style = MaterialTheme.typography.titleSmall.copy(
                    color = if (valB <= valA) AqiModerate else InkMuted,
                    fontSize = 12.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Proportional dual bar indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(PitchBlack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(animatedRatioA.coerceAtLeast(0.01f))
                    .background(EditorialBlue)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight((1f - animatedRatioA).coerceAtLeast(0.01f))
                    .background(AqiModerate)
            )
        }
    }
}
