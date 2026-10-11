package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AirQualityReport
import com.example.data.model.ChartMetric
import com.example.data.model.LocationItem
import com.example.ui.animation.bounceClick
import com.example.ui.icons.LucideIcons
import com.example.ui.theme.*
import java.util.Locale
import kotlin.math.abs

/**
 * City Duel Comparison Screen redesigned with Apple minimalism.
 * Pure Black window with System Gray 6 cards, 0.5dp borders, and spring physics.
 */
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
            .background(AppleSystemBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("city_duel_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. City Selectors Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(AppleCardBackground)
                    .border(0.5.dp, AppleBorder, RoundedCornerShape(22.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = LucideIcons.Compare,
                        contentDescription = null,
                        tint = AppleTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "COMPARE CITIES",
                        style = TypographySectionHeader
                    )
                }

                // City A Selector Row
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "City 1: ${cityA.name}",
                        style = TypographyCaptionStrong.copy(fontSize = 12.sp, color = AppleSkyBlue)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(availableLocations, key = { "a_${it.id}" }) { loc ->
                            val isSel = loc.id == cityA.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(if (isSel) AppleActionBlue else AppleCardSecondary)
                                    .border(
                                        0.5.dp,
                                        if (isSel) AppleActionBlue else AppleBorder,
                                        RoundedCornerShape(9999.dp)
                                    )
                                    .bounceClick(scaleDown = 0.94f) { onSelectCityA(loc) }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = loc.name,
                                    style = TypographyCaptionStrong.copy(
                                        color = if (isSel) AppleTextPrimary else AppleTextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }

                // City B Selector Row
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "City 2: ${cityB.name}",
                        style = TypographyCaptionStrong.copy(fontSize = 12.sp, color = AppleSkyBlue)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(availableLocations, key = { "b_${it.id}" }) { loc ->
                            val isSel = loc.id == cityB.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(if (isSel) AppleActionBlue else AppleCardSecondary)
                                    .border(
                                        0.5.dp,
                                        if (isSel) AppleActionBlue else AppleBorder,
                                        RoundedCornerShape(9999.dp)
                                    )
                                    .bounceClick(scaleDown = 0.94f) { onSelectCityB(loc) }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = loc.name,
                                    style = TypographyCaptionStrong.copy(
                                        color = if (isSel) AppleTextPrimary else AppleTextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Head-to-Head Comparison Card
        item {
            val aqiA = reportA?.current?.usAqi?.toInt()
            val aqiB = reportB?.current?.usAqi?.toInt()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(AppleCardBackground)
                    .border(0.5.dp, AppleBorder, RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "AIR QUALITY INDEX",
                    style = TypographySectionHeader
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // City A Score Box
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AppleCardSecondary)
                            .border(0.5.dp, AppleBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = cityA.name,
                            style = TypographyBodyStrong.copy(fontSize = 15.sp),
                            maxLines = 1
                        )
                        if (isLoadingA) {
                            CircularProgressIndicator(
                                color = AppleActionBlue,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(28.dp)
                            )
                        } else if (aqiA != null) {
                            Text(
                                text = aqiA.toString(),
                                style = TypographyHeroDisplay.copy(fontSize = 42.sp)
                            )
                            reportA?.current?.level?.let { AppleStatusBadge(level = it) }
                        } else {
                            Text(text = "—", style = TypographyDisplayLg)
                        }
                    }

                    // City B Score Box
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AppleCardSecondary)
                            .border(0.5.dp, AppleBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = cityB.name,
                            style = TypographyBodyStrong.copy(fontSize = 15.sp),
                            maxLines = 1
                        )
                        if (isLoadingB) {
                            CircularProgressIndicator(
                                color = AppleActionBlue,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(28.dp)
                            )
                        } else if (aqiB != null) {
                            Text(
                                text = aqiB.toString(),
                                style = TypographyHeroDisplay.copy(fontSize = 42.sp)
                            )
                            reportB?.current?.level?.let { AppleStatusBadge(level = it) }
                        } else {
                            Text(text = "—", style = TypographyDisplayLg)
                        }
                    }
                }

                // Summary verdict
                if (aqiA != null && aqiB != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    val cleanerCity = if (aqiA < aqiB) cityA.name else if (aqiB < aqiA) cityB.name else "Both cities"
                    val delta = abs(aqiA - aqiB)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppleCardSecondary)
                            .border(0.5.dp, AppleBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (delta == 0) "Equal air quality measured in both locations."
                            else "$cleanerCity has cleaner air by $delta AQI points.",
                            style = TypographyBody.copy(fontSize = 13.5.sp, color = AppleTextPrimary)
                        )
                    }
                }
            }
        }

        // 3. Pollutant Breakdown Comparison
        if (reportA != null && reportB != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(AppleCardBackground)
                        .border(0.5.dp, AppleBorder, RoundedCornerShape(22.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "POLLUTANT COMPARISON",
                        style = TypographySectionHeader
                    )

                    val metrics = listOf(
                        Triple("PM2.5", reportA.current.pm25, reportB.current.pm25),
                        Triple("PM10", reportA.current.pm10, reportB.current.pm10),
                        Triple("NO₂", reportA.current.nitrogenDioxide, reportB.current.nitrogenDioxide),
                        Triple("O₃", reportA.current.ozone, reportB.current.ozone)
                    )

                    metrics.forEach { (name, valA, valB) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppleCardSecondary)
                                .border(0.5.dp, AppleBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = String.format(Locale.US, "%.1f", valA),
                                style = TypographyBodyStrong.copy(
                                    color = if (valA <= valB) AppleGreen else AppleTextSecondary,
                                    fontSize = 14.sp
                                )
                            )

                            Text(
                                text = name,
                                style = TypographyBodyStrong.copy(fontSize = 14.sp, color = AppleTextPrimary)
                            )

                            Text(
                                text = String.format(Locale.US, "%.1f", valB),
                                style = TypographyBodyStrong.copy(
                                    color = if (valB <= valA) AppleGreen else AppleTextSecondary,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
