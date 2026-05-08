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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random
import xbiconnect.android.driver.R
import xbiconnect.android.driver.ui.components.DriverButton
import xbiconnect.android.driver.ui.components.DriverButtonSize
import xbiconnect.android.driver.ui.components.DriverButtonVariant
import xbiconnect.android.driver.ui.components.DriverIcon
import xbiconnect.android.driver.ui.components.DriverIconName
import xbiconnect.android.driver.ui.theme.LocalAppColors

@Composable
fun VoiceModal(onDismiss: () -> Unit, onSend: (String) -> Unit) {
    val c = LocalAppColors.current
    val transcript = stringResource(R.string.voice_transcript)
    var pulse by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(200)
            pulse++
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(Modifier.fillMaxSize().background(c.text.copy(alpha = 0.6f))) {
            Column(
                Modifier
                    .align(Alignment.Center)
                    .width(480.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(c.surface)
                    .border(1.dp, c.line, RoundedCornerShape(16.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(10.dp).clip(CircleShape).background(c.stop))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.voice_recording),
                            color = c.text,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        )
                    }
                    Box(
                        Modifier.size(28.dp).clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center,
                    ) {
                        DriverIcon(DriverIconName.X, size = 20.dp, color = c.textMute)
                    }
                }
                // Waveform
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(c.surfaceSoft),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterHorizontally),
                ) {
                    val rng = Random(pulse)
                    repeat(28) { i ->
                        val h = (8f + abs(sin(i * 0.5f)) * 30f + rng.nextFloat() * 14f).toInt()
                        Box(
                            Modifier
                                .width(3.dp)
                                .height(h.dp.coerceAtLeast(4.dp))
                                .background(c.brand)
                                .clip(RoundedCornerShape(2.dp)),
                        )
                    }
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(c.surfaceSoft)
                        .border(1.dp, c.line, RoundedCornerShape(10.dp))
                        .padding(14.dp),
                ) {
                    Text(transcript, color = c.text, fontSize = 14.sp, lineHeight = 21.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DriverButton(
                        label = stringResource(R.string.cancel),
                        onClick = onDismiss,
                        variant = DriverButtonVariant.SECONDARY,
                        size = DriverButtonSize.MD,
                        modifier = Modifier.weight(1f),
                    )
                    DriverButton(
                        label = stringResource(R.string.send_message),
                        onClick = { onSend(transcript) },
                        variant = DriverButtonVariant.PRIMARY,
                        icon = DriverIconName.SEND,
                        modifier = Modifier.weight(2f),
                    )
                }
            }
        }
    }
}

