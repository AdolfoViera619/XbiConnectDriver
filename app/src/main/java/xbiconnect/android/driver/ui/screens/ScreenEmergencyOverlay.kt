package xbiconnect.android.driver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import xbiconnect.android.driver.R
import xbiconnect.android.driver.ui.components.DriverIcon
import xbiconnect.android.driver.ui.components.DriverIconName
import xbiconnect.android.driver.ui.theme.DriverPalette
import xbiconnect.android.driver.ui.theme.PlexMono

@Composable
fun ScreenEmergencyOverlay(onClose: () -> Unit) {
    var sent by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(1400)
        sent = true
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(DriverPalette.Stop)
            .padding(36.dp),
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(4.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                DriverIcon(DriverIconName.ALERT, size = 72.dp, color = Color.White, strokeWidth = 2.5.dp)
            }
            Spacer(Modifier.size(20.dp))
            Text(
                stringResource(R.string.emergency_kicker),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.size(8.dp))
            Text(
                if (sent) stringResource(R.string.alert_sent) else stringResource(R.string.sending_alert),
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp,
            )
            Spacer(Modifier.size(10.dp))
            Text(
                if (sent) stringResource(R.string.emergency_sent_body) else stringResource(R.string.emergency_sending_body),
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 15.sp,
                lineHeight = 21.sp,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
            Spacer(Modifier.size(26.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(2.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                ) {
                    Text(
                        stringResource(R.string.emergency_call),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                }
                Box(
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable(onClick = onClose)
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                ) {
                    Text(
                        stringResource(R.string.cancel_alert),
                        color = DriverPalette.Stop,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                stringResource(R.string.emergency_meta_unit),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontFamily = PlexMono,
            )
            Text(
                stringResource(R.string.emergency_meta_loc),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontFamily = PlexMono,
            )
            Text(
                stringResource(R.string.emergency_meta_time),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontFamily = PlexMono,
            )
        }
    }
}
