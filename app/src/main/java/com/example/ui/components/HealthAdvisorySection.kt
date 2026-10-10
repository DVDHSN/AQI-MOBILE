package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.animation.bounceClickable
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AqiLevel
import com.example.ui.theme.*

@Composable
fun HealthAdvisorySection(
    level: AqiLevel,
    modifier: Modifier = Modifier
) {
    var selectedRow by remember { mutableStateOf<String?>(null) }

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
            .testTag("health_advisory_section")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Health Guidelines",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = InkWhite,
                    fontSize = 15.sp
                )
            )

            Text(
                text = "Tap for details",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = InkFaint,
                    fontSize = 10.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AdvisoryRow(
                icon = Icons.AutoMirrored.Filled.DirectionsRun,
                title = "Outdoor Exercise",
                advice = level.exerciseAdvice,
                tip = "Morning hours typically feature lowest particulate density. Avoid strenuous aerobic workouts during midday heat when secondary ozone builds.",
                isSelected = selectedRow == "exercise",
                onClick = { selectedRow = if (selectedRow == "exercise") null else "exercise" }
            )

            AdvisoryRow(
                icon = Icons.Default.Home,
                title = "Indoor Ventilation",
                advice = level.ventilationAdvice,
                tip = "Operate air purifiers with true HEPA filtration. When outdoor AQI exceeds 100, keep windows sealed and recirculate internal airflow.",
                isSelected = selectedRow == "ventilation",
                onClick = { selectedRow = if (selectedRow == "ventilation") null else "ventilation" }
            )

            AdvisoryRow(
                icon = Icons.Default.Shield,
                title = "Personal Protection",
                advice = level.maskAdvice,
                tip = "Cloth and surgical masks cannot capture sub-micron PM2.5 particulates. Choose certified N95, KN95, or FFP2 respirators with complete facial seal.",
                isSelected = selectedRow == "mask",
                onClick = { selectedRow = if (selectedRow == "mask") null else "mask" }
            )
        }
    }
}

@Composable
private fun AdvisoryRow(
    icon: ImageVector,
    title: String,
    advice: String,
    tip: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkElevated)
            .then(
                if (isSelected) {
                    Modifier.border(1.dp, EditorialBlue.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                } else Modifier
            )
            .bounceClickable(scaleDown = 0.97f) { onClick() }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) EditorialBlue else InkMuted,
                    modifier = Modifier.size(15.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = InkWhite,
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = advice,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = InkMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                )
            }
        }

        AnimatedVisibility(visible = isSelected) {
            Column(modifier = Modifier.padding(top = 8.dp, start = 38.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(HairlineBorder)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = EditorialBlue,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                )
            }
        }
    }
}
