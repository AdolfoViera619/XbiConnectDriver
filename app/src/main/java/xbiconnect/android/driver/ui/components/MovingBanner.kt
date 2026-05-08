package xbiconnect.android.driver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xbiconnect.android.driver.ui.theme.LocalAppColors

@Composable
fun MovingBanner(text: String) {
    val c = LocalAppColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(c.warn),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text.uppercase(),
            color = c.warnText,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
fun ParkedBanner(text: String, onSimulate: (() -> Unit)? = null, simulateLabel: String? = null) {
    val c = LocalAppColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(c.go),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text.uppercase(),
            color = c.goText,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            letterSpacing = 1.sp,
        )
        if (onSimulate != null && simulateLabel != null) {
            Spacer(Modifier.width(12.dp))
            DriverButton(
                label = simulateLabel,
                onClick = onSimulate,
                variant = DriverButtonVariant.GHOST,
                size = DriverButtonSize.SM,
            )
        }
    }
}
