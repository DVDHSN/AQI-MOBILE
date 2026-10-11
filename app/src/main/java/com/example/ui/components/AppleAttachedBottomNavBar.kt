package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppMode
import com.example.ui.animation.bounceClick
import com.example.ui.icons.LucideIcons
import com.example.ui.theme.*

enum class AppleNavTab {
    OVERVIEW,
    COMPARE,
    STATIONS
}

/**
 * Classic Apple iOS Attached Bottom Navigation Bar (Matching user's reference image).
 * Attached to the bottom of the viewport with blur/dark styling, icons on top, labels below,
 * spring scaling on selection, and native haptics.
 */
@Composable
fun AppleAttachedBottomNavBar(
    currentTab: AppleNavTab,
    onSelectTab: (AppleNavTab) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    val infiniteTransition = rememberInfiniteTransition(label = "refreshSpin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(AppleCardBackground.copy(alpha = 0.96f))
            .border(
                width = 0.5.dp,
                color = AppleBorder,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .navigationBarsPadding()
            .height(58.dp)
            .testTag("apple_attached_bottom_nav_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // 1. Overview Tab
            AppleBottomNavItem(
                icon = LucideIcons.Gauge,
                label = "Overview",
                isSelected = currentTab == AppleNavTab.OVERVIEW,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelectTab(AppleNavTab.OVERVIEW)
                },
                modifier = Modifier.weight(1f)
            )

            // 2. Compare Tab
            AppleBottomNavItem(
                icon = LucideIcons.Compare,
                label = "Compare",
                isSelected = currentTab == AppleNavTab.COMPARE,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelectTab(AppleNavTab.COMPARE)
                },
                modifier = Modifier.weight(1f)
            )

            // 3. Stations Search Tab
            AppleBottomNavItem(
                icon = LucideIcons.MapPin,
                label = "Stations",
                isSelected = currentTab == AppleNavTab.STATIONS,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelectTab(AppleNavTab.STATIONS)
                },
                modifier = Modifier.weight(1f)
            )

            // 4. Quick Refresh Button
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .bounceClick {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onRefresh()
                    }
                    .testTag("bottom_nav_refresh"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Sync",
                    tint = if (isRefreshing) AppleActionBlue else AppleTextSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .then(if (isRefreshing) Modifier.graphicsLayer { rotationZ = rotation } else Modifier)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Sync",
                    style = TypographyFinePrint.copy(
                        fontSize = 10.5.sp,
                        color = if (isRefreshing) AppleActionBlue else AppleTextSecondary
                    )
                )
            }
        }
    }
}

@Composable
private fun AppleBottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Spring scaling on active selection
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.12f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "navItemScale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) AppleActionBlue else AppleTextSecondary,
        animationSpec = tween(180),
        label = "navIconColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) AppleTextPrimary else AppleTextSecondary,
        animationSpec = tween(180),
        label = "navTextColor"
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .bounceClick(scaleDown = 0.92f) { onClick() }
            .testTag("bottom_nav_${label.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier
                .size(21.dp)
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                }
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            style = TypographyFinePrint.copy(
                fontSize = 10.5.sp,
                color = textColor,
                fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.SemiBold else androidx.compose.ui.text.font.FontWeight.Normal
            )
        )
    }
}
