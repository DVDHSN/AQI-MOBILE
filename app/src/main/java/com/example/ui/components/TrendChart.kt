package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AqiLevel
import com.example.data.model.ChartMetric
import com.example.data.model.HourlyDataPoint
import com.example.data.model.TimelineMode
import com.example.ui.animation.bounceClick
import com.example.ui.theme.*
import java.util.Locale
import kotlin.math.max

/**
 * Air Quality Trend & Forecast Card ({component.store-utility-card} / {component.product-tile-dark-3})
 * - Near-Black Tile 3 (#252527) surface canvas.
 * - Corner radius {rounded.lg} (18.dp).
 * - Action Blue (#0066cc) vector curve with subtle monochromatic transparency.
 * - Pill buttons ({rounded.pill}) for mode switcher and metric filters.
 * - Tactile spring feedback and native haptics on scrubbing.
 */
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
    val haptic = LocalHapticFeedback.current

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

    var localScrubbedPoint by remember { mutableStateOf<HourlyDataPoint?>(null) }
    LaunchedEffect(timelineMode, activeData) {
        localScrubbedPoint = null
    }

    val activePoint = localScrubbedPoint ?: scrubbedPoint ?: (if (timelineMode == TimelineMode.HISTORY) activeData.lastOrNull() else activeData.firstOrNull())
    val activeValue = activePoint?.let { selectedMetric.getValue(it) } ?: 0f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp)) // {rounded.lg} 18px
            .background(SurfaceTile3)        // Near-Black Tile 3 (#252527)
            .padding(20.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .testTag("trend_chart_section")
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(11.dp))
                .bounceClick { isExpanded = !isExpanded }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Atmospheric Trend",
                    style = TypographyTagline.copy(
                        fontSize = 19.sp,
                        color = BodyOnDark
                    )
                )

                Text(
                    text = if (isExpanded) {
                        if (timelineMode == TimelineMode.HISTORY) "7-Day Historical Analysis" else "5-Day Predictive Outlook"
                    } else {
                        val modeLabel = if (timelineMode == TimelineMode.HISTORY) "Past 7D" else "Forecast 5D"
                        "$modeLabel · ${selectedMetric.shortLabel}: ${String.format(Locale.US, "%.1f %s", activeValue, selectedMetric.unit)}"
                    },
                    style = TypographyCaption.copy(
                        color = if (isExpanded) FinePrint else SkyLinkBlue,
                        fontSize = 12.sp
                    ),
                    maxLines = 1
                )
            }

            // Expand / Collapse Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(SurfaceTile1)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isExpanded) "Hide" else "Show",
                    style = TypographyButtonUtility.copy(
                        color = BodyMuted,
                        fontSize = 11.sp
                    )
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse chart" else "Expand chart",
                    tint = BodyMuted,
                    modifier = Modifier
                        .size(14.dp)
                        .graphicsLayer { rotationZ = chevronRotation }
                )
            }
        }

        // Expanded Interactive Timeline
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(animationSpec = tween(200)) + expandVertically(),
            exit = fadeOut(animationSpec = tween(150)) + shrinkVertically()
        ) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))

                // Timeline Mode Switcher (Apple fluid sliding pill control)
                AppleSlidingSegmentedControl(
                    items = TimelineMode.entries,
                    selectedIndex = TimelineMode.entries.indexOf(timelineMode),
                    onItemSelected = { index ->
                        onSelectTimelineMode(TimelineMode.entries[index])
                        onScrubPoint(null)
                    },
                    label = { mode -> if (mode == TimelineMode.HISTORY) "7D Past" else "5D Forecast" }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Metric Selector Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(ChartMetric.entries.toTypedArray()) { metric ->
                        val isSelected = metric == selectedMetric

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(if (isSelected) ActionBlue else SurfaceTile1)
                                .bounceClick { onSelectMetric(metric) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = metric.shortLabel,
                                style = TypographyCaptionStrong.copy(
                                    color = if (isSelected) BodyOnDark else BodyMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Point Inspection Strip
                activePoint?.let { point ->
                    val ptLevel = if (selectedMetric == ChartMetric.US_AQI) {
                        AqiLevel.fromUsAqi(activeValue)
                    } else {
                        null
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(11.dp))
                            .background(SurfaceTile1)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${point.dayOfWeek}, ${point.displayDate} · ${point.displayHour}",
                            style = TypographyCaption.copy(
                                color = BodyMuted,
                                fontSize = 12.sp
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = String.format(Locale.US, "%.1f %s", activeValue, selectedMetric.unit),
                                style = TypographyBodyStrong.copy(
                                    color = ptLevel?.color ?: BodyOnDark,
                                    fontSize = 14.sp
                                )
                            )

                            if (ptLevel != null) {
                                AppleStatusPill(level = ptLevel)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Vector Canvas Chart
                var chartWidth by remember { mutableFloatStateOf(1f) }
                val pointsCount = activeData.size
                val geometryCache = remember(metricValues, maxVal) { ChartGeometryCache() }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceBlack)
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
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
                                    if (pt != localScrubbedPoint) {
                                        localScrubbedPoint = pt
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                }
                            )
                        }
                        .testTag("trend_canvas_chart")
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 6.dp, vertical = 8.dp)
                    ) {
                        val width = size.width
                        val height = size.height

                        if (metricValues.isEmpty() || pointsCount < 2) return@Canvas

                        // Horizontal hairlines (no loud grid lines)
                        val gridSteps = 3
                        for (i in 0..gridSteps) {
                            val y = height * (i.toFloat() / gridSteps)
                            drawLine(
                                color = DarkHairline,
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1f
                            )
                        }

                        // Geometry calculation
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

                        // Strict Apple design: Action Blue #0066cc curve with calm transparent falloff
                        drawPath(
                            path = geometryCache.fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    ActionBlue.copy(alpha = 0.22f),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = height
                            )
                        )

                        drawPath(
                            path = geometryCache.linePath,
                            color = ActionBlue,
                            style = Stroke(width = 2.4f, cap = StrokeCap.Round)
                        )

                        // Scrub indicator
                        val currentScrub = localScrubbedPoint ?: scrubbedPoint
                        currentScrub?.let { scrub ->
                            val scrubIndex = activeData.indexOfFirst { it.rawTime == scrub.rawTime }
                            if (scrubIndex in 0 until pointsCount) {
                                val stepX = width / (pointsCount - 1).toFloat()
                                val scrubX = scrubIndex * stepX
                                val scrubY = height - ((metricValues[scrubIndex] - minVal) / (maxVal - minVal) * height)
                                    .coerceIn(0f, height)

                                drawLine(
                                    color = BodyOnDark.copy(alpha = 0.5f),
                                    start = Offset(scrubX, 0f),
                                    end = Offset(scrubX, height),
                                    strokeWidth = 1f
                                )

                                drawCircle(
                                    color = ActionBlue,
                                    radius = 4.dp.toPx(),
                                    center = Offset(scrubX, scrubY)
                                )
                                drawCircle(
                                    color = BodyOnDark,
                                    radius = 2.dp.toPx(),
                                    center = Offset(scrubX, scrubY)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // X-axis timestamps
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
                            style = TypographyFinePrint.copy(
                                color = FinePrint,
                                fontSize = 10.sp
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
