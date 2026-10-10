package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurface)
            .border(1.dp, HairlineBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
            .testTag("health_advisory_section")
    ) {
        Text(
            text = "Health Guidelines",
            style = MaterialTheme.typography.titleMedium.copy(
                color = InkWhite,
                fontSize = 15.sp
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AdvisoryRow(
                icon = Icons.Default.DirectionsRun,
                title = "Outdoor Exercise",
                advice = level.exerciseAdvice
            )

            AdvisoryRow(
                icon = Icons.Default.Home,
                title = "Indoor Ventilation",
                advice = level.ventilationAdvice
            )

            AdvisoryRow(
                icon = Icons.Default.Shield,
                title = "Personal Protection",
                advice = level.maskAdvice
            )
        }
    }
}

@Composable
private fun AdvisoryRow(
    icon: ImageVector,
    title: String,
    advice: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkElevated)
            .padding(10.dp),
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
                tint = EditorialBlue,
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
}
