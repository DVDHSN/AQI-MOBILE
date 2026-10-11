package com.example.ui.animation

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import kotlin.math.cos
import kotlin.math.sin

/**
 * Shared motion physics tokens for consistent tactile feel across the application.
 */
object AtmosphereSprings {
    val Tactile = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    val Snappy = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val Gentle = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
}

// bounceClickable and bounceClick are defined in BounceClickModifier.kt

/**
 * Draw-phase atmospheric breathing glow ring around components without triggering recompositions.
 */
fun Modifier.atmosphericAura(
    color: Color,
    minAlpha: Float = 0.12f,
    maxAlpha: Float = 0.28f,
    durationMillis: Int = 2400
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "atmosphericAuraTransition")
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraAlpha"
    )

    this.drawBehind {
        val radius = size.maxDimension / 2f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = animatedAlpha),
                    color.copy(alpha = animatedAlpha * 0.4f),
                    Color.Transparent
                ),
                center = center,
                radius = radius * 1.25f
            ),
            radius = radius * 1.25f,
            center = center
        )
    }
}

/**
 * Draw-phase procedural floating atmospheric air particles.
 * Floats 8 ambient micro-particles using pure canvas math keyed off an infinite time value.
 * Zero heap allocations during active draw loops and zero recomposition overhead.
 */
fun Modifier.atmosphericParticles(
    color: Color,
    particleCount: Int = 7
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "particlesMotion")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2831853f, // 2 * PI
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particlePhase"
    )

    this.drawWithCache {
        onDrawBehind {
            val width = size.width
            val height = size.height
            if (width <= 0f || height <= 0f) return@onDrawBehind

            for (i in 0 until particleCount) {
                // Fixed deterministic pseudo-random seeds per particle
                val seedX = (i * 137.5f) % width
                val seedY = (i * 219.7f) % height
                val speed = 0.7f + (i % 3) * 0.4f
                val radius = 1.2f + (i % 3) * 0.8f

                // Orbital sine/cosine atmospheric drift
                val offsetX = sin(time * speed + i) * 22f
                val offsetY = cos(time * speed * 0.8f + i * 1.5f) * 16f
                val alphaOscillation = (sin(time * speed * 1.2f + i) + 1f) / 2f
                val particleAlpha = 0.15f + alphaOscillation * 0.35f

                val posX = (seedX + offsetX + width) % width
                val posY = (seedY + offsetY + height) % height

                drawCircle(
                    color = color.copy(alpha = particleAlpha),
                    radius = radius,
                    center = Offset(posX, posY)
                )

                // Micro aura for larger particles
                if (radius > 1.8f) {
                    drawCircle(
                        color = color.copy(alpha = particleAlpha * 0.25f),
                        radius = radius * 2.8f,
                        center = Offset(posX, posY)
                    )
                }
            }
        }
    }
}
