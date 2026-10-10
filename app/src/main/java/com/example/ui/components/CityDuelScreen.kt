package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
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
    var scrubIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .padding(16.dp)
            .testTag("city_duel_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Duel Masthead Title
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .border(1.dp, HairlineBorder)
                    .padding(16.dp)
            ) {
                Text(
                    text = "COMPARATIVE ENVIRONMENTAL ANALYSIS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = EditorialBlue,
                        letterSpacing = 1.2.sp
                    )
                )
                Text(
                    text = "CITY-TO-CITY ATMOSPHERIC DUEL",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = InkWhite,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Side-by-side criteria pollutant comparison and synchronous multi-day trajectory curves.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = InkMuted)
                )
            }
        }

        // 2. City Selector Rows (Choose City A and City B)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .border(1.dp, HairlineBorder)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // City A Selector
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "CITY A (CYAN CURVE)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EditorialBlue,
                                letterSpacing = 0.8.sp
                            )
                        )
                        Text(
                            text = cityA.name.uppercase(),
                            style = MaterialTheme.typography.labelLarge.copy(color = InkWhite)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(availableLocations, key = { "a_${it.id}" }) { loc ->
                            val isSel = loc.id == cityA.id
                            Box(
                                modifier = Modifier
                                    .background(if (isSel) EditorialBlue else PitchBlack)
                                    .border(1.dp, if (isSel) EditorialBlue else HairlineBorder)
                                    .clickable { onSelectCityA(loc) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = loc.name.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSel) PitchBlack else InkMuted,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(HairlineBorder)
                )

                // City B Selector
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "CITY B (AMBER CURVE)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AqiModerate,
                                letterSpacing = 0.8.sp
                            )
                        )
                        Text(
                            text = cityB.name.uppercase(),
                            style = MaterialTheme.typography.labelLarge.copy(color = InkWhite)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(availableLocations, key = { "b_${it.id}" }) { loc ->
                            val isSel = loc.id == cityB.id
                            Box(
                                modifier = Modifier
                                    .background(if (isSel) AqiModerate else PitchBlack)
                                    .border(1.dp, if (isSel) AqiModerate else HairlineBorder)
                                    .clickable { onSelectCityB(loc) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = loc.name.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSel) PitchBlack else InkMuted,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Verdict Card (When both reports loaded)
        if (reportA != null && reportB != null) {
            val aqiA = reportA.current.usAqi
            val aqiB = reportB.current.usAqi
            val diff = abs(aqiA - aqiB)
            val winnerCity = if (aqiA <= aqiB) cityA.name else cityB.name
            val percentCleaner = if (max(aqiA, aqiB) > 0f) {
                ((diff / max(aqiA, aqiB)) * 100f).toInt()
            } else 0

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkElevated)
                        .border(1.dp, EditorialBlue)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ENVIRONMENTAL VERDICT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EditorialBlue,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$winnerCity MAINTAINS CLEANER AIR",
                        style = MaterialTheme.typography.titleLarge.copy(color = InkWhite)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$winnerCity's US AQI is ${if (aqiA <= aqiB) aqiA.toInt() else aqiB.toInt()} vs ${(if (aqiA > aqiB) aqiA else aqiB).toInt()} — $percentCleaner% lower criteria air pollution load at this hour.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = InkMuted)
                    )
                }
            }

            // 4. Side-by-side Scorecards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Scorecard A
                    CityScorecard(
                        cityName = cityA.name,
                        country = cityA.country,
                        accentColor = EditorialBlue,
                        report = reportA,
                        modifier = Modifier.weight(1f)
                    )

                    // Scorecard B
                    CityScorecard(
                        cityName = cityB.name,
                        country = cityB.country,
                        accentColor = AqiModerate,
                        report = reportB,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 5. Dual Overlay Trend Chart
            item {
                DualOverlayChart(
                    reportA = reportA,
                    reportB = reportB,
                    cityA = cityA,
                    cityB = cityB,
                    selectedMetric = selectedMetric,
                    onSelectMetric = onSelectMetric,
                    scrubIndex = scrubIndex,
                    onScrubIndex = { scrubIndex = it }
                )
            }

            // 6. Direct Pollutant Comparison Matrix
            item {
                HeadToHeadTable(reportA = reportA, reportB = reportB, nameA = cityA.name, nameB = cityB.name)
            }
        } else if (isLoadingA || isLoadingB) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = EditorialBlue)
                }
            }
        }
    }
}

