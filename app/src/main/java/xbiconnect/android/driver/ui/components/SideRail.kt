package xbiconnect.android.driver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import xbiconnect.android.driver.ui.nav.Section
import xbiconnect.android.driver.ui.theme.LocalAppColors
import xbiconnect.android.driver.ui.theme.PlexMono

data class RailItem(
    val section: Section,
    val icon: DriverIconName,
    val label: String,
    val badge: Int = 0,
)

@Composable
fun SideRail(
    items: List<RailItem>,
    active: Section,
    onChange: (Section) -> Unit,
    sosLabel: String,
    onSos: () -> Unit,
) {
    val c = LocalAppColors.current
    val borderColor = c.line
    Column(
        Modifier
            .width(84.dp)
            .fillMaxHeight()
            .background(c.tabBg)
            .drawBehind {
                drawLine(
                    color = borderColor,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1f,
                )
            }
            .padding(vertical = 14.dp),
    ) {
        Box(Modifier.fillMaxWidth().padding(bottom = 16.dp), contentAlignment = Alignment.Center) {
            XbiMark(size = 44.dp, onLight = true)
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(c.line))
        Spacer(Modifier.height(8.dp))
        Column(
            Modifier.weight(1f).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items.forEach { item ->
                RailEntry(item, active = item.section == active, onClick = { onChange(item.section) })
            }
        }
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            ThemeToggle(compact = true)
        }
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier
                .padding(horizontal = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.5.dp, c.stop, RoundedCornerShape(10.dp))
                .clickable(onClick = onSos)
                .padding(vertical = 12.dp, horizontal = 6.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DriverIcon(DriverIconName.ALERT, size = 22.dp, color = c.stop)
                Spacer(Modifier.height(4.dp))
                Text(
                    sosLabel,
                    color = c.stop,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp,
                )
            }
        }
    }
}

@Composable
private fun RailEntry(item: RailItem, active: Boolean, onClick: () -> Unit) {
    val c = LocalAppColors.current
    val bg = if (active) c.infoBg else Color.Transparent
    val fg = if (active) c.infoText else c.textMute
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 6.dp),
    ) {
        if (active) {
            Box(
                Modifier
                    .offset(x = (-14).dp)
                    .size(width = 3.dp, height = 22.dp)
                    .clip(RoundedCornerShape(0.dp, 3.dp, 3.dp, 0.dp))
                    .background(c.info)
                    .align(Alignment.CenterStart),
            )
        }
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box {
                DriverIcon(item.icon, size = 24.dp, color = fg)
                if (item.badge > 0) {
                    Box(
                        Modifier
                            .offset(x = 14.dp, y = (-6).dp)
                            .clip(CircleShape)
                            .background(c.stop)
                            .border(2.dp, c.tabBg, CircleShape)
                            .padding(horizontal = 4.dp, vertical = 1.dp),
                    ) {
                        Text(
                            item.badge.toString(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontFamily = PlexMono,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                item.label.uppercase(),
                color = fg,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.3.sp,
            )
        }
    }
}
