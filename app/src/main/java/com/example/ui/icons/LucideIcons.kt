package com.example.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Lucide & SF Symbols style minimalist vector icons for Apple UI.
 * 24x24 viewport with 1.8dp crisp hairline strokes.
 */
object LucideIcons {

    val Wind: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideWind",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(17.7f, 7.7f)
                arcTo(2.5f, 2.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, x1 = 19.5f, y1 = 12f)
                horizontalLineTo(2f)
                moveTo(14.7f, 16.3f)
                arcTo(2.5f, 2.5f, 0f, isMoreThanHalf = true, isPositiveArc = false, x1 = 16.5f, y1 = 20f)
                horizontalLineTo(2f)
                moveTo(9.7f, 4f)
                arcTo(2.5f, 2.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, x1 = 11.5f, y1 = 8f)
                horizontalLineTo(2f)
            }
        }.build()
    }

    val Activity: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideActivity",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(22f, 12f)
                lineTo(18f, 12f)
                lineTo(15f, 21f)
                lineTo(9f, 3f)
                lineTo(6f, 12f)
                lineTo(2f, 12f)
            }
        }.build()
    }

    val Shield: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideShield",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 22f)
                curveTo(12f, 22f, 20f, 18f, 20f, 12f)
                verticalLineTo(5f)
                lineTo(12f, 2f)
                lineTo(4f, 5f)
                verticalLineTo(12f)
                curveTo(4f, 18f, 12f, 22f, 12f, 22f)
                close()
            }
        }.build()
    }

    val ShieldCheck: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideShieldCheck",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 22f)
                curveTo(12f, 22f, 20f, 18f, 20f, 12f)
                verticalLineTo(5f)
                lineTo(12f, 2f)
                lineTo(4f, 5f)
                verticalLineTo(12f)
                curveTo(4f, 18f, 12f, 22f, 12f, 22f)
                close()
                moveTo(9f, 12f)
                lineTo(11f, 14f)
                lineTo(15f, 10f)
            }
        }.build()
    }

    val Droplets: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideDroplets",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(7f, 16.3f)
                curveTo(4.4f, 16.3f, 2.5f, 14.4f, 2.5f, 12f)
                curveTo(2.5f, 9.6f, 7f, 3.5f, 7f, 3.5f)
                curveTo(7f, 3.5f, 11.5f, 9.6f, 11.5f, 12f)
                curveTo(11.5f, 14.4f, 9.6f, 16.3f, 7f, 16.3f)
                close()
                moveTo(16.5f, 20.5f)
                curveTo(14.6f, 20.5f, 13f, 18.9f, 13f, 17f)
                curveTo(13f, 15f, 16.5f, 10f, 16.5f, 10f)
                curveTo(16.5f, 10f, 20f, 15f, 20f, 17f)
                curveTo(20f, 18.9f, 18.4f, 20.5f, 16.5f, 20.5f)
                close()
            }
        }.build()
    }

    val Gauge: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideGauge",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 15f)
                lineTo(15f, 10f)
                moveTo(3.34f, 18.5f)
                arcTo(10f, 10f, 0f, isMoreThanHalf = true, isPositiveArc = true, x1 = 20.66f, y1 = 18.5f)
            }
        }.build()
    }

    val Sparkles: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideSparkles",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(9.937f, 15.5f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, x1 = 8.5f, y1 = 14.063f)
                lineTo(2.328f, 12.336f)
                arcTo(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, x1 = 2.328f, y1 = 11.664f)
                lineTo(8.5f, 9.937f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, x1 = 9.937f, y1 = 8.5f)
                lineTo(11.664f, 2.328f)
                arcTo(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, x1 = 12.336f, y1 = 2.328f)
                lineTo(14.063f, 8.5f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, x1 = 15.5f, y1 = 9.937f)
                lineTo(21.672f, 11.664f)
                arcTo(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, x1 = 21.672f, y1 = 12.336f)
                lineTo(15.5f, 14.063f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, x1 = 14.063f, y1 = 15.5f)
                lineTo(12.336f, 21.672f)
                arcTo(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, x1 = 11.664f, y1 = 21.672f)
                close()
            }
        }.build()
    }

    val Sliders: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideSliders",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(4f, 21f)
                verticalLineTo(14f)
                moveTo(4f, 10f)
                verticalLineTo(3f)
                moveTo(12f, 21f)
                verticalLineTo(12f)
                moveTo(12f, 8f)
                verticalLineTo(3f)
                moveTo(20f, 21f)
                verticalLineTo(16f)
                moveTo(20f, 12f)
                verticalLineTo(3f)
                moveTo(1f, 14f)
                horizontalLineTo(7f)
                moveTo(9f, 8f)
                horizontalLineTo(15f)
                moveTo(17f, 16f)
                horizontalLineTo(23f)
            }
        }.build()
    }

    val MapPin: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideMapPin",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(20f, 10f)
                curveTo(20f, 16f, 12f, 22f, 12f, 22f)
                curveTo(12f, 22f, 4f, 16f, 4f, 10f)
                arcTo(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, x1 = 20f, y1 = 10f)
                close()
                moveTo(12f, 13f)
                arcTo(3f, 3f, 0f, isMoreThanHalf = true, isPositiveArc = true, x1 = 12f, y1 = 7f)
                arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, x1 = 12f, y1 = 13f)
                close()
            }
        }.build()
    }

    val TrendingUp: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideTrendingUp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(23f, 6f)
                lineTo(13.5f, 15.5f)
                lineTo(8.5f, 10.5f)
                lineTo(1f, 18f)
                moveTo(17f, 6f)
                horizontalLineTo(23f)
                verticalLineTo(12f)
            }
        }.build()
    }

    val Compare: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideCompare",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(20f, 8f)
                lineTo(4f, 8f)
                moveTo(16f, 4f)
                lineTo(20f, 8f)
                lineTo(16f, 12f)
                moveTo(4f, 16f)
                lineTo(20f, 16f)
                moveTo(8f, 12f)
                lineTo(4f, 16f)
                lineTo(8f, 20f)
            }
        }.build()
    }

    val Search: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideSearch",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(11f, 19f)
                arcTo(8f, 8f, 0f, isMoreThanHalf = true, isPositiveArc = true, x1 = 11f, y1 = 3f)
                arcTo(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, x1 = 11f, y1 = 19f)
                close()
                moveTo(21f, 21f)
                lineTo(16.65f, 16.65f)
            }
        }.build()
    }

    val Navigation: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideNavigation",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 11f)
                lineTo(22f, 2f)
                lineTo(13f, 21f)
                lineTo(11f, 13f)
                close()
            }
        }.build()
    }

    val RefreshCw: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideRefreshCw",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(21f, 2f)
                verticalLineTo(7f)
                horizontalLineTo(16f)
                moveTo(3f, 22f)
                verticalLineTo(17f)
                horizontalLineTo(8f)
                moveTo(20.49f, 9f)
                arcTo(9f, 9f, 0f, isMoreThanHalf = false, isPositiveArc = false, x1 = 5.64f, y1 = 5.64f)
                lineTo(3f, 7f)
                moveTo(3.51f, 15f)
                arcTo(9f, 9f, 0f, isMoreThanHalf = false, isPositiveArc = false, x1 = 18.36f, y1 = 18.36f)
                lineTo(21f, 17f)
            }
        }.build()
    }

    val Check: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideCheck",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(20f, 6f)
                lineTo(9f, 17f)
                lineTo(4f, 12f)
            }
        }.build()
    }

    val X: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideX",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(18f, 6f)
                lineTo(6f, 18f)
                moveTo(6f, 6f)
                lineTo(18f, 18f)
            }
        }.build()
    }

    val Bookmark: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideBookmark",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(19f, 21f)
                lineTo(12f, 16f)
                lineTo(5f, 21f)
                verticalLineTo(5f)
                curveTo(5f, 3.9f, 5.9f, 3f, 7f, 3f)
                horizontalLineTo(17f)
                curveTo(18.1f, 3f, 19f, 3.9f, 19f, 5f)
                close()
            }
        }.build()
    }

    val BookmarkFilled: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideBookmarkFilled",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White)
            ) {
                moveTo(19f, 21f)
                lineTo(12f, 16f)
                lineTo(5f, 21f)
                verticalLineTo(5f)
                curveTo(5f, 3.9f, 5.9f, 3f, 7f, 3f)
                horizontalLineTo(17f)
                curveTo(18.1f, 3f, 19f, 3.9f, 19f, 5f)
                close()
            }
        }.build()
    }
}
