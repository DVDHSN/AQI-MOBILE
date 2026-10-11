package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
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
import com.example.ui.animation.bounceClick
import com.example.ui.theme.*

/**
 * 30-Day Air Quality Calendar Heatmap Card redesigned for Apple design language.
 * - Near-Black Tile 2 (#2a2a2c) surface
 * - 18dp corner radius
 * - Apple typography & Action Blue accents
 */
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
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceTile2)
            .padding(18.dp)
            .animateContentSize()
            .testTag("monthly_heatmap_card")
    ) {
        // Summary Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .bounceClick { isExpanded = !isExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceTile1),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = ActionBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column {
                    Text(
                        text = "30-Day Retrospective",
                        style = TypographyBodyStrong.copy(fontSize = 15.sp, color = BodyOnDark)
                    )
                    Text(
                        text = "${report.goodDaysCount} clean days (${report.trendComparisonPercent}% compliance)",
                        style = TypographyCaption.copy(fontSize = 12.sp, color = SkyLinkBlue)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(SurfaceTile1)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isExpanded) "Hide" else "Inspect",
                    style = TypographyButtonUtility.copy(color = BodyMuted, fontSize = 11.sp)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = BodyMuted,
                    modifier = Modifier
                        .size(14.dp)
                        .graphicsLayer { rotationZ = arrowRotation }
                )
            }
        }

        // Expanded Calendar Grid
        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                // Day grid (6 cols x 5 rows)
                val chunkedDays = report.days.chunked(6)
                chunkedDays.forEach { rowDays ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowDays.forEach { day ->
                            val isSel = selectedDay?.date == day.date
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(day.level.color.copy(alpha = if (isSel) 0.5f else 0.2f))
                                    .bounceClick { selectedDay = day },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.displayDate.takeLast(2).trim(),
                                    style = TypographyCaptionStrong.copy(
                                        fontSize = 11.sp,
                                        color = if (isSel) BodyOnDark else BodyMuted
                                    )
                                )
                            }
                        }
                    }
                }

                // Selected Day Details
                selectedDay?.let { day ->
                    Spacer(modifier = Modifier.height(12.dp))
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
                            text = "${day.date} · AQI ${day.avgAqi.toInt()}",
                            style = TypographyBodyStrong.copy(fontSize = 13.sp)
                        )
                        AppleStatusPill(level = day.level)
                    }
                }
            }
        }
    }
}
