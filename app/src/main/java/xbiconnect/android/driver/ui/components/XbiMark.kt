package xbiconnect.android.driver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
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
import xbiconnect.android.driver.ui.theme.DriverPalette

@Composable
fun XbiMark(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    onLight: Boolean = true,
) {
    val bg = if (onLight) DriverPalette.Navy else Color.White
    val fg = if (onLight) Color.White else DriverPalette.Navy
    Box(
        modifier
            .size(size)
            .clip(RoundedCornerShape((size.value * 0.25f).dp))
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "X",
            color = fg,
            fontWeight = FontWeight.ExtraBold,
            fontSize = (size.value * 0.5f).sp,
        )
    }
}
