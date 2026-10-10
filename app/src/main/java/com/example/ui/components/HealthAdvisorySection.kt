package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            .background(DarkSurface)
            .border(1.dp, HairlineBorder)
            .padding(16.dp)
            .testTag("health_advisory_section")
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "EXPOSURE PROTOCOLS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp,
                        color = InkFaint
                    )
                )
                Text(
                    text = "ACTIONABLE HEALTH ADVISORY",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = InkWhite,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Box(
                modifier = Modifier
                    .border(1.dp, level.color)
                    .background(PitchBlack)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = level.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = level.color,
                        letterSpacing = 0.8.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Advisory Rows
        AdvisoryItem(
            category = "01 // OUTDOOR EXERCISE",
            title = "Physical Exertion",
            guidance = level.exerciseAdvice
        )

        Spacer(modifier = Modifier.height(10.dp))

        AdvisoryItem(
            category = "02 // HOME VENTILATION",
            title = "Indoor Air Exchange",
            guidance = level.ventilationAdvice
        )

        Spacer(modifier = Modifier.height(10.dp))

        AdvisoryItem(
            category = "03 // RESPIRATORY MASK",
            title = "Personal Protection",
            guidance = level.maskAdvice
        )

        Spacer(modifier = Modifier.height(10.dp))

        AdvisoryItem(
            category = "04 // VULNERABLE POPULATIONS",
            title = "Demographic Alert",
            guidance = level.detailedAdvisory
        )
    }
}

@Composable
private fun AdvisoryItem(
    category: String,
    title: String,
    guidance: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkElevated)
            .border(1.dp, HairlineBorder)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = EditorialBlue,
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp
                )
            )
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = InkFaint,
                    fontSize = 10.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = guidance,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = InkMuted,
                lineHeight = 18.sp
            )
        )
    }
}
