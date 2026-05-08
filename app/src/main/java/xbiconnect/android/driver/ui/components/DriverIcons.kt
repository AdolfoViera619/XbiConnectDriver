package xbiconnect.android.driver.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Line icons port from the React design (icons.jsx). Each icon is drawn at a
 * 24x24 reference grid; the Canvas auto-scales to the requested [size].
 */
@Composable
fun DriverIcon(
    name: DriverIconName,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    color: Color = Color.Black,
    strokeWidth: Dp = 2.25.dp,
) {
    val swPx = with(LocalDensity.current) { strokeWidth.toPx() }
    val path = remember(name) { buildPath(name) }
    val fill = remember(name) { buildFillPath(name) }
    Canvas(modifier = modifier.then(Modifier.size(size))) {
        val k = this.size.minDimension / 24f
        scale(k, pivot = androidx.compose.ui.geometry.Offset.Zero) {
            if (fill != null) drawPath(fill, color = color)
            drawPath(
                path,
                color = color,
                style = Stroke(width = swPx / k, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }
    }
}

enum class DriverIconName {
    TRUCK, CHAT, INBOX, HOME, MAP, SETTINGS, USER, USERS, BELL, ALERT,
    MIC, SEND, CHECK, CHECK_ALL, CHEV_R, CHEV_L, CHEV_D, ARROW_R, ARROW_L,
    PLUS, X, SEARCH, CLOCK, MOON, SUN, PIN, FUEL, INFO, BED, WIFI, BATTERY,
    SIGNAL, SWAP, LOCK, PLAY, PAUSE, FLAG, QMARK, WEIGHT, PKG,
}

private fun rectPath(p: Path, x: Float, y: Float, w: Float, h: Float, rx: Float = 0f) {
    if (rx > 0f) p.addRoundRect(RoundRect(x, y, x + w, y + h, CornerRadius(rx, rx)))
    else p.addRect(Rect(x, y, x + w, y + h))
}

private fun circlePath(p: Path, cx: Float, cy: Float, r: Float) {
    p.addOval(Rect(cx - r, cy - r, cx + r, cy + r))
}

private fun buildFillPath(name: DriverIconName): Path? {
    val p = Path()
    when (name) {
        DriverIconName.SIGNAL -> {
            rectPath(p, 2f, 18f, 2f, 4f)
            rectPath(p, 7f, 14f, 2f, 8f)
            rectPath(p, 12f, 10f, 2f, 12f)
            rectPath(p, 17f, 6f, 2f, 16f)
            return p
        }
        DriverIconName.PLAY -> {
            p.moveTo(5f, 3f); p.lineTo(19f, 12f); p.lineTo(5f, 21f); p.close()
            return p
        }
        DriverIconName.PAUSE -> {
            rectPath(p, 6f, 4f, 4f, 16f)
            rectPath(p, 14f, 4f, 4f, 16f)
            return p
        }
        DriverIconName.BATTERY -> {
            rectPath(p, 3f, 8f, 11f, 8f, 1f)
            return p
        }
        else -> return null
    }
}

private fun buildPath(name: DriverIconName): Path {
    val p = Path()
    when (name) {
        DriverIconName.TRUCK -> {
            p.moveTo(3f, 17f); p.lineTo(3f, 7f); p.lineTo(4f, 6f); p.lineTo(15f, 6f); p.lineTo(15f, 17f)
            p.moveTo(15f, 11f); p.lineTo(19f, 11f); p.lineTo(22f, 14f); p.lineTo(22f, 17f); p.lineTo(15f, 17f)
            circlePath(p, 7.5f, 17.5f, 2f)
            circlePath(p, 17.5f, 17.5f, 2f)
        }
        DriverIconName.CHAT -> {
            p.moveTo(21f, 15f); p.lineTo(21f, 5f); p.lineTo(20f, 3f); p.lineTo(5f, 3f); p.lineTo(3f, 5f); p.lineTo(3f, 21f); p.lineTo(7f, 17f); p.lineTo(19f, 17f); p.lineTo(21f, 15f)
        }
        DriverIconName.INBOX -> {
            p.moveTo(22f, 12f); p.lineTo(16f, 12f); p.lineTo(14f, 15f); p.lineTo(10f, 15f); p.lineTo(8f, 12f); p.lineTo(2f, 12f)
            p.moveTo(5.45f, 5.11f); p.lineTo(2f, 12f); p.lineTo(2f, 18f); p.lineTo(4f, 20f); p.lineTo(20f, 20f); p.lineTo(22f, 18f); p.lineTo(22f, 12f); p.lineTo(18.55f, 5.11f); p.lineTo(16.76f, 4f); p.lineTo(7.24f, 4f); p.close()
        }
        DriverIconName.HOME -> {
            p.moveTo(3f, 9f); p.lineTo(12f, 2f); p.lineTo(21f, 9f); p.lineTo(21f, 20f); p.lineTo(19f, 22f); p.lineTo(15f, 22f); p.lineTo(15f, 15f); p.lineTo(9f, 15f); p.lineTo(9f, 22f); p.lineTo(5f, 22f); p.lineTo(3f, 20f); p.close()
        }
        DriverIconName.MAP -> {
            p.moveTo(1f, 6f); p.lineTo(1f, 22f); p.lineTo(8f, 18f); p.lineTo(16f, 22f); p.lineTo(23f, 18f); p.lineTo(23f, 2f); p.lineTo(16f, 6f); p.lineTo(8f, 2f); p.close()
            p.moveTo(8f, 2f); p.lineTo(8f, 18f)
            p.moveTo(16f, 6f); p.lineTo(16f, 22f)
        }
        DriverIconName.SETTINGS -> {
            circlePath(p, 12f, 12f, 3f)
            circlePath(p, 12f, 12f, 9f)
        }
        DriverIconName.USER -> {
            circlePath(p, 12f, 8f, 4f)
            p.moveTo(6f, 21f); p.lineTo(6f, 19f); p.lineTo(10f, 15f); p.lineTo(14f, 15f); p.lineTo(18f, 19f); p.lineTo(18f, 21f)
        }
        DriverIconName.USERS -> {
            p.moveTo(16f, 21f); p.lineTo(16f, 19f); p.lineTo(12f, 15f); p.lineTo(6f, 15f); p.lineTo(2f, 19f); p.lineTo(2f, 21f)
            circlePath(p, 9f, 7f, 4f)
            p.moveTo(22f, 21f); p.lineTo(22f, 19f); p.lineTo(19f, 15.13f)
            p.moveTo(16f, 3.13f); p.lineTo(18f, 5f); p.lineTo(18f, 8.88f); p.lineTo(16f, 10.88f)
        }
        DriverIconName.BELL -> {
            p.moveTo(18f, 8f); p.lineTo(15f, 3f); p.lineTo(9f, 3f); p.lineTo(6f, 8f); p.lineTo(6f, 13f); p.lineTo(3f, 17f); p.lineTo(21f, 17f); p.lineTo(18f, 13f); p.close()
            p.moveTo(13.73f, 21f); p.lineTo(10.27f, 21f)
        }
        DriverIconName.ALERT -> {
            p.moveTo(10.29f, 3.86f); p.lineTo(1.82f, 18f); p.lineTo(3.53f, 21f); p.lineTo(20.47f, 21f); p.lineTo(22.18f, 18f); p.lineTo(13.71f, 3.86f); p.close()
            p.moveTo(12f, 9f); p.lineTo(12f, 13f)
            p.moveTo(12f, 17f); p.lineTo(12.01f, 17f)
        }
        DriverIconName.MIC -> {
            p.moveTo(12f, 2f); p.lineTo(9f, 5f); p.lineTo(9f, 12f); p.lineTo(12f, 15f); p.lineTo(15f, 12f); p.lineTo(15f, 5f); p.close()
            p.moveTo(19f, 10f); p.lineTo(19f, 12f); p.lineTo(12f, 19f); p.lineTo(5f, 12f); p.lineTo(5f, 10f)
            p.moveTo(12f, 19f); p.lineTo(12f, 23f)
        }
        DriverIconName.SEND -> {
            p.moveTo(22f, 2f); p.lineTo(11f, 13f)
            p.moveTo(22f, 2f); p.lineTo(15f, 22f); p.lineTo(11f, 13f); p.lineTo(2f, 9f); p.close()
        }
        DriverIconName.CHECK -> {
            p.moveTo(20f, 6f); p.lineTo(9f, 17f); p.lineTo(4f, 12f)
        }
        DriverIconName.CHECK_ALL -> {
            p.moveTo(18f, 7f); p.lineTo(9.5f, 15.5f); p.lineTo(6f, 12f)
            p.moveTo(22f, 7f); p.lineTo(13.5f, 15.5f); p.lineTo(12.5f, 14.5f)
        }
        DriverIconName.CHEV_R -> { p.moveTo(9f, 18f); p.lineTo(15f, 12f); p.lineTo(9f, 6f) }
        DriverIconName.CHEV_L -> { p.moveTo(15f, 18f); p.lineTo(9f, 12f); p.lineTo(15f, 6f) }
        DriverIconName.CHEV_D -> { p.moveTo(6f, 9f); p.lineTo(12f, 15f); p.lineTo(18f, 9f) }
        DriverIconName.ARROW_R -> {
            p.moveTo(5f, 12f); p.lineTo(19f, 12f)
            p.moveTo(12f, 5f); p.lineTo(19f, 12f); p.lineTo(12f, 19f)
        }
        DriverIconName.ARROW_L -> {
            p.moveTo(19f, 12f); p.lineTo(5f, 12f)
            p.moveTo(12f, 19f); p.lineTo(5f, 12f); p.lineTo(12f, 5f)
        }
        DriverIconName.PLUS -> {
            p.moveTo(12f, 5f); p.lineTo(12f, 19f)
            p.moveTo(5f, 12f); p.lineTo(19f, 12f)
        }
        DriverIconName.X -> {
            p.moveTo(18f, 6f); p.lineTo(6f, 18f)
            p.moveTo(6f, 6f); p.lineTo(18f, 18f)
        }
        DriverIconName.SEARCH -> {
            circlePath(p, 11f, 11f, 7f)
            p.moveTo(21f, 21f); p.lineTo(16.65f, 16.65f)
        }
        DriverIconName.CLOCK -> {
            circlePath(p, 12f, 12f, 10f)
            p.moveTo(12f, 6f); p.lineTo(12f, 12f); p.lineTo(16f, 14f)
        }
        DriverIconName.MOON -> {
            p.addOval(Rect(2f, 3f, 22f, 23f))
        }
        DriverIconName.SUN -> {
            circlePath(p, 12f, 12f, 4f)
            p.moveTo(12f, 2f); p.lineTo(12f, 4f)
            p.moveTo(12f, 20f); p.lineTo(12f, 22f)
            p.moveTo(4.93f, 4.93f); p.lineTo(6.34f, 6.34f)
            p.moveTo(17.66f, 17.66f); p.lineTo(19.07f, 19.07f)
            p.moveTo(2f, 12f); p.lineTo(4f, 12f)
            p.moveTo(20f, 12f); p.lineTo(22f, 12f)
            p.moveTo(4.93f, 19.07f); p.lineTo(6.34f, 17.66f)
            p.moveTo(17.66f, 6.34f); p.lineTo(19.07f, 4.93f)
        }
        DriverIconName.PIN -> {
            p.moveTo(21f, 10f); p.lineTo(12f, 23f); p.lineTo(3f, 10f); p.lineTo(12f, 1f); p.close()
            circlePath(p, 12f, 10f, 3f)
        }
        DriverIconName.FUEL -> {
            p.moveTo(3f, 22f); p.lineTo(15f, 22f)
            p.moveTo(4f, 9f); p.lineTo(14f, 9f)
            p.moveTo(14f, 22f); p.lineTo(14f, 4f); p.lineTo(12f, 2f); p.lineTo(6f, 2f); p.lineTo(4f, 4f); p.lineTo(4f, 22f)
            p.moveTo(14f, 13f); p.lineTo(16f, 13f); p.lineTo(18f, 15f); p.lineTo(18f, 17f); p.lineTo(20f, 19f); p.lineTo(22f, 17f); p.lineTo(22f, 9.83f); p.lineTo(18f, 5f)
        }
        DriverIconName.INFO -> {
            circlePath(p, 12f, 12f, 10f)
            p.moveTo(12f, 16f); p.lineTo(12f, 12f)
            p.moveTo(12f, 8f); p.lineTo(12.01f, 8f)
        }
        DriverIconName.BED -> {
            p.moveTo(2f, 4f); p.lineTo(2f, 20f)
            p.moveTo(2f, 8f); p.lineTo(20f, 8f); p.lineTo(22f, 10f); p.lineTo(22f, 20f)
            p.moveTo(2f, 17f); p.lineTo(22f, 17f)
            circlePath(p, 7f, 13f, 2f)
        }
        DriverIconName.WIFI -> {
            p.moveTo(5f, 12.55f); p.lineTo(19.08f, 12.55f)
            p.moveTo(1.42f, 9f); p.lineTo(22.58f, 9f)
            p.moveTo(8.53f, 16.11f); p.lineTo(15.48f, 16.11f)
            p.moveTo(12f, 20f); p.lineTo(12.01f, 20f)
        }
        DriverIconName.BATTERY -> {
            rectPath(p, 1f, 6f, 18f, 12f, 2f)
            p.moveTo(23f, 13f); p.lineTo(23f, 11f)
        }
        DriverIconName.SIGNAL -> { /* fill only */ }
        DriverIconName.SWAP -> {
            p.moveTo(17f, 1f); p.lineTo(21f, 5f); p.lineTo(17f, 9f)
            p.moveTo(3f, 11f); p.lineTo(3f, 9f); p.lineTo(7f, 5f); p.lineTo(21f, 5f)
            p.moveTo(7f, 23f); p.lineTo(3f, 19f); p.lineTo(7f, 15f)
            p.moveTo(21f, 13f); p.lineTo(21f, 15f); p.lineTo(17f, 19f); p.lineTo(3f, 19f)
        }
        DriverIconName.LOCK -> {
            rectPath(p, 3f, 11f, 18f, 11f, 2f)
            p.moveTo(7f, 11f); p.lineTo(7f, 7f); p.lineTo(12f, 2f); p.lineTo(17f, 7f); p.lineTo(17f, 11f)
        }
        DriverIconName.PLAY -> { /* fill only */ }
        DriverIconName.PAUSE -> { /* fill only */ }
        DriverIconName.FLAG -> {
            p.moveTo(4f, 15f); p.lineTo(8f, 14f); p.lineTo(13f, 16f); p.lineTo(20f, 15f); p.lineTo(20f, 3f); p.lineTo(13f, 4f); p.lineTo(8f, 2f); p.lineTo(4f, 3f); p.close()
            p.moveTo(4f, 22f); p.lineTo(4f, 15f)
        }
        DriverIconName.QMARK -> {
            circlePath(p, 12f, 12f, 10f)
            p.moveTo(9.09f, 9f); p.lineTo(11.92f, 7f); p.lineTo(14.92f, 10f); p.lineTo(11.92f, 13f)
            p.moveTo(12f, 17f); p.lineTo(12.01f, 17f)
        }
        DriverIconName.WEIGHT -> {
            circlePath(p, 12f, 5f, 2f)
            p.moveTo(3f, 22f); p.lineTo(5f, 9f); p.lineTo(19f, 9f); p.lineTo(21f, 22f); p.close()
        }
        DriverIconName.PKG -> {
            p.moveTo(21f, 16f); p.lineTo(21f, 8f); p.lineTo(20f, 6.27f); p.lineTo(13f, 2.27f); p.lineTo(11f, 2.27f); p.lineTo(4f, 6.27f); p.lineTo(3f, 8f); p.lineTo(3f, 16f); p.lineTo(4f, 17.73f); p.lineTo(11f, 21.73f); p.lineTo(13f, 21.73f); p.lineTo(20f, 17.73f); p.close()
            p.moveTo(3.27f, 6.96f); p.lineTo(12f, 12.01f); p.lineTo(20.73f, 6.96f)
            p.moveTo(12f, 22.08f); p.lineTo(12f, 12.01f)
        }
    }
    return p
}
