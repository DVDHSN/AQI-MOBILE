package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
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
import com.example.ui.animation.bounceClick
import com.example.ui.theme.*

/**
 * Atmospheric Drivers Section redesigned with Apple design tokens.
 * Near-Black Tile 2 (#2a2a2c), {rounded.lg} (18.dp), Action Blue #0066cc.
 */
@Composable
fun AtmosphericDriversSection(
    drivers: AtmosphericDrivers,
    modifier: Modifier = Modifier
) {
    val driver = drivers.dominantDriver

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(AppleCardBackground)
            .border(0.5.dp, AppleBorder, RoundedCornerShape(22.dp))
            .bounceClick { /* tactile inspection */ }
            .padding(20.dp)
            .testTag("atmospheric_drivers_section")
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
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = AppleActionBlue,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "Primary Atmospheric Driver",
                    style = TypographyBodyStrong.copy(fontSize = 14.sp, color = AppleTextPrimary)
                )
            }

            Text(
                text = "${driver.formula} · ${driver.relativeSharePercent}%",
                style = TypographyCaptionStrong.copy(color = AppleSkyBlue, fontSize = 12.sp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Animated Share Bar
        val animatedShare by animateFloatAsState(
            targetValue = (driver.relativeSharePercent / 100f).coerceIn(0.05f, 1f),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "driverShareProgress"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(9999.dp))
                .background(AppleCardSecondary)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedShare)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(9999.dp))
                    .background(AppleActionBlue)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "${driver.name} is currently the primary determinant of local ambient air quality indices.",
            style = TypographyFinePrint.copy(color = BodyMuted, fontSize = 12.sp)
        )
    }
}
