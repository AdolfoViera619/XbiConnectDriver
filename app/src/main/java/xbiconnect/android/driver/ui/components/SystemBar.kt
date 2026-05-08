package xbiconnect.android.driver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import xbiconnect.android.driver.ui.theme.LocalAppColors

@Composable
fun SystemBar(
    title: String = "XBI Connect",
    unit: String? = "Unidad 45",
    subtitle: String? = null,
    right: @Composable (() -> Unit)? = null,
) {
    val c = LocalAppColors.current
    Row(
        Modifier
            .fillMaxWidth()
            .background(c.tabStatusBar)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            DriverIcon(DriverIconName.CHAT, size = 13.dp, color = c.tabStatusBarFg, strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
            Text(
                title,
                color = c.text,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
            )
            if (unit != null) {
                Spacer(Modifier.width(8.dp))
                BarPipe()
                Spacer(Modifier.width(8.dp))
                Text(unit, color = c.textMute, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            if (subtitle != null) {
                Spacer(Modifier.width(8.dp))
                BarPipe()
                Spacer(Modifier.width(8.dp))
                Text(subtitle, color = c.textMute, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            right?.invoke()
        }
    }
    Box(Modifier.fillMaxWidth().height(1.dp).background(c.line))
}

@Composable
private fun BarPipe() {
    val c = LocalAppColors.current
    Box(Modifier.size(width = 1.dp, height = 12.dp).background(c.textFaint))
}
