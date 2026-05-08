package xbiconnect.android.driver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import xbiconnect.android.driver.ui.theme.LocalAppColors

@Composable
fun EmergencyHold(onTrigger: () -> Unit, modifier: Modifier = Modifier) {
    val c = LocalAppColors.current
    var holding by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(holding) {
        if (holding) {
            val start = System.currentTimeMillis()
            while (holding) {
                val elapsed = System.currentTimeMillis() - start
                progress = (elapsed / 3000f).coerceAtMost(1f)
                if (progress >= 1f) {
                    holding = false
                    progress = 0f
                    onTrigger()
                    break
                }
                delay(30)
            }
            progress = 0f
        }
    }

    Box(
        modifier
            .fillMaxWidth()
            .heightIn(min = 130.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(c.stop)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        holding = true
                        try {
                            tryAwaitRelease()
                        } finally {
                            holding = false
                        }
                    },
                )
            }
            .padding(16.dp),
    ) {
        // progress fill from left
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.25f)),
        )
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DriverIcon(DriverIconName.ALERT, size = 32.dp, color = androidx.compose.ui.graphics.Color.White, strokeWidth = 2.5.dp)
            Spacer(Modifier.height(6.dp))
            Text(
                "EMERGENCIA",
                color = androidx.compose.ui.graphics.Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "MANTÉN PRESIONADO 3s",
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f),
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 0.5.sp,
            )
        }
    }
}
