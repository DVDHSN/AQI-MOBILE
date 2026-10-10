package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyHeatmapPoint
import com.example.data.model.MonthlyHeatmapReport
import com.example.ui.theme.*

@Composable
fun MonthlyHeatmapCard(
    report: MonthlyHeatmapReport,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf<DailyHeatmapPoint?>(null) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "heatmapArrow"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurface)
            .border(1.dp, HairlineBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
            .animateContentSize()
            .testTag("monthly_heatmap_card")
    ) {
        // Summary Header (Clickable to toggle)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = EditorialBlue,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Column {
                    Text(
                        text = "30-Day Retrospective",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = InkWhite,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = "${report.goodDaysCount} clean days (${report.trendComparisonPercent}%)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EditorialBlue,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (isExpanded) "Hide" else "Expand",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = InkMuted,
                        fontSize = 11.sp
                    )
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = InkMuted,
                    modifier = Modifier
                        .size(16.dp)
                        .graphicsLayer { rotationZ = arrowRotation }
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = report.trendInsight,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = InkMuted,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        )

        // Expandable Responsive Dot Matrix
        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(HairlineBorder)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Heatmap Grid: 7 columns with weight(1f) to guarantee zero clipping on 6.3" screens
                val rows = report.days.chunked(7)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    rows.forEach { weekDays ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            weekDays.forEach { day ->
                                val isSelected = selectedDay?.date == day.date
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(day.level.color.copy(alpha = if (isSelected) 0.5f else 0.2f))
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) InkWhite else day.level.color.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .clickable { selectedDay = day },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.displayDate.takeLast(2).trim(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) InkWhite else InkMuted,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                            repeat(7 - weekDays.size) {
                                Spacer(modifier = Modifier.weight(1f).height(28.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selected Day Inspection Tooltip
                selectedDay?.let { day ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkElevated)
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${day.dayOfWeek}, ${day.displayDate}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = InkWhite,
                                fontSize = 11.sp
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Avg AQI ${day.avgAqi.toInt()}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = day.level.color,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = "· ${day.level.label}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = day.level.color,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Legend row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(color = AqiGood, label = "Good")
                    LegendItem(color = AqiModerate, label = "Moderate")
                    LegendItem(color = AqiSensitive, label = "Sensitive")
                    LegendItem(color = AqiUnhealthy, label = "Unhealthy")
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: androidx.compose.ui.graphics.Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = InkFaint,
                fontSize = 9.sp
            )
        )
    }
}
