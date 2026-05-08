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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xbiconnect.android.driver.R
import xbiconnect.android.driver.ui.components.DriverBadge
import xbiconnect.android.driver.ui.components.DriverIcon
import xbiconnect.android.driver.ui.components.DriverIconName
import xbiconnect.android.driver.ui.components.DriverStatus
import xbiconnect.android.driver.ui.components.EmergencyHold
import xbiconnect.android.driver.ui.components.MovingBanner
import xbiconnect.android.driver.ui.components.SystemBar
import xbiconnect.android.driver.ui.theme.LocalAppColors
import xbiconnect.android.driver.ui.theme.PlexMono

private data class QuickReply(val labelKey: Int, val icon: DriverIconName, val color: ColorRef)
private enum class ColorRef { GO, INFO, WARN }

@Composable
fun ScreenDrivingLock(
    onUnlock: () -> Unit,
    onEmergency: () -> Unit,
    onVoice: () -> Unit,
    onQuickReply: (String) -> Unit,
) {
    val c = LocalAppColors.current
    val quicks = listOf(
        QuickReply(R.string.qr_received, DriverIconName.CHECK, ColorRef.GO),
        QuickReply(R.string.qr_arriving, DriverIconName.PIN, ColorRef.INFO),
        QuickReply(R.string.qr_arrived, DriverIconName.FLAG, ColorRef.GO),
        QuickReply(R.string.qr_delayed, DriverIconName.CLOCK, ColorRef.WARN),
        QuickReply(R.string.qr_break, DriverIconName.BED, ColorRef.INFO),
        QuickReply(R.string.qr_fueling, DriverIconName.FUEL, ColorRef.INFO),
    )

    Column(Modifier.fillMaxSize()) {
        SystemBar(
            title = "XBI Connect",
            unit = "Unidad 45",
            right = {
                DriverBadge(status = DriverStatus.DRIVING, name = "Carlos Méndez", compact = true)
                Spacer(Modifier.width(8.dp))
                Box(Modifier.size(width = 1.dp, height = 12.dp).background(c.textFaint))
                Spacer(Modifier.width(8.dp))
                Text("14:32", color = c.textMute, fontSize = 10.sp)
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, c.border, RoundedCornerShape(6.dp))
                        .clickable(onClick = onUnlock)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(stringResource(R.string.exit_sim), color = c.textMute, fontSize = 10.sp)
                }
            },
        )
        Column(
            Modifier
                .fillMaxSize()
                .background(c.tabBg)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            MovingBanner(text = stringResource(R.string.status_moving_locked))
            Row(
                Modifier.fillMaxSize().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // LEFT: speed hero + quick replies + dispatch
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SpeedHero()
                    Column(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            stringResource(R.string.quick_reply_kicker).uppercase(),
                            color = c.textMute,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                        )
                        QuickRepliesGrid(quicks, onQuickReply)
                    }
                    DispatchBanner()
                }
                // RIGHT: SOS + voice
                Column(
                    Modifier.width(180.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    EmergencyHold(onTrigger = onEmergency, modifier = Modifier.weight(1f))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(c.canvas)
                            .border(2.dp, c.line, RoundedCornerShape(14.dp))
                            .clickable(onClick = onVoice)
                            .padding(14.dp),
                    ) {
                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            DriverIcon(DriverIconName.MIC, size = 28.dp, color = c.text, strokeWidth = 2.dp)
                            Spacer(Modifier.height(6.dp))
                            Text(stringResource(R.string.voice_note), color = c.text, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(stringResource(R.string.hands_free), color = c.textMute, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpeedHero() {
    val c = LocalAppColors.current
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(c.surfaceSoft)
            .border(2.dp, c.line, RoundedCornerShape(14.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                stringResource(R.string.metric_speed).uppercase(),
                color = c.textMute,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "67",
                    color = c.text,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 84.sp,
                    fontFamily = PlexMono,
                    letterSpacing = (-3).sp,
                )
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.unit_kmh), color = c.textMute, fontSize = 14.sp)
            }
        }
        Box(Modifier.width(1.dp).height(70.dp).background(c.line))
        Spacer(Modifier.width(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            HeroMetric("ETA", "17:20", c.text)
            HeroMetric("HOS", "3:20", c.warn)
        }
    }
}

@Composable
private fun HeroMetric(label: String, value: String, accent: Color) {
    val c = LocalAppColors.current
    Column {
        Text(
            label,
            color = c.textMute,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
        )
        Text(value, color = accent, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, fontFamily = PlexMono)
    }
}

@Composable
private fun QuickRepliesGrid(quicks: List<QuickReply>, onSend: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            quicks.take(3).forEach { q -> QuickReplyTile(q, onSend, modifier = Modifier.weight(1f).fillMaxHeight()) }
        }
        Row(
            Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            quicks.drop(3).forEach { q -> QuickReplyTile(q, onSend, modifier = Modifier.weight(1f).fillMaxHeight()) }
        }
    }
}

@Composable
private fun QuickReplyTile(q: QuickReply, onSend: (String) -> Unit, modifier: Modifier = Modifier) {
    val c = LocalAppColors.current
    val color = when (q.color) {
        ColorRef.GO -> c.go
        ColorRef.INFO -> c.info
        ColorRef.WARN -> c.warn
    }
    val label = stringResource(q.labelKey)
    Box(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(c.surface)
            .border(1.5.dp, c.line, RoundedCornerShape(14.dp))
            .clickable { onSend(label) }
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(c.surfaceSoft)
                    .border(2.dp, color, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                DriverIcon(q.icon, size = 22.dp, color = color, strokeWidth = 2.5.dp)
            }
            Text(label, color = c.text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun DispatchBanner() {
    val c = LocalAppColors.current
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.infoBg)
            .border(1.dp, c.infoBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            DriverIcon(DriverIconName.CHAT, size = 12.dp, color = c.infoText)
            Spacer(Modifier.width(6.dp))
            Text(stringResource(R.string.dispatch), color = c.infoText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text(stringResource(R.string.dispatch_5min), color = c.textFaint, fontSize = 10.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(stringResource(R.string.confirm_arrival), color = c.text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}
