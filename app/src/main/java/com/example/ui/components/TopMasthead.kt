package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppMode
import com.example.ui.animation.bounceClickable
import com.example.ui.theme.*

@Composable
fun TopMasthead(
    appMode: AppMode,
    onSelectAppMode: (AppMode) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "refreshRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(PitchBlack)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Main Masthead Bar (Tuned for 6.3" phone viewports)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Atmosphere",
                style = MaterialTheme.typography.headlineMedium.copy(
                    letterSpacing = 0.5.sp,
                    color = InkWhite,
                    fontSize = 20.sp
                ),
                modifier = Modifier.testTag("masthead_title")
            )

            // Segmented Mode Switcher (Pill style with animated scale microinteractions)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurface)
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppMode.entries.forEach { mode ->
                    val isSelected = mode == appMode
                    val modeLabel = if (mode == AppMode.DISPATCH) "Overview" else "Compare"

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) DarkElevated else DarkSurface)
                            .bounceClickable(scaleDown = 0.95f) {
                                onSelectAppMode(mode)
                            }
                            .padding(horizontal = 11.dp, vertical = 5.dp)
                            .testTag("app_mode_${mode.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = modeLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) InkWhite else InkMuted,
                                fontSize = 11.sp,
                                letterSpacing = 0.3.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(3.dp))

                // Refresh Button inside header with tactile microinteraction
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .bounceClickable(
                            enabled = !isRefreshing,
                            scaleDown = 0.86f
                        ) {
                            onRefresh()
                        }
                        .testTag("refresh_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh data",
                        tint = if (isRefreshing) EditorialBlue else InkMuted,
                        modifier = Modifier
                            .size(15.dp)
                            .graphicsLayer { rotationZ = if (isRefreshing) rotation else 0f }
                    )
                }
            }
        }
    }
}
