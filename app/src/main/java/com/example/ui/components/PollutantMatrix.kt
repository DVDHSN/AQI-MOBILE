package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrentAirQuality
import com.example.ui.theme.*
import java.util.Locale

private data class PollutantItem(
    val formula: String,
    val name: String,
    val value: Float,
    val unit: String,
    val threshold: Float
)

@Composable
fun PollutantMatrix(
    current: CurrentAirQuality,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedFormula by remember { mutableStateOf<String?>(null) }

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pollutantChevronRotation"
    )

    val items = remember(current) {
        listOf(
            PollutantItem("PM2.5", "Fine particles", current.pm25, "μg/m³", 15f),
            PollutantItem("PM10", "Inhalable dust", current.pm10, "μg/m³", 45f),
            PollutantItem("O₃", "Ozone", current.ozone, "μg/m³", 100f),
            PollutantItem("NO₂", "Nitrogen dioxide", current.nitrogenDioxide, "μg/m³", 25f),
            PollutantItem("SO₂", "Sulfur dioxide", current.sulphurDioxide, "μg/m³", 40f),
            PollutantItem("CO", "Carbon monoxide", current.carbonMonoxide, "μg/m³", 4000f)
        )
    }
    val chunkedItems = remember(items) { items.chunked(2) }

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
            .testTag("pollutant_matrix_section")
    ) {
        // Expandable Header Row (Clickable toggle, min 48dp interactive size)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isExpanded = !isExpanded
                }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Pollutants",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = InkWhite,
                        fontSize = 15.sp
                    )
                )

                // Subtitle preview when collapsed vs expanded
                Text(
                    text = if (isExpanded) {
                        "Concentration levels (μg/m³)"
                    } else {
                        "PM2.5: ${String.format(Locale.US, "%.1f", current.pm25)} · O₃: ${String.format(Locale.US, "%.1f", current.ozone)}"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isExpanded) InkFaint else EditorialBlue,
                        fontSize = 11.sp
                    ),
                    maxLines = 1
                )
            }

            // Expand / Collapse Pill + Chevron with tactile microinteraction
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
                    contentDescription = if (isExpanded) "Collapse pollutants" else "Expand pollutants",
                    tint = InkMuted,
                    modifier = Modifier
                        .size(15.dp)
                        .graphicsLayer { rotationZ = chevronRotation }
                )
            }
        }

        // Collapsed Compact Snapshot Strip (Saves vertical space on 6.3" screens)
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
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.take(3).forEach { item ->
                    val ratio = if (item.threshold > 0f) item.value / item.threshold else 0f
                    val statusColor = when {
                        ratio <= 1.0f -> AqiGood
                        ratio <= 2.0f -> AqiModerate
                        ratio <= 3.5f -> AqiSensitive
                        else -> AqiUnhealthy
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Text(
                            text = item.formula,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = InkMuted,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = String.format(Locale.US, "%.1f", item.value),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = InkWhite,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Text(
                    text = "+3 more",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = InkFaint,
                        fontSize = 9.sp
                    )
                )
            }
        }

        // Expanded Detailed Grid of 6 Pollutant Cards
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

                // 2-column grid tuned for 6.3" phone widths (380-410dp)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    chunkedItems.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowItems.forEach { item ->
                                val isCardSelected = selectedFormula == item.formula
                                PollutantCard(
                                    item = item,
                                    isSelected = isCardSelected,
                                    onClick = {
                                        selectedFormula = if (isCardSelected) null else item.formula
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PollutantCard(
    item: PollutantItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ratio = if (item.threshold > 0f) item.value / item.threshold else 0f
    val statusColor = when {
        ratio <= 1.0f -> AqiGood
        ratio <= 2.0f -> AqiModerate
        ratio <= 3.5f -> AqiSensitive
        else -> AqiUnhealthy
    }

    // Microinteraction: animated scale and border on tap/selection
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.025f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pollutantCardScale"
    )

    val borderAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.6f else 0.0f,
        label = "pollutantCardBorder"
    )

    Row(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(10.dp))
            .background(DarkElevated)
            .then(
                if (isSelected) {
                    Modifier.border(
                        1.dp,
                        statusColor.copy(alpha = borderAlpha),
                        RoundedCornerShape(10.dp)
                    )
                } else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag("pollutant_card_${item.formula}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Text(
                    text = item.formula,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = InkWhite,
                        fontSize = 12.sp
                    )
                )
            }
            Text(
                text = item.name,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = InkFaint,
                    fontSize = 9.sp
                ),
                maxLines = 1
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = String.format(Locale.US, "%.1f", item.value),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = InkWhite,
                    fontSize = 13.sp
                )
            )
            Text(
                text = item.unit,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = InkFaint,
                    fontSize = 8.sp
                )
            )
        }
    }
}
