package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AqiLevel
import com.example.data.model.ChartMetric
import com.example.data.model.HourlyDataPoint
import com.example.data.model.TimelineMode
import com.example.ui.theme.*
import java.util.Locale
import kotlin.math.max

@Composable
fun TrendChart(
    historyData: List<HourlyDataPoint>,
    forecastData: List<HourlyDataPoint>,
    timelineMode: TimelineMode,
    onSelectTimelineMode: (TimelineMode) -> Unit,
    selectedMetric: ChartMetric,
    onSelectMetric: (ChartMetric) -> Unit,
    scrubbedPoint: HourlyDataPoint?,
    onScrubPoint: (HourlyDataPoint?) -> Unit,
    modifier: Modifier = Modifier
) {
    // Select active dataset based on mode
    val activeData = if (timelineMode == TimelineMode.HISTORY) historyData else forecastData

    // Extract non-null points
    val metricValues = remember(activeData, selectedMetric) {
        activeData.map { point ->
            selectedMetric.getValue(point) ?: 0f
        }
    }

    val maxVal = remember(metricValues) {
        val calculatedMax = metricValues.maxOrNull() ?: 100f
        max(calculatedMax * 1.15f, selectedMetric.whoThreshold * 1.2f)
    }

    val minVal = 0f

    val peakVal = remember(metricValues) { metricValues.maxOrNull() ?: 0f }
    val lowVal = remember(metricValues) { metricValues.minOrNull() ?: 0f }
    val avgVal = remember(metricValues) {
        if (metricValues.isNotEmpty()) metricValues.average().toFloat() else 0f
    }

    // Active inspection point
    val activePoint = scrubbedPoint ?: (if (timelineMode == TimelineMode.HISTORY) activeData.lastOrNull() else activeData.firstOrNull())
    val activeValue = activePoint?.let { selectedMetric.getValue(it) } ?: 0f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .border(1.dp, HairlineBorder)
            .padding(16.dp)
            .testTag("trend_chart_section")
    ) {
        // Section Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (timelineMode == TimelineMode.HISTORY) "HISTORICAL // 168H" else "FORECAST // 120H",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp,
                        color = EditorialBlue,
                        fontSize = 10.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = selectedMetric.title.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = InkWhite,
                        letterSpacing = 0.5.sp,
                        fontSize = 15.sp
                    ),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Timeline Mode Selector: [ 7D HISTORY | 5D FORECAST ]
            Row(
                modifier = Modifier
                    .border(1.dp, HairlineBorderLight)
                    .background(PitchBlack)
            ) {
                TimelineMode.entries.forEach { mode ->
                    val isSelected = mode == timelineMode
                    Box(
                        modifier = Modifier
                            .background(if (isSelected) InkWhite else PitchBlack)
                            .clickable {
                                onSelectTimelineMode(mode)
                                onScrubPoint(null)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("timeline_mode_${mode.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (mode == TimelineMode.HISTORY) "7D PAST" else "5D AHEAD",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) PitchBlack else InkMuted,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            ),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Metric Selector Tabs
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(ChartMetric.entries.toTypedArray()) { metric ->
                val isSelected = metric == selectedMetric
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .background(if (isSelected) InkWhite else PitchBlack)
                        .border(
                            1.dp,
                            if (isSelected) InkWhite else HairlineBorder
                        )
                        .clickable { onSelectMetric(metric) }
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = metric.shortLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSelected) PitchBlack else InkMuted,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Inspection Readout Card
        activePoint?.let { point ->
            val ptLevel = if (selectedMetric == ChartMetric.US_AQI) {
                AqiLevel.fromUsAqi(activeValue)
            } else {
                null
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkElevated)
                    .border(1.dp, HairlineBorder)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (scrubbedPoint != null) "INSPECTING TIMELINE" else (if (timelineMode == TimelineMode.HISTORY) "LATEST MEASUREMENT" else "UPCOMING PROJECTION"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (scrubbedPoint != null) EditorialBlue else InkFaint,
                            letterSpacing = 0.8.sp,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = "${point.dayOfWeek}, ${point.displayDate} · ${point.displayHour}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = InkWhite,
                            fontSize = 13.sp
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = String.format(Locale.US, "%.1f %s", activeValue, selectedMetric.unit),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = ptLevel?.color ?: EditorialBlue,
                            letterSpacing = (-0.5).sp,
                            fontSize = 16.sp
                        )
                    )

                    if (ptLevel != null) {
                        Box(
                            modifier = Modifier
                                .background(ptLevel.color.copy(alpha = 0.2f))
                                .border(1.dp, ptLevel.color)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = ptLevel.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ptLevel.color,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Interactive Canvas Chart
        var chartWidth by remember { mutableFloatStateOf(1f) }
        val pointsCount = activeData.size

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(PitchBlack)
                .border(1.dp, HairlineBorder)
                .pointerInput(pointsCount) {
                    detectTapGestures(
                        onPress = { offset ->
                            val index = ((offset.x / chartWidth) * (pointsCount - 1))
                                .toInt()
                                .coerceIn(0, pointsCount - 1)
                            onScrubPoint(activeData.getOrNull(index))
                        }
                    )
                }
                .pointerInput(pointsCount) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val index = ((offset.x / chartWidth) * (pointsCount - 1))
                                .toInt()
                                .coerceIn(0, pointsCount - 1)
                            onScrubPoint(activeData.getOrNull(index))
                        },
                        onDragEnd = {},
                        onDragCancel = {
                            onScrubPoint(null)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val index = ((change.position.x / chartWidth) * (pointsCount - 1))
                                .toInt()
                                .coerceIn(0, pointsCount - 1)
                            onScrubPoint(activeData.getOrNull(index))
                        }
                    )
                }
                .testTag("trend_canvas_chart")
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                chartWidth = size.width
                val width = size.width
                val height = size.height

                if (metricValues.isEmpty() || pointsCount < 2) return@Canvas

                // 1. Draw horizontal hairline gridlines (3 lines)
                val gridSteps = 3
                for (i in 0..gridSteps) {
                    val y = height * (i.toFloat() / gridSteps)
                    drawLine(
                        color = HairlineBorder,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }

                // 2. Compute path points
                val stepX = width / (pointsCount - 1).toFloat()
                val path = Path()
                val fillPath = Path()

                val firstY = height - ((metricValues[0] - minVal) / (maxVal - minVal) * height)
                    .coerceIn(0f, height)
                path.moveTo(0f, firstY)
                fillPath.moveTo(0f, height)
                fillPath.lineTo(0f, firstY)

                for (i in 1 until pointsCount) {
                    val prevX = (i - 1) * stepX
                    val prevY = height - ((metricValues[i - 1] - minVal) / (maxVal - minVal) * height)
                        .coerceIn(0f, height)
                    val currX = i * stepX
                    val currY = height - ((metricValues[i] - minVal) / (maxVal - minVal) * height)
                        .coerceIn(0f, height)

                    val midX = (prevX + currX) / 2f
                    path.cubicTo(midX, prevY, midX, currY, currX, currY)
                    fillPath.cubicTo(midX, prevY, midX, currY, currX, currY)
                }

                fillPath.lineTo(width, height)
                fillPath.close()

                // 3. Draw gradient fill below trend curve
                val gradientColor = if (timelineMode == TimelineMode.HISTORY) EditorialBlue else AqiModerate
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            gradientColor.copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = height
                    )
                )

                // 4. Draw curve line
                drawPath(
                    path = path,
                    color = gradientColor,
                    style = Stroke(width = 2.2f, cap = StrokeCap.Round)
                )

                // 5. Draw scrub cursor if active
                scrubbedPoint?.let { scrub ->
                    val scrubIndex = activeData.indexOfFirst { it.rawTime == scrub.rawTime }
                    if (scrubIndex in 0 until pointsCount) {
                        val scrubX = scrubIndex * stepX
                        val scrubY = height - ((metricValues[scrubIndex] - minVal) / (maxVal - minVal) * height)
                            .coerceIn(0f, height)

                        drawLine(
                            color = InkWhite.copy(alpha = 0.7f),
                            start = Offset(scrubX, 0f),
                            end = Offset(scrubX, height),
                            strokeWidth = 1.2f
                        )

                        drawCircle(
                            color = PitchBlack,
                            radius = 6.dp.toPx(),
                            center = Offset(scrubX, scrubY)
                        )
                        drawCircle(
                            color = gradientColor,
                            radius = 4.dp.toPx(),
                            center = Offset(scrubX, scrubY)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // X-axis Day markers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val sampledMarkers = remember(activeData) {
                if (activeData.size >= 5) {
                    val step = activeData.size / 5
                    (0..4).mapNotNull { idx ->
                        activeData.getOrNull((idx * step).coerceAtMost(activeData.size - 1))
                    }
                } else {
                    activeData
                }
            }

            sampledMarkers.forEach { marker ->
                Text(
                    text = "${marker.dayOfWeek} ${marker.displayDate.takeLast(2)}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = InkFaint,
                        fontSize = 9.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stats Bar: Peak, Low, Average
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "PEAK: ${String.format(Locale.US, "%.1f", peakVal)}",
                style = MaterialTheme.typography.labelSmall.copy(color = InkMuted, fontSize = 10.sp)
            )
            Text(
                text = "LOW: ${String.format(Locale.US, "%.1f", lowVal)}",
                style = MaterialTheme.typography.labelSmall.copy(color = InkMuted, fontSize = 10.sp)
            )
            Text(
                text = "AVG: ${String.format(Locale.US, "%.1f", avgVal)}",
                style = MaterialTheme.typography.labelSmall.copy(color = InkMuted, fontSize = 10.sp)
            )
            Text(
                text = "TOUCH TO INSPECT",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = EditorialBlue,
                    letterSpacing = 0.5.sp,
                    fontSize = 10.sp
                )
            )
        }
    }
}
