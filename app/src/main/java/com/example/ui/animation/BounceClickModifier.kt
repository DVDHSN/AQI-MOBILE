package com.example.ui.animation

import android.graphics.Color as AndroidColor
import android.graphics.Paint as AndroidPaint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Tactical spring feedback microinteraction token from Apple design system:
 * - Scales down to 0.95f on press using low-bouncy spring physics.
 * - Triggers native subtle haptics on tap.
 */
fun Modifier.bounceClick(
    enabled: Boolean = true,
    scaleDown: Float = 0.95f,
    onClick: () -> Unit
): Modifier = composed {
    if (!enabled) return@composed this

    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "appleBounceSpring"
    )

    val haptic = LocalHapticFeedback.current

    this
        .graphicsLayer {
            this.scaleX = animatedScale
            this.scaleY = animatedScale
        }
        .pointerInput(enabled) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                isPressed = true
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                val upOrCancel = waitForUpOrCancellation()
                isPressed = false
                if (upOrCancel != null) {
                    onClick()
                }
            }
        }
}

/**
 * Backward compatibility alias for bounceClick
 */
fun Modifier.bounceClickable(
    enabled: Boolean = true,
    scaleDown: Float = 0.95f,
    onClick: () -> Unit
): Modifier = bounceClick(enabled = enabled, scaleDown = scaleDown, onClick = onClick)

/**
 * Signature Apple product drop-shadow:
 * `rgba(0, 0, 0, 0.22) 3px 5px 30px`
 * Strictly reserved for photographic/3D hardware renders resting on a surface.
 */
fun Modifier.productDropShadow(
    blurRadius: Dp = 30.dp,
    offsetX: Dp = 3.dp,
    offsetY: Dp = 5.dp,
    cornerRadius: Dp = 18.dp
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = AndroidPaint().apply {
            isAntiAlias = true
            color = AndroidColor.TRANSPARENT
            setShadowLayer(
                blurRadius.toPx(),
                offsetX.toPx(),
                offsetY.toPx(),
                AndroidColor.argb((0.22f * 255).toInt(), 0, 0, 0)
            )
        }
        canvas.nativeCanvas.drawRoundRect(
            0f,
            0f,
            size.width,
            size.height,
            cornerRadius.toPx(),
            cornerRadius.toPx(),
            paint
        )
    }
}
