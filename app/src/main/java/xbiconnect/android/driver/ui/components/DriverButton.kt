package xbiconnect.android.driver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xbiconnect.android.driver.ui.theme.LocalAppColors

enum class DriverButtonVariant { PRIMARY, SECONDARY, GHOST, DANGER, INFO, GO }
enum class DriverButtonSize { SM, MD, LG, XL }

@Composable
fun DriverButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: DriverButtonVariant = DriverButtonVariant.PRIMARY,
    size: DriverButtonSize = DriverButtonSize.MD,
    icon: DriverIconName? = null,
    full: Boolean = false,
) {
    val c = LocalAppColors.current
    data class V(val bg: Color, val fg: Color, val border: Color)
    val v = when (variant) {
        DriverButtonVariant.PRIMARY -> V(c.brand, c.brandOn, c.brand)
        DriverButtonVariant.SECONDARY -> V(c.canvas, c.textMute, c.line)
        DriverButtonVariant.GHOST -> V(Color.Transparent, c.textMute, c.border)
        DriverButtonVariant.DANGER -> V(c.stop, Color.White, c.stop)
        DriverButtonVariant.INFO -> V(c.infoBg, c.infoText, c.infoBorder)
        DriverButtonVariant.GO -> V(c.goBg, c.goText, c.goBorder)
    }
    data class S(val pad: PaddingValues, val font: Int, val iconDp: Dp)
    val s = when (size) {
        DriverButtonSize.SM -> S(PaddingValues(horizontal = 16.dp, vertical = 10.dp), 13, 15.dp)
        DriverButtonSize.MD -> S(PaddingValues(horizontal = 22.dp, vertical = 14.dp), 15, 18.dp)
        DriverButtonSize.LG -> S(PaddingValues(horizontal = 28.dp, vertical = 16.dp), 16, 20.dp)
        DriverButtonSize.XL -> S(PaddingValues(horizontal = 32.dp, vertical = 20.dp), 18, 22.dp)
    }
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier
            .let { if (full) it.fillMaxWidth() else it }
            .clip(shape)
            .background(v.bg)
            .border(1.5.dp, v.border, shape)
            .clickable(onClick = onClick)
            .padding(s.pad),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            DriverIcon(icon, size = s.iconDp, color = v.fg, strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
        }
        Text(label, color = v.fg, fontWeight = FontWeight.Bold, fontSize = s.font.sp)
    }
}
