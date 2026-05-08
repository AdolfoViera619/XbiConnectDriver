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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xbiconnect.android.driver.R
import xbiconnect.android.driver.ui.components.DriverBadge
import xbiconnect.android.driver.ui.components.DriverIcon
import xbiconnect.android.driver.ui.components.DriverIconName
import xbiconnect.android.driver.ui.components.DriverStatus
import xbiconnect.android.driver.ui.components.MetricCard
import xbiconnect.android.driver.ui.components.ParkedBanner
import xbiconnect.android.driver.ui.components.SystemBar
import xbiconnect.android.driver.ui.theme.LocalAppColors

@Composable
fun ScreenTrip(
    onOpenChat: () -> Unit,
    onOpenAnnouncement: () -> Unit,
    onSimulateDrive: () -> Unit,
) {
    val c = LocalAppColors.current
    Column(Modifier.fillMaxSize()) {
        SystemBar(
            title = "XBI Connect",
            unit = "Unidad 45",
            right = {
                DriverBadge(status = DriverStatus.AVAILABLE, name = "Carlos Méndez", compact = true)
                Spacer(Modifier.width(8.dp))
                Box(Modifier.size(width = 1.dp, height = 12.dp).background(c.textFaint))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.eld_ok), color = c.textMute, fontSize = 10.sp)
                Spacer(Modifier.width(8.dp))
                Box(Modifier.size(width = 1.dp, height = 12.dp).background(c.textFaint))
                Spacer(Modifier.width(8.dp))
                Text("14:32", color = c.textMute, fontSize = 10.sp)
            },
        )
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(c.tabBg)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ParkedBanner(
                text = stringResource(R.string.status_parked),
                onSimulate = onSimulateDrive,
                simulateLabel = stringResource(R.string.simulate_drive),
            )
            TripRouteCard()
            MetricsRow()
            AnnouncementPinnedCard(onOpen = onOpenAnnouncement)
            LastMessageCard(onOpen = onOpenChat)
        }
    }
}

@Composable
private fun TripRouteCard() {
    val c = LocalAppColors.current
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(c.surface)
            .border(1.5.dp, c.line, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                stringResource(R.string.trip_kicker).uppercase(),
                color = c.textMute,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text("Monterrey, NL", color = c.text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("Centro Distribución Norte", color = c.textMute, fontSize = 11.sp)
        }
        DriverIcon(DriverIconName.ARROW_R, size = 20.dp, color = c.text)
        Column(
            Modifier.weight(1f),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                stringResource(R.string.trip_dest_kicker).uppercase(),
                color = c.textMute,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text("Ciudad de México", color = c.text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("Almacén Vallejo", color = c.textMute, fontSize = 11.sp)
        }
    }
}

@Composable
private fun MetricsRow() {
    val c = LocalAppColors.current
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        MetricCard(
            label = stringResource(R.string.metric_speed),
            value = "0",
            unit = stringResource(R.string.unit_kmh),
            big = true,
            mono = true,
            accent = c.textMute,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = stringResource(R.string.metric_eta),
            value = "2:45",
            unit = stringResource(R.string.unit_hrs),
            big = true,
            mono = true,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = stringResource(R.string.metric_hos),
            value = "3:20",
            unit = stringResource(R.string.unit_hrs),
            big = true,
            mono = true,
            accent = c.warn,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = stringResource(R.string.metric_messages),
            value = "2",
            unit = stringResource(R.string.unit_unread),
            big = true,
            mono = true,
            accent = c.info,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun AnnouncementPinnedCard(onOpen: () -> Unit) {
    val c = LocalAppColors.current
    val gradient = Brush.verticalGradient(listOf(c.warnBgSoft, c.warnBg))
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(gradient)
            .border(1.5.dp, c.warnBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onOpen)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(c.surface)
                    .border(1.dp, c.warnBorder, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center,
            ) {
                DriverIcon(DriverIconName.BELL, size = 12.dp, color = c.warnText, strokeWidth = 2.5.dp)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                stringResource(R.string.announcement_kicker).uppercase(),
                color = c.warnText,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.weight(1f))
            Text("hace 2 h", color = c.textFaint, fontSize = 10.sp)
        }
        Text(
            "Mantenimiento programado · Taller Norte",
            color = c.text,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 18.sp,
        )
        Text(
            "Sábado 4 de mayo, 7:00–13:00. Si tu unidad está agendada, llega 30 min antes.",
            color = c.textMute,
            fontSize = 12.sp,
            lineHeight = 17.sp,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Operaciones · Lucía Pérez",
                color = c.textMute,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${stringResource(R.string.see_all)} (3)",
                    color = c.infoText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(4.dp))
                DriverIcon(DriverIconName.CHEV_R, size = 12.dp, color = c.infoText)
            }
        }
    }
}

@Composable
private fun LastMessageCard(onOpen: () -> Unit) {
    val c = LocalAppColors.current
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.canvas)
            .border(1.dp, c.line, RoundedCornerShape(12.dp))
            .clickable(onClick = onOpen)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            DriverIcon(DriverIconName.CHAT, size = 12.dp, color = c.info)
            Spacer(Modifier.width(6.dp))
            Text(
                stringResource(R.string.dispatch_label),
                color = c.info,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.weight(1f))
            Text(stringResource(R.string.dispatch_5min), color = c.textFaint, fontSize = 10.sp)
        }
        Text(
            "Confirma hora de llegada a CDMX. Cliente espera por las 17:00.",
            color = c.text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
        )
    }
}
