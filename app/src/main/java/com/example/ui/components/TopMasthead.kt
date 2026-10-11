package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppMode
import com.example.data.model.LocationItem
import com.example.ui.animation.bounceClick
import com.example.ui.icons.LucideIcons
import com.example.ui.theme.*

/**
 * Classic Apple Attached Top Navigation Bar with sliding segmented control ("Overview" | "Compare").
 * Permanently anchored at the top of the viewport with frosted black background and hairline divider.
 */
@Composable
fun ClassicAppleNavBar(
    appMode: AppMode,
    onSelectAppMode: (AppMode) -> Unit,
    activeLocation: LocationItem? = null,
    isRefreshing: Boolean = false,
    isLocating: Boolean = false,
    onRefresh: () -> Unit = {},
    onRequestCurrentLocation: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "refreshSpin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AppleSystemBackground)
            .border(width = 0.5.dp, color = AppleBorder, shape = RoundedCornerShape(0.dp))
    ) {
        // Main Classic Attached Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Clean brand mark
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(AppleActionBlue)
                )
                Text(
                    text = "Atmosphere",
                    style = TypographyTagline.copy(
                        fontSize = 18.sp,
                        letterSpacing = (-0.3).sp,
                        color = AppleTextPrimary
                    ),
                    modifier = Modifier.testTag("masthead_title")
                )
            }

            // Center: Classic Attached Apple Segmented Control with animated sliding pill
            AppleSlidingSegmentedControl(
                items = AppMode.entries,
                selectedItem = appMode,
                onItemSelected = onSelectAppMode,
                height = 32.dp,
                modifier = Modifier
                    .width(170.dp)
                    .testTag("attached_nav_segmented_control"),
                itemLabel = { mode -> if (mode == AppMode.DISPATCH) "Overview" else "Compare" }
            )

            // Right: Apple Action Icons (Search & Refresh)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Search Pill / Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AppleCardBackground)
                        .border(0.5.dp, AppleBorder, CircleShape)
                        .bounceClick { onOpenSearch() }
                        .testTag("search_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = LucideIcons.Search,
                        contentDescription = "Search Cities",
                        tint = AppleTextPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Refresh Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AppleCardBackground)
                        .border(0.5.dp, AppleBorder, CircleShape)
                        .bounceClick { onRefresh() }
                        .testTag("refresh_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = LucideIcons.RefreshCw,
                        contentDescription = "Sync Telemetry",
                        tint = AppleTextPrimary,
                        modifier = Modifier
                            .size(14.dp)
                            .then(if (isRefreshing) Modifier.graphicsLayer { rotationZ = rotation } else Modifier)
                    )
                }
            }
        }
    }
}

/**
 * Backward compatibility alias for TopMasthead / AppleTopNavBar
 */
@Composable
fun AppleTopNavBar(
    appMode: AppMode,
    onSelectAppMode: (AppMode) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    ClassicAppleNavBar(
        appMode = appMode,
        onSelectAppMode = onSelectAppMode,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    )
}

@Composable
fun TopMasthead(
    appMode: AppMode,
    onSelectAppMode: (AppMode) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    ClassicAppleNavBar(
        appMode = appMode,
        onSelectAppMode = onSelectAppMode,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    )
}
