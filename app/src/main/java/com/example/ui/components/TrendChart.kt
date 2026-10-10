package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AqiLevel
import com.example.data.model.ChartMetric
import com.example.data.model.HourlyDataPoint
import com.example.data.model.TimelineMode
import com.example.ui.animation.bounceClickable
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
    var isExpanded by remember { mutableStateOf(true) }

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "trendChevronRotation"
    )

    val activeData = if (timelineMode == TimelineMode.HISTORY) historyData else forecastData

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

    val haloTransition = rememberInfiniteTransition(label = "scrubberHalo")
    val haloPulse by haloTransition.animateFloat(
        initialValue = 6f,
        targetValue = 11f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloRadius"
    )

    var localScrubbedPoint by remember { mutableStateOf<HourlyDataPoint?>(null) }
    LaunchedEffect(timelineMode, activeData) {
        localScrubbedPoint = null
    }

    val activePoint = localScrubbedPoint ?: scrubbedPoint ?: (if (timelineMode == TimelineMode.HISTORY) activeData.lastOrNull() else activeData.firstOrNull())
    val activeValue = activePoint?.let { selectedMetric.getValue(it) } ?: 0f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurface)
            .border(1.dp, HairlineBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .testTag("trend_chart_section")
    ) {
        // Expandable Header Row (Clickable toggle, min 48dp interactive size)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .bounceClickable(scaleDown = 0.98f) {
                    isExpanded = !isExpanded
                }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Air Quality Trend",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = InkWhite,
                        fontSize = 15.sp
                    )
                )

                Text(
                    text = if (isExpanded) {
                        if (timelineMode == TimelineMode.HISTORY) "7-Day Historical Analysis" else "5-Day Predictive Outlook"
                    } else {
                        val modeLabel = if (timelineMode == TimelineMode.HISTORY) "Past 7D" else "Forecast 5D"
                        "$modeLabel · ${selectedMetric.shortLabel}: ${String.format(Locale.US, "%.1f %s", activeValue, selectedMetric.unit)}"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isExpanded) InkFaint else EditorialBlue,
                        fontSize = 11.sp
                    ),
                    maxLines = 1
                )
            }

            // Expand / Collapse Pill + Rotating Chevron
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkElevated)
                    .padding(horizontal = 9.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (isExpanded) "Collapse" else "Expand",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = InkMuted,
                        fontSize = 10.sp
                    )
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse trend chart" else "Expand trend chart",
                    tint = InkMuted,
                    modifier = Modifier
                        .size(15.dp)
                        .graphicsLayer { rotationZ = chevronRotation }
                )
            }
        }

        // Collapsed Compact Snapshot Strip (Saves space on 6.3" screens)
        AnimatedVisibility(
            visible = !isExpanded,
            enter = fadeIn(animationSpec = tween(180)) + expandVertically(),
            exit = fadeOut(animationSpec = tween(120)) + shrinkVertically()
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkElevated)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val modeColor = if (timelineMode == TimelineMode.HISTORY) EditorialBlue else AqiModerate
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(modeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (timelineMode == TimelineMode.HISTORY) "7D PAST" else "5D FORECAST",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = modeColor,
                                fontSize = 9.sp
                            )
                        )
                    }

                    Text(
                        text = selectedMetric.shortLabel,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = InkWhite,
                            fontSize = 11.sp
                        )
                    )
                }

                Text(
                    text = String.format(Locale.US, "%.1f %s", activeValue, selectedMetric.unit),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = InkWhite,
                        fontSize = 13.sp
                    )
                )
            }
        }

        // Expanded Interactive Timeline Section
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(animationSpec = tween(220)) + expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ),
            exit = fadeOut(animationSpec = tween(150)) + shrinkVertically()
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))

                // Timeline Mode Switcher (Pill style with spring scale microinteraction)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkElevated)
                        .padding(2.dp)
                ) {
                    TimelineMode.entries.forEach { mode ->
                        val isSelected = mode == timelineMode
                        val label = if (mode == TimelineMode.HISTORY) "7D Past" else "5D Forecast"

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) InkWhite else DarkElevated)
                                .bounceClickable(scaleDown = 0.95f) {
                                    onSelectTimelineMode(mode)
                                    onScrubPoint(null)
                                }
                                .padding(vertical = 6.dp)
                                .testTag("timeline_mode_${mode.name}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) PitchBlack else InkMuted,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.2.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Metric Selector Chips with tactile microinteraction
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(ChartMetric.entries.toTypedArray()) { metric ->
                        val isSelected = metric == selectedMetric

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) DarkElevated else DarkSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) EditorialBlue.copy(alpha = 0.6f) else HairlineBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .bounceClickable(scaleDown = 0.94f) { onSelectMetric(metric) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = metric.shortLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) InkWhite else InkMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Active Inspection Point Readout with dynamic color
                activePoint?.let { point ->
                    val ptLevel = if (selectedMetric == ChartMetric.US_AQI) {
                        AqiLevel.fromUsAqi(activeValue)
                    } else {
                        null
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkElevated)
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${point.dayOfWeek}, ${point.displayDate} · ${point.displayHour}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = InkMuted,
                                fontSize = 11.sp
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = String.format(Locale.US, "%.1f %s", activeValue, selectedMetric.unit),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = ptLevel?.color ?: EditorialBlue,
                                    fontSize = 13.sp
                                )
                            )

                            if (ptLevel != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ptLevel.color.copy(alpha = 0.15f))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
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

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive Canvas Chart (Tuned to 140.dp height for optimal 6.3" screen proportion)
                var chartWidth by remember { mutableFloatStateOf(1f) }
                val pointsCount = activeData.size

                // Zero-recomposition geometry cache (Plain Kotlin container, no Compose state writes inside DrawScope)
                val geometryCache = remember(metricValues, maxVal) { ChartGeometryCache() }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PitchBlack)
                        .onSizeChanged { size ->
                            chartWidth = size.width.toFloat()
                        }
                        .pointerInput(pointsCount, activeData) {
                            detectTapGestures(
                                onPress = { offset ->
                                    val safeWidth = if (chartWidth > 0f) chartWidth else 1f
                                    val index = ((offset.x / safeWidth) * (pointsCount - 1))
                                        .toInt()
                                        .coerceIn(0, pointsCount - 1)
                                    val pt = activeData.getOrNull(index)
                                    localScrubbedPoint = pt
                                    onScrubPoint(pt)
                                }
                            )
                        }
                        .pointerInput(pointsCount, activeData) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val safeWidth = if (chartWidth > 0f) chartWidth else 1f
                                    val index = ((offset.x / safeWidth) * (pointsCount - 1))
                                        .toInt()
                                        .coerceIn(0, pointsCount - 1)
                                    val pt = activeData.getOrNull(index)
                                    localScrubbedPoint = pt
                                    onScrubPoint(pt)
                                },
                                onDragEnd = {},
                                onDragCancel = {
                                    localScrubbedPoint = null
                                    onScrubPoint(null)
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    val safeWidth = if (chartWidth > 0f) chartWidth else 1f
                                    val index = ((change.position.x / safeWidth) * (pointsCount - 1))
                                        .toInt()
                                        .coerceIn(0, pointsCount - 1)
                                    val pt = activeData.getOrNull(index)
                                    localScrubbedPoint = pt
                                }
                            )
                        }
                        .testTag("trend_canvas_chart")
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 6.dp, vertical = 6.dp)
                    ) {
                        val width = size.width
                        val height = size.height

                        if (metricValues.isEmpty() || pointsCount < 2) return@Canvas

                        // Horizontal hairline gridlines
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

                        // Path calculation without snapshot state invalidations
                        if (geometryCache.width != width || geometryCache.height != height) {
                            geometryCache.width = width
                            geometryCache.height = height
                            geometryCache.linePath.reset()
                            geometryCache.fillPath.reset()

                            val stepX = width / (pointsCount - 1).toFloat()
                            val firstY = height - ((metricValues[0] - minVal) / (maxVal - minVal) * height)
                                .coerceIn(0f, height)

                            geometryCache.linePath.moveTo(0f, firstY)
                            geometryCache.fillPath.moveTo(0f, height)
                            geometryCache.fillPath.lineTo(0f, firstY)

                            for (i in 1 until pointsCount) {
                                val prevX = (i - 1) * stepX
                                val prevY = height - ((metricValues[i - 1] - minVal) / (maxVal - minVal) * height)
                                    .coerceIn(0f, height)
                                val currX = i * stepX
                                val currY = height - ((metricValues[i] - minVal) / (maxVal - minVal) * height)
                                    .coerceIn(0f, height)

                                val midX = (prevX + currX) / 2f
                                geometryCache.linePath.cubicTo(midX, prevY, midX, currY, currX, currY)
                                geometryCache.fillPath.cubicTo(midX, prevY, midX, currY, currX, currY)
                            }

                            geometryCache.fillPath.lineTo(width, height)
                            geometryCache.fillPath.close()
                        }

                        val gradientColor = if (timelineMode == TimelineMode.HISTORY) EditorialBlue else AqiModerate
                        drawPath(
                            path = geometryCache.fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    gradientColor.copy(alpha = 0.2f),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = height
                            )
                        )

                        drawPath(
                            path = geometryCache.linePath,
                            color = gradientColor,
                            style = Stroke(width = 2.2f, cap = StrokeCap.Round)
                        )

                        // Microinteraction: Scrub cursor with glowing indicator dot
                        val currentScrub = localScrubbedPoint ?: scrubbedPoint
                        currentScrub?.let { scrub ->
                            val scrubIndex = activeData.indexOfFirst { it.rawTime == scrub.rawTime }
                            if (scrubIndex in 0 until pointsCount) {
                                val stepX = width / (pointsCount - 1).toFloat()
                                val scrubX = scrubIndex * stepX
                                val scrubY = height - ((metricValues[scrubIndex] - minVal) / (maxVal - minVal) * height)
                                    .coerceIn(0f, height)

                                drawLine(
                                    color = InkWhite.copy(alpha = 0.6f),
                                    start = Offset(scrubX, 0f),
                                    end = Offset(scrubX, height),
                                    strokeWidth = 1f
                                )

                                // Outer breathing glow circle
                                drawCircle(
                                    color = gradientColor.copy(alpha = 0.22f),
                                    radius = haloPulse.dp.toPx(),
                                    center = Offset(scrubX, scrubY)
                                )

                                drawCircle(
                                    color = gradientColor.copy(alpha = 0.5f),
                                    radius = 5.dp.toPx(),
                                    center = Offset(scrubX, scrubY)
                                )

                                drawCircle(
                                    color = PitchBlack,
                                    radius = 3.5.dp.toPx(),
                                    center = Offset(scrubX, scrubY)
                                )
                                drawCircle(
                                    color = InkWhite,
                                    radius = 2.dp.toPx(),
                                    center = Offset(scrubX, scrubY)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

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
            }
        }
    }
}

private class ChartGeometryCache {
    var width: Float = 0f
    var height: Float = 0f
    val linePath: Path = Path()
    val fillPath: Path = Path()
}