@Composable
private fun CityScorecard(
    cityName: String,
    country: String,
    accentColor: Color,
    report: AirQualityReport,
    modifier: Modifier = Modifier
) {
    val level = report.current.level
    Column(
        modifier = modifier
            .background(DarkSurface)
            .border(1.dp, HairlineBorder)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = cityName.uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(color = InkWhite)
            )
            Box(
                modifier = Modifier
                    .background(accentColor)
                    .size(8.dp)
            )
        }
        Text(
            text = country.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(color = InkFaint, fontSize = 9.sp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = report.current.usAqi.toInt().toString(),
            style = MaterialTheme.typography.displayMedium.copy(color = InkWhite)
        )

        Box(
            modifier = Modifier
                .border(1.dp, level.color)
                .background(PitchBlack)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = level.label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = level.color,
                    fontSize = 9.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "PM2.5: ${String.format(Locale.US, "%.1f", report.current.pm25)} μg",
            style = MaterialTheme.typography.labelSmall.copy(color = InkMuted)
        )
        Text(
            text = "O₃: ${String.format(Locale.US, "%.1f", report.current.ozone)} μg",
            style = MaterialTheme.typography.labelSmall.copy(color = InkMuted)
        )
    }
}

@Composable
private fun DualOverlayChart(
    reportA: AirQualityReport,
    reportB: AirQualityReport,
    cityA: LocationItem,
    cityB: LocationItem,
    selectedMetric: ChartMetric,
    onSelectMetric: (ChartMetric) -> Unit,
    scrubIndex: Int?,
    onScrubIndex: (Int?) -> Unit
) {
    val historyA = reportA.hourlyHistory
    val historyB = reportB.hourlyHistory
    val count = minOf(historyA.size, historyB.size)

    val valuesA = remember(historyA, selectedMetric) {
        historyA.take(count).map { selectedMetric.getValue(it) ?: 0f }
    }
    val valuesB = remember(historyB, selectedMetric) {
        historyB.take(count).map { selectedMetric.getValue(it) ?: 0f }
    }

    val maxVal = remember(valuesA, valuesB) {
        max((valuesA + valuesB).maxOrNull() ?: 100f * 1.15f, selectedMetric.whoThreshold * 1.2f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .border(1.dp, HairlineBorder)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "7-DAY TRAJECTORY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = InkFaint,
                        letterSpacing = 1.2.sp
                    )
                )
                Text(
                    text = selectedMetric.title.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(color = InkWhite)
                )
            }

            // Legend
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(8.dp).background(EditorialBlue))
                    Text(cityA.name, style = MaterialTheme.typography.labelSmall.copy(color = InkMuted, fontSize = 10.sp))
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(8.dp).background(AqiModerate))
                    Text(cityB.name, style = MaterialTheme.typography.labelSmall.copy(color = InkMuted, fontSize = 10.sp))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Metric pills
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(ChartMetric.entries.toTypedArray()) { metric ->
                val isSel = metric == selectedMetric
                Box(
                    modifier = Modifier
                        .background(if (isSel) InkWhite else PitchBlack)
                        .border(1.dp, if (isSel) InkWhite else HairlineBorder)
                        .clickable { onSelectMetric(metric) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        metric.shortLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSel) PitchBlack else InkMuted,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Inspection readout
        if (scrubIndex != null && scrubIndex in 0 until count) {
            val ptA = historyA[scrubIndex]
            val valA = valuesA[scrubIndex]
            val valB = valuesB[scrubIndex]

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkElevated)
                    .border(1.dp, HairlineBorder)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${ptA.dayOfWeek} ${ptA.displayDate} · ${ptA.displayHour}",
                    style = MaterialTheme.typography.labelSmall.copy(color = InkWhite)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "${cityA.name}: ${String.format(Locale.US, "%.1f", valA)}",
                        style = MaterialTheme.typography.labelSmall.copy(color = EditorialBlue)
                    )
                    Text(
                        "${cityB.name}: ${String.format(Locale.US, "%.1f", valB)}",
                        style = MaterialTheme.typography.labelSmall.copy(color = AqiModerate)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Dual Canvas
        var chartWidth by remember { mutableFloatStateOf(1f) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(PitchBlack)
                .border(1.dp, HairlineBorder)
                .pointerInput(count) {
                    detectTapGestures { offset ->
                        val idx = ((offset.x / chartWidth) * (count - 1)).toInt().coerceIn(0, count - 1)
                        onScrubIndex(idx)
                    }
                }
                .pointerInput(count) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val idx = ((offset.x / chartWidth) * (count - 1)).toInt().coerceIn(0, count - 1)
                            onScrubIndex(idx)
                        },
                        onDragEnd = {},
                        onDragCancel = { onScrubIndex(null) },
                        onDrag = { change, _ ->
                            change.consume()
                            val idx = ((change.position.x / chartWidth) * (count - 1)).toInt().coerceIn(0, count - 1)
                            onScrubIndex(idx)
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 10.dp)) {
                chartWidth = size.width
                val w = size.width
                val h = size.height

                if (count < 2) return@Canvas

                val stepX = w / (count - 1).toFloat()

                // Hairline grid
                for (i in 0..2) {
                    val y = h * (i / 2f)
                    drawLine(HairlineBorder, Offset(0f, y), Offset(w, y), 1f)
                }

                // Draw curve A
                val pathA = Path()
                val firstYA = h - (valuesA[0] / maxVal * h).coerceIn(0f, h)
                pathA.moveTo(0f, firstYA)
                for (i in 1 until count) {
                    val x = i * stepX
                    val y = h - (valuesA[i] / maxVal * h).coerceIn(0f, h)
                    pathA.lineTo(x, y)
                }
                drawPath(pathA, EditorialBlue, style = Stroke(width = 2.2f, cap = StrokeCap.Round))

                // Draw curve B
                val pathB = Path()
                val firstYB = h - (valuesB[0] / maxVal * h).coerceIn(0f, h)
                pathB.moveTo(0f, firstYB)
                for (i in 1 until count) {
                    val x = i * stepX
                    val y = h - (valuesB[i] / maxVal * h).coerceIn(0f, h)
                    pathB.lineTo(x, y)
                }
                drawPath(pathB, AqiModerate, style = Stroke(width = 2.2f, cap = StrokeCap.Round))

                // Scrub line
                scrubIndex?.let { idx ->
                    if (idx in 0 until count) {
                        val sx = idx * stepX
                        drawLine(InkWhite.copy(alpha = 0.7f), Offset(sx, 0f), Offset(sx, h), 1f)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeadToHeadTable(
    reportA: AirQualityReport,
    reportB: AirQualityReport,
    nameA: String,
    nameB: String
) {
    val pollutants = listOf(
        Triple("PM2.5 (μg/m³)", reportA.current.pm25, reportB.current.pm25),
        Triple("PM10 (μg/m³)", reportA.current.pm10, reportB.current.pm10),
        Triple("O₃ (μg/m³)", reportA.current.ozone, reportB.current.ozone),
        Triple("NO₂ (μg/m³)", reportA.current.nitrogenDioxide, reportB.current.nitrogenDioxide),
        Triple("SO₂ (μg/m³)", reportA.current.sulphurDioxide, reportB.current.sulphurDioxide),
        Triple("CO (μg/m³)", reportA.current.carbonMonoxide, reportB.current.carbonMonoxide)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .border(1.dp, HairlineBorder)
            .padding(14.dp)
    ) {
        Text(
            "CRITERIA POLLUTANT HEAD-TO-HEAD",
            style = MaterialTheme.typography.labelSmall.copy(color = InkFaint, letterSpacing = 1.sp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("POLLUTANT", style = MaterialTheme.typography.labelSmall.copy(color = InkFaint), modifier = Modifier.weight(1.5f))
            Text(nameA.uppercase(), style = MaterialTheme.typography.labelSmall.copy(color = EditorialBlue), modifier = Modifier.weight(1f))
            Text(nameB.uppercase(), style = MaterialTheme.typography.labelSmall.copy(color = AqiModerate), modifier = Modifier.weight(1f))
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(HairlineBorder))

        pollutants.forEach { (name, valA, valB) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(name, style = MaterialTheme.typography.bodyMedium.copy(color = InkWhite), modifier = Modifier.weight(1.5f))
                Text(String.format(Locale.US, "%.1f", valA), style = MaterialTheme.typography.labelMedium.copy(color = InkMuted), modifier = Modifier.weight(1f))
                Text(String.format(Locale.US, "%.1f", valB), style = MaterialTheme.typography.labelMedium.copy(color = InkMuted), modifier = Modifier.weight(1f))
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(HairlineBorderLight))
        }
    }
}
