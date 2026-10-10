package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppMode
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
    ) {
        // Top Brand Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ATMOSPHERE",
                    style = MaterialTheme.typography.titleLarge.copy(
                        letterSpacing = 2.5.sp,
                        color = InkWhite,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.testTag("masthead_title")
                )
                Text(
                    text = "GLOBAL ENVIRONMENTAL DISPATCH // OPEN-METEO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.8.sp,
                        color = InkFaint,
                        fontSize = 9.sp
                    ),
                    maxLines = 1
                )
            }

            IconButton(
                onClick = onRefresh,
                enabled = !isRefreshing,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("refresh_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh data",
                    tint = if (isRefreshing) EditorialBlue else InkWhite,
                    modifier = Modifier.rotate(if (isRefreshing) rotation else 0f)
                )
            }
        }

        // Dedicated Mode Tabs Row (Full-width segmented control, zero horizontal squeeze)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 10.dp)
                .border(1.dp, HairlineBorder)
                .background(DarkSurface)
        ) {
            AppMode.entries.forEach { mode ->
                val isSelected = mode == appMode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (isSelected) InkWhite else DarkSurface)
                        .clickable { onSelectAppMode(mode) }
                        .padding(vertical = 8.dp)
                        .testTag("app_mode_${mode.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode.title,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = if (isSelected) PitchBlack else InkMuted,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        ),
                        maxLines = 1
                    )
                }
            }
        }

        // Hairline divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(HairlineBorder)
        )
    }
}
