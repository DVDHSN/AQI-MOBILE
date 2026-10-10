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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Spa
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
import com.example.data.model.AllergenReport
import com.example.ui.animation.bounceClickable
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun PollenAllergenCard(
    report: AllergenReport,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "allergenArrowRotation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurface)
            .border(1.dp, HairlineBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
            .animateContentSize()
            .testTag("pollen_allergen_card")
    ) {
        // Top Summary Row (Clickable to toggle expansion)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .bounceClickable(scaleDown = 0.98f) { isExpanded = !isExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = report.overallRisk.color,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column {
                    Text(
                        text = "Pollen & Allergens",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = InkWhite,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = "Dominant: ${report.dominantPollen}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = InkFaint,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Overall Risk Pill + Chevron
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(report.overallRisk.color.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = report.overallRisk.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = report.overallRisk.color,
                            fontSize = 11.sp
                        )
                    )
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse details" else "Expand details",
                    tint = InkMuted,
                    modifier = Modifier
                        .size(18.dp)
                        .graphicsLayer { rotationZ = arrowRotation }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = report.overallRisk.advice,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = InkMuted,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        )

        // Expandable breakdown of individual pollens
        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 14.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(HairlineBorder)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    report.pollens.forEach { pollen ->
                        var isPollenSelected by remember { mutableStateOf(false) }
                        val progressFraction = (pollen.countGrains / 350f).coerceIn(0.05f, 1f)
                        val animatedPollenProgress by animateFloatAsState(
                            targetValue = progressFraction,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "pollenProgress_${pollen.name}"
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkElevated)
                                .then(
                                    if (isPollenSelected) {
                                        Modifier.border(1.dp, pollen.risk.color.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    } else Modifier
                                )
                                .bounceClickable(scaleDown = 0.97f) {
                                    isPollenSelected = !isPollenSelected
                                }
                                .animateContentSize(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    )
                                )
                                .padding(horizontal = 12.dp, vertical = 9.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(pollen.risk.color)
                                    )
                                    Text(
                                        text = pollen.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
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
                                        text = String.format(Locale.US, "%.0f grains/m³", pollen.countGrains),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = InkMuted,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Text(
                                        text = pollen.risk.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = pollen.risk.color,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            // Animated Exposure Progress Bar
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.5.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(PitchBlack)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(animatedPollenProgress)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(pollen.risk.color)
                                )
                            }

                            AnimatedVisibility(visible = isPollenSelected) {
                                Column(modifier = Modifier.padding(top = 8.dp)) {
                                    val guidance = when (pollen.name) {
                                        "Grass Pollen" -> "Peaks on warm windy afternoons. Shower and rinse hair after extended outdoor exposure."
                                        "Birch Pollen" -> "Tree pollens circulate heavily in early morning. Keep bedroom windows shut overnight."
                                        "Olive Pollen" -> "High allergenicity during late spring. Use sunglasses to protect eyes from airborne grains."
                                        "Ragweed Pollen" -> "Late summer irritant. Keep vehicular air conditioning set to recirculate mode."
                                        else -> "Airborne allergen count tracked via European pollen dispersion sensor network."
                                    }
                                    Text(
                                        text = guidance,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = InkMuted,
                                            fontSize = 10.sp,
                                            lineHeight = 14.sp
                                        )
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
