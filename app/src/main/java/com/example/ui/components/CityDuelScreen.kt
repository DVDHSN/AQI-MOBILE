package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.text.style.TextOverflow
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
    onBackToOverview: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBackToOverview()
    }
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
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(availableLocations, key = { "a_${it.id}" }) { loc ->
                            val isSel = loc.id == cityA.id
                            val animatedBg by animateColorAsState(
                                targetValue = if (isSel) AppleActionBlue else AppleCardSecondary,
                                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                                label = "duelCityABg_${loc.id}"
                            )
                            val animatedText by animateColorAsState(
                                targetValue = if (isSel) AppleTextPrimary else AppleTextSecondary,
                                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                                label = "duelCityAText_${loc.id}"
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(animatedBg)
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
                                        color = animatedText,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal
                                    ),
                                    maxLines = 1,
                                    softWrap = false
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
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(availableLocations, key = { "b_${it.id}" }) { loc ->
                            val isSel = loc.id == cityB.id
                            val animatedBg by animateColorAsState(
                                targetValue = if (isSel) AppleActionBlue else AppleCardSecondary,
                                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                                label = "duelCityBBg_${loc.id}"
                            )
                            val animatedText by animateColorAsState(
                                targetValue = if (isSel) AppleTextPrimary else AppleTextSecondary,
                                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                                label = "duelCityBText_${loc.id}"
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(animatedBg)
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
                                        color = animatedText,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal
                                    ),
                                    maxLines = 1,
                                    softWrap = false
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

            val animatedAqiA by animateIntAsState(
                targetValue = aqiA ?: 0,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "duelAqiA"
            )
            val animatedAqiB by animateIntAsState(
                targetValue = aqiB ?: 0,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "duelAqiB"
            )

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
                    val isCityAWinner = aqiA != null && aqiB != null && aqiA < aqiB
                    val isCityBWinner = aqiA != null && aqiB != null && aqiB < aqiA

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AppleCardSecondary)
                            .border(
                                width = if (isCityAWinner) 1.dp else 0.5.dp,
                                color = if (isCityAWinner) AppleGreen else AppleBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = cityA.name,
                                style = TypographyBodyStrong.copy(fontSize = 15.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isCityAWinner) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(AppleGreen)
                                )
                            }
                        }

                        if (isLoadingA) {
                            CircularProgressIndicator(
                                color = AppleActionBlue,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(28.dp)
                            )
                        } else if (aqiA != null) {
                            Text(
                                text = animatedAqiA.toString(),
                                style = TypographyHeroDisplay.copy(fontSize = 38.sp)
                            )
                            reportA?.current?.level?.let { AppleStatusBadge(level = it, fontSize = 11.sp) }
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
                            .border(
                                width = if (isCityBWinner) 1.dp else 0.5.dp,
                                color = if (isCityBWinner) AppleGreen else AppleBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = cityB.name,
                                style = TypographyBodyStrong.copy(fontSize = 15.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isCityBWinner) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(AppleGreen)
                                )
                            }
                        }

                        if (isLoadingB) {
                            CircularProgressIndicator(
                                color = AppleActionBlue,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(28.dp)
                            )
                        } else if (aqiB != null) {
                            Text(
                                text = animatedAqiB.toString(),
                                style = TypographyHeroDisplay.copy(fontSize = 38.sp)
                            )
                            reportB?.current?.level?.let { AppleStatusBadge(level = it, fontSize = 11.sp) }
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (delta > 0) AppleGreen.copy(alpha = 0.12f) else AppleCardSecondary)
                            .border(
                                0.5.dp,
                                if (delta > 0) AppleGreen.copy(alpha = 0.3f) else AppleBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (delta > 0) LucideIcons.Sparkles else LucideIcons.Activity,
                            contentDescription = null,
                            tint = if (delta > 0) AppleGreen else AppleTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (delta == 0) "Identical air quality measured in both locations."
                            else "$cleanerCity has cleaner air by $delta AQI points.",
                            style = TypographyBody.copy(
                                fontSize = 13.5.sp,
                                color = if (delta > 0) AppleTextPrimary else AppleTextSecondary
                            )
                        )
                    }
                }
            }
        }

        // 3. Pollutant Breakdown Comparison with animated comparison visualizers
        if (reportA != null && reportB != null) {
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "POLLUTANT COMPARISON",
                            style = TypographySectionHeader
                        )
                        Text(
                            text = "${cityA.name} vs ${cityB.name}",
                            style = TypographyCaption.copy(fontSize = 11.5.sp, color = AppleSkyBlue)
                        )
                    }

                    val metrics = listOf(
                        Triple("PM2.5", reportA.current.pm25, reportB.current.pm25),
                        Triple("PM10", reportA.current.pm10, reportB.current.pm10),
                        Triple("NO₂", reportA.current.nitrogenDioxide, reportB.current.nitrogenDioxide),
                        Triple("O₃", reportA.current.ozone, reportB.current.ozone)
                    )

                    metrics.forEach { (name, valA, valB) ->
                        val maxV = maxOf(valA, valB, 1f)
                        val fracA = (valA / maxV).coerceIn(0.08f, 1f)
                        val fracB = (valB / maxV).coerceIn(0.08f, 1f)

                        val animFracA by animateFloatAsState(
                            targetValue = fracA,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "duelBarA_$name"
                        )
                        val animFracB by animateFloatAsState(
                            targetValue = fracB,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "duelBarB_$name"
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppleCardSecondary)
                                .border(0.5.dp, AppleBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = String.format(Locale.US, "%.1f", valA),
                                    style = TypographyBodyStrong.copy(
                                        color = if (valA < valB) AppleGreen else if (valA > valB) AppleTextSecondary else AppleSkyBlue,
                                        fontSize = 14.sp
                                    )
                                )

                                Text(
                                    text = name,
                                    style = TypographyBodyStrong.copy(fontSize = 13.5.sp, color = AppleTextPrimary)
                                )

                                Text(
                                    text = String.format(Locale.US, "%.1f", valB),
                                    style = TypographyBodyStrong.copy(
                                        color = if (valB < valA) AppleGreen else if (valB > valA) AppleTextSecondary else AppleSkyBlue,
                                        fontSize = 14.sp
                                    )
                                )
                            }

                            // Dual comparative progress bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // City A bar (aligns right to center)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(9999.dp))
                                        .background(AppleCardTertiary),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(animFracA)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(9999.dp))
                                            .background(if (valA <= valB) AppleGreen else AppleOrange)
                                    )
                                }

                                // City B bar (aligns left from center)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(9999.dp))
                                        .background(AppleCardTertiary),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(animFracB)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(9999.dp))
                                            .background(if (valB <= valA) AppleGreen else AppleOrange)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
