package zhiqiu.iztro.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun SurroundedLine(
    index: Int,
    scope: String?,
    modifier: Modifier = Modifier,
) {
    if (index < 0) return

    val strokeColor = scope?.let { IztroTheme.scopeColor(it).copy(alpha = 0.5f) }
        ?: Color(0xFFF50000).copy(alpha = 0.5f)

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width / 2
        val h = size.height / 2
        val points = listOf(
            Offset(0f, h * 2),
            Offset(0f, h * 1.5f),
            Offset(0f, h * 0.5f),
            Offset(0f, 0f),
            Offset(w * 0.5f, 0f),
            Offset(w * 1.5f, 0f),
            Offset(w * 2, 0f),
            Offset(w * 2, h * 0.5f),
            Offset(w * 2, h * 1.5f),
            Offset(w * 2, h * 2),
            Offset(w * 1.5f, h * 2),
            Offset(w * 0.5f, h * 2),
        )

        fun fixIndex(i: Int) = ((i % 12) + 12) % 12
        val dgIdx = fixIndex(index + 6)
        val q4Idx = fixIndex(index + 4)
        val h4Idx = fixIndex(index - 4)

        val path = Path().apply {
            moveTo(points[dgIdx].x, points[dgIdx].y)
            lineTo(points[index].x, points[index].y)
            lineTo(points[q4Idx].x, points[q4Idx].y)
            lineTo(points[h4Idx].x, points[h4Idx].y)
            lineTo(points[index].x, points[index].y)
        }
        drawPath(path, strokeColor, style = Stroke(width = 2f))
    }
}
