package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AllergenReport
import com.example.ui.animation.bounceClick
import com.example.ui.theme.*
import java.util.Locale

/**
 * Pollen & Allergen Index Card redesigned with Apple design tokens.
 * - Near-Black Tile 2 (#2a2a2c) surface
 * - 18dp corner radius
 * - Apple typography
 */
@Composable
fun PollenAllergenCard(
    report: AllergenReport,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "allergenArrowRotation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceTile2)
            .padding(18.dp)
            .animateContentSize()
            .testTag("pollen_allergen_card")
    ) {
        // Summary Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .bounceClick { isExpanded = !isExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceTile1),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = ActionBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column {
                    Text(
                        text = "Botanical Allergens",
                        style = TypographyBodyStrong.copy(fontSize = 15.sp, color = BodyOnDark)
                    )
                    Text(
                        text = "Dominant species: ${report.dominantPollen}",
                        style = TypographyCaption.copy(fontSize = 12.sp, color = BodyMuted)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(SurfaceTile1)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isExpanded) "Hide" else "Details",
                    style = TypographyButtonUtility.copy(color = BodyMuted, fontSize = 11.sp)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = BodyMuted,
                    modifier = Modifier
                        .size(14.dp)
                        .graphicsLayer { rotationZ = arrowRotation }
                )
            }
        }

        // Expanded Pollen Species Breakdown
        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                report.pollens.forEach { pollen ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(SurfaceTile1)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pollen.name,
                            style = TypographyBodyStrong.copy(fontSize = 13.sp, color = BodyOnDark)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = String.format(Locale.US, "%.1f grains/m³", pollen.countGrains),
                                style = TypographyBody.copy(fontSize = 13.sp, color = BodyMuted)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(pollen.risk.color.copy(alpha = 0.18f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = pollen.risk.label,
                                    style = TypographyFinePrint.copy(color = pollen.risk.color, fontSize = 10.sp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
