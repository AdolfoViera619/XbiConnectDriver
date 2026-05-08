package xbiconnect.android.driver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xbiconnect.android.driver.ui.theme.LocalAppColors
import xbiconnect.android.driver.ui.theme.PlexMono

enum class HOSKind { DRIVE, ON_DUTY, REST }

data class HOSSegment(val kind: HOSKind, val weight: Float)

@Composable
fun HOSTimeline(driverName: String, segments: List<HOSSegment>) {
    val c = LocalAppColors.current
    val total = segments.sumOf { it.weight.toDouble() }.toFloat()
    Column {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(driverName, color = c.text, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("00:00 — 24:00", color = c.textFaint, fontSize = 10.sp, fontFamily = PlexMono)
        }
        Spacer(Modifier.height(4.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .height(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(1.dp, c.line, RoundedCornerShape(6.dp)),
        ) {
            segments.forEach { seg ->
                Box(
                    Modifier
                        .weight(seg.weight / total)
                        .fillMaxHeight()
                        .background(hosColor(seg.kind, c)),
                )
            }
        }
    }
}

@Composable
fun HOSLegend() {
    val c = LocalAppColors.current
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        LegendItem("Manejando", hosColor(HOSKind.DRIVE, c))
        LegendItem("En servicio", hosColor(HOSKind.ON_DUTY, c))
        LegendItem("Descanso", hosColor(HOSKind.REST, c))
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    val c = LocalAppColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(color))
        Spacer(Modifier.width(6.dp))
        Text(label, color = c.textMute, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun hosColor(kind: HOSKind, c: xbiconnect.android.driver.ui.theme.AppColors): Color = when (kind) {
    HOSKind.DRIVE -> c.brand
    HOSKind.ON_DUTY -> c.go
    HOSKind.REST -> Color(0xFF778DA9)
}
