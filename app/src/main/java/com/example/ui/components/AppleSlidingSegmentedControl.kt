package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.animation.bounceClick
import com.example.ui.theme.*

/**
 * Signature Apple UISegmentedControl with fluid sliding pill spring physics.
 * The selection pill smoothly glides/moves through the track when changing tabs.
 */
@Composable
fun <T> AppleSlidingSegmentedControl(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 34.dp,
    trackColor: Color = AppleCardBackground,
    indicatorColor: Color = AppleCardSecondary,
    selectedTextColor: Color = AppleTextPrimary,
    unselectedTextColor: Color = AppleTextSecondary,
    itemLabel: (T) -> String = { it.toString() }
) {
    val selectedIndex = items.indexOf(selectedItem).coerceAtLeast(0)
    AppleSlidingSegmentedControl(
        items = items,
        selectedIndex = selectedIndex,
        onItemSelected = { index -> onItemSelected(items[index]) },
        modifier = modifier,
        height = height,
        trackColor = trackColor,
        indicatorColor = indicatorColor,
        selectedTextColor = selectedTextColor,
        unselectedTextColor = unselectedTextColor,
        label = itemLabel
    )
}

@Composable
fun <T> AppleSlidingSegmentedControl(
    items: List<T>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 34.dp,
    trackColor: Color = AppleCardBackground,
    indicatorColor: Color = AppleCardSecondary,
    selectedTextColor: Color = AppleTextPrimary,
    unselectedTextColor: Color = AppleTextSecondary,
    label: (T) -> String = { it.toString() }
) {
    val haptic = LocalHapticFeedback.current
    val safeSelectedIndex = selectedIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0))

    BoxWithConstraints(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(9999.dp))
            .background(trackColor)
            .border(0.5.dp, AppleBorder, RoundedCornerShape(9999.dp))
            .padding(2.dp)
    ) {
        val totalWidth = maxWidth
        val count = items.size.coerceAtLeast(1)
        val itemWidth = totalWidth / count

        // Fluid spring sliding pill indicator
        val targetX = itemWidth * safeSelectedIndex
        val animatedX by animateDpAsState(
            targetValue = targetX,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "slidingPillOffset"
        )

        // Animated moving highlight pill
        Box(
            modifier = Modifier
                .width(itemWidth)
                .fillMaxHeight()
                .offset(x = animatedX)
                .clip(RoundedCornerShape(9999.dp))
                .background(indicatorColor)
                .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(9999.dp))
        )

        // Interactive labels layer
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == safeSelectedIndex
                val animatedTextColor by animateColorAsState(
                    targetValue = if (isSelected) selectedTextColor else unselectedTextColor,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                    label = "textColor_$index"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(9999.dp))
                        .bounceClick(scaleDown = 0.94f) {
                            if (!isSelected) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onItemSelected(index)
                            }
                        }
                        .testTag("segmented_option_${label(item)}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label(item),
                        style = TypographyCaptionStrong.copy(
                            color = animatedTextColor,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}
