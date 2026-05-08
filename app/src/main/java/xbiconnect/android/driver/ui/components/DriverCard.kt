package xbiconnect.android.driver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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

data class DriverMetric(val label: String, val value: String, val color: Color? = null)

@Composable
fun DriverCard(
    initials: String,
    name: String,
    status: DriverStatus,
    metrics: List<DriverMetric>,
    modifier: Modifier = Modifier,
) {
    val c = LocalAppColors.current
    val isActive = status == DriverStatus.DRIVING
    val bg = if (isActive) c.goBgSoft else c.warnBgSoft
    val border = if (isActive) c.goBorder else c.warnBorder
    val avatarBg = if (isActive) c.goBg else c.warnBg
    val avatarFg = if (isActive) c.goText else c.warnText
    val dot = if (isActive) c.goDot else c.warnDot
    val labelText = if (isActive) "DRIVING" else "SLEEPER"
    val labelFg = if (isActive) c.goText else c.warnText

    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(2.dp, border, RoundedCornerShape(14.dp))
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(avatarBg),
                contentAlignment = Alignment.Center,
            ) {
                Text(initials, color = avatarFg, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(name, color = c.text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(6.dp).clip(CircleShape).background(dot))
                    Spacer(Modifier.width(4.dp))
                    Text(labelText, color = labelFg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row {
            metrics.forEachIndexed { idx, m ->
                if (idx > 0) Box(Modifier.width(1.dp).height(36.dp).background(border))
                Column(
                    Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        m.label.uppercase(),
                        color = c.textMute,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.4.sp,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        m.value,
                        color = m.color ?: c.text,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = PlexMono,
                    )
                }
            }
        }
    }
}
