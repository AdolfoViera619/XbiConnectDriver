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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xbiconnect.android.driver.R
import xbiconnect.android.driver.ui.components.DriverIcon
import xbiconnect.android.driver.ui.components.DriverIconName
import xbiconnect.android.driver.ui.components.SystemBar
import xbiconnect.android.driver.ui.components.ThemeToggle
import xbiconnect.android.driver.ui.theme.LocalAppColors
import xbiconnect.android.driver.ui.theme.LocalThemeController
import xbiconnect.android.driver.ui.theme.ThemeMode

@Composable
fun ScreenSettings() {
    val c = LocalAppColors.current
    val controller = LocalThemeController.current
    val themeValue = when (controller.mode) {
        ThemeMode.LIGHT -> stringResource(R.string.theme_day)
        ThemeMode.DARK -> stringResource(R.string.theme_night)
        ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
    }

    val items = listOf(
        SettingItem(DriverIconName.TRUCK, stringResource(R.string.settings_unit), stringResource(R.string.settings_unit_value)),
        SettingItem(DriverIconName.USER, stringResource(R.string.settings_driver), stringResource(R.string.settings_driver_value)),
        SettingItem(DriverIconName.WIFI, stringResource(R.string.settings_conn), stringResource(R.string.settings_conn_value)),
        SettingItem(DriverIconName.BELL, stringResource(R.string.settings_notif), stringResource(R.string.settings_notif_value)),
        SettingItem(DriverIconName.MOON, stringResource(R.string.settings_display), themeValue, trailing = { ThemeToggle(compact = true) }),
        SettingItem(DriverIconName.INFO, stringResource(R.string.settings_about), stringResource(R.string.settings_about_value)),
    )

    Column(Modifier.fillMaxSize()) {
        SystemBar(title = "XBI Connect", unit = null, subtitle = stringResource(R.string.nav_settings))
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(c.tabBg)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                stringResource(R.string.settings_title),
                color = c.text,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
            )
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.surface)
                    .border(1.dp, c.line, RoundedCornerShape(12.dp)),
            ) {
                items.forEachIndexed { idx, it ->
                    SettingRow(it)
                    if (idx < items.lastIndex) Box(Modifier.fillMaxWidth().height(1.dp).background(c.line))
                }
            }
            Row(
                Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.5.dp, c.stop, RoundedCornerShape(10.dp))
                    .clickable { /* unlink — Phase 3 */ }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DriverIcon(DriverIconName.X, size = 16.dp, color = c.stop, strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.unlink), color = c.stop, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

private data class SettingItem(
    val icon: DriverIconName,
    val label: String,
    val value: String,
    val onClick: (() -> Unit)? = null,
    val trailing: (@Composable () -> Unit)? = null,
)

@Composable
private fun SettingRow(item: SettingItem) {
    val c = LocalAppColors.current
    Row(
        Modifier
            .fillMaxWidth()
            .let { if (item.onClick != null) it.clickable(onClick = item.onClick) else it }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(c.canvas)
                .border(1.dp, c.line, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            DriverIcon(item.icon, size = 18.dp, color = c.textMute)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(item.label, color = c.text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(2.dp))
            Text(item.value, color = c.textMute, fontSize = 12.sp)
        }
        if (item.trailing != null) item.trailing.invoke()
        else DriverIcon(DriverIconName.CHEV_R, size = 16.dp, color = c.textFaint)
    }
}
