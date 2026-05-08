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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xbiconnect.android.driver.R
import xbiconnect.android.driver.ui.components.DriverIcon
import xbiconnect.android.driver.ui.components.DriverIconName
import xbiconnect.android.driver.ui.components.InfoCallout
import xbiconnect.android.driver.ui.components.SystemBar
import xbiconnect.android.driver.ui.theme.LocalAppColors
import xbiconnect.android.driver.ui.theme.PlexMono

private enum class AnnouncementKind { GENERAL, INFO, WARN }

private data class Announcement(
    val id: Int,
    val kind: AnnouncementKind,
    val title: String,
    val body: String,
    val time: String,
    val author: String,
    val pinned: Boolean = false,
    val ack: Int,
    val total: Int,
)

@Composable
fun ScreenAnnouncements() {
    val c = LocalAppColors.current
    val items = listOf(
        Announcement(1, AnnouncementKind.GENERAL, "Mantenimiento programado · Taller Norte", "Sábado 4 de mayo, 7:00–13:00. Si tu unidad está agendada, llega 30 min antes con la documentación de servicio.", "hace 2 h", "Operaciones · Lucía Pérez", pinned = true, ack = 28, total = 42),
        Announcement(2, AnnouncementKind.WARN, "Bloqueo MX-15D km 240", "Manifestación bloquea ambos sentidos de la carretera. Toma desvío por Saltillo–Monclova. La oficina actualiza cada hora.", "hace 4 h", "Seguridad · Centro Op.", ack = 39, total = 42),
        Announcement(3, AnnouncementKind.INFO, "Nuevo cliente: Bimbo CDMX", "Desde mayo entrega diaria 5:00 a 7:00 AM en planta Azcapotzalco. Capacitación obligatoria viernes 10 mayo, 16:00.", "Ayer · 09:14", "Operaciones · Lucía Pérez", ack = 31, total = 42),
        Announcement(4, AnnouncementKind.INFO, "Pago de nómina adelantado", "Por el día festivo, la nómina de esta quincena se deposita el jueves 1 de mayo en lugar del viernes.", "Lun · 11:30", "RH · Mariana Velázquez", ack = 42, total = 42),
        Announcement(5, AnnouncementKind.GENERAL, "Encuesta de satisfacción del operador", "Toma 3 minutos en responder. Tu feedback se usa para mejorar rutas, descansos y herramientas.", "Vie pasado", "RH · Mariana Velázquez", ack = 18, total = 42),
    )

    Column(Modifier.fillMaxSize()) {
        SystemBar(title = "XBI Connect", unit = null, subtitle = "Comunicados de la flota")
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(c.tabBg)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(stringResource(R.string.announcements_title), color = c.text, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(stringResource(R.string.announcements_sub), color = c.textMute, fontSize = 12.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(stringResource(R.string.filter_all), active = true)
                    FilterChip(stringResource(R.string.filter_unread), active = false)
                    FilterChip(stringResource(R.string.filter_pinned), active = false)
                }
            }
            Spacer(Modifier.height(4.dp))
            items.forEach { AnnouncementCard(it) }
        }
        InfoCallout(stringResource(R.string.announcements_callout))
    }
}

@Composable
private fun FilterChip(label: String, active: Boolean) {
    val c = LocalAppColors.current
    val bg = if (active) c.brand else c.surface
    val fg = if (active) c.brandOn else c.textMute
    val border = if (active) c.brand else c.line
    Box(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(label, color = fg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AnnouncementCard(a: Announcement) {
    val c = LocalAppColors.current
    val style = when (a.kind) {
        AnnouncementKind.WARN -> ChipStyle(c.warnBg, c.warnBorder, c.warnText, stringResource(R.string.chip_urgent))
        AnnouncementKind.INFO -> ChipStyle(c.infoBg, c.infoBorder, c.infoText, stringResource(R.string.chip_info))
        AnnouncementKind.GENERAL -> ChipStyle(c.canvas, c.line, c.text, stringResource(R.string.chip_general))
    }
    val cardBg: Any = if (a.pinned) Brush.verticalGradient(listOf(c.warnBgSoft, c.warnBg)) else c.surface
    val cardBorder = if (a.pinned) c.warnBorder else c.line
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .let { mod ->
                if (cardBg is Brush) mod.background(cardBg) else mod.background(cardBg as Color)
            }
            .border(1.5.dp, cardBorder, RoundedCornerShape(14.dp))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (a.pinned) {
                Row(
                    Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(c.surface)
                        .border(1.dp, c.warnBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DriverIcon(DriverIconName.BELL, size = 11.dp, color = c.warnText, strokeWidth = 2.5.dp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        stringResource(R.string.chip_pinned),
                        color = c.warnText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                    )
                }
                Spacer(Modifier.width(6.dp))
            }
            Box(
                Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(style.bg)
                    .border(1.dp, style.border, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(style.label, color = style.fg, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            }
            Spacer(Modifier.weight(1f))
            Text(a.time, color = c.textFaint, fontSize = 11.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(a.title, color = c.text, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, lineHeight = 20.sp)
        Spacer(Modifier.height(6.dp))
        Text(a.body, color = c.textMute, fontSize = 13.sp, lineHeight = 19.sp)
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(c.line))
        Spacer(Modifier.height(10.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val initials = a.author.substringAfter("· ", "").split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
                Box(
                    Modifier.size(22.dp).clip(CircleShape).background(c.brand),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(initials.ifBlank { "OP" }, color = c.brandOn, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.width(6.dp))
                Text(a.author, color = c.textMute, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DriverIcon(DriverIconName.CHECK_ALL, size = 12.dp, color = c.go, strokeWidth = 2.dp)
                    Spacer(Modifier.width(4.dp))
                    Text("${a.ack}/${a.total}", color = c.textMute, fontSize = 11.sp, fontFamily = PlexMono)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.seen_count), color = c.textMute, fontSize = 11.sp)
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(c.infoBg)
                        .border(1.dp, c.infoBorder, RoundedCornerShape(8.dp))
                        .clickable { /* mark read */ }
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                ) {
                    Text(stringResource(R.string.mark_read), color = c.infoText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private data class ChipStyle(val bg: Color, val border: Color, val fg: Color, val label: String)
