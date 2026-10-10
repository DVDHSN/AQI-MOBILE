package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AtmosphericDrivers
import com.example.ui.animation.bounceClickable
import com.example.ui.theme.*

@Composable
fun AtmosphericDriversSection(
    drivers: AtmosphericDrivers,
    modifier: Modifier = Modifier
) {
    val driver = drivers.dominantDriver

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, HairlineBorder, RoundedCornerShape(16.dp))
            .bounceClickable(scaleDown = 0.98f) { /* tactile inspection */ }
            .padding(16.dp)
            .testTag("atmospheric_drivers_section")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = EditorialBlue,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Primary Pollutant",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = InkWhite,
                        fontSize = 14.sp
                    )
                )
            }

            Text(
                text = "${driver.formula} · ${driver.relativeSharePercent}% of index",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = EditorialBlue,
                    fontSize = 12.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Animated Share Bar
        val animatedShare by androidx.compose.animation.core.animateFloatAsState(
            targetValue = (driver.relativeSharePercent / 100f).coerceIn(0.05f, 1f),
            animationSpec = androidx.compose.animation.core.spring(
                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow
            ),
            label = "driverShareProgress"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(PitchBlack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedShare)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(EditorialBlue)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = driver.impactAssessment,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = InkMuted,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        )
    }
}
