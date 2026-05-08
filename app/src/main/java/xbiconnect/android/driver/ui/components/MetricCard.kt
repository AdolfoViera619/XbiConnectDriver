package xbiconnect.android.driver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@Composable
fun MetricCard(
    label: String,
    value: String,
    unit: String? = null,
    modifier: Modifier = Modifier,
    accent: Color? = null,
    big: Boolean = false,
    mono: Boolean = false,
) {
    val c = LocalAppColors.current
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(c.surfaceSoft)
            .border(1.5.dp, c.line, shape)
            .padding(horizontal = if (big) 14.dp else 10.dp, vertical = if (big) 16.dp else 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            label.uppercase(),
            color = c.textMute,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
        )
        Text(
            value,
            color = accent ?: c.text,
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (big) 44.sp else 32.sp,
            fontFamily = if (mono) PlexMono else null,
        )
        if (unit != null) {
            Text(unit, color = c.textMute, fontSize = 11.sp)
        }
    }
}
