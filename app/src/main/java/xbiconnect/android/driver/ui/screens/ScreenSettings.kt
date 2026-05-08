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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import xbiconnect.android.driver.R
import xbiconnect.android.driver.data.LocalDriverPreferences
import xbiconnect.android.driver.data.ServerMode
import xbiconnect.android.driver.ui.components.DriverIcon
import xbiconnect.android.driver.ui.components.DriverIconName
import xbiconnect.android.driver.ui.components.SystemBar
import xbiconnect.android.driver.ui.components.ThemeToggle
import xbiconnect.android.driver.ui.theme.LocalAppColors
import xbiconnect.android.driver.ui.theme.LocalThemeController
import xbiconnect.android.driver.ui.theme.PlexMono
import xbiconnect.android.driver.ui.theme.ThemeMode

@Composable
fun ScreenSettings(onUnlink: () -> Unit) {
    val c = LocalAppColors.current
    val controller = LocalThemeController.current
    val prefs = LocalDriverPreferences.current
    val scope = rememberCoroutineScope()
    val savedVin by prefs.vin.collectAsState(initial = null)
    val serverMode by prefs.serverMode.collectAsState(initial = ServerMode.PROD)

    var showUnlinkDialog by remember { mutableStateOf(false) }
    var showServerDialog by remember { mutableStateOf(false) }

    val themeValue = when (controller.mode) {
        ThemeMode.LIGHT -> stringResource(R.string.theme_day)
        ThemeMode.DARK -> stringResource(R.string.theme_night)
        ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
    }
    val serverValue = when (serverMode) {
        ServerMode.LOCAL -> stringResource(R.string.server_local)
        ServerMode.PROD -> stringResource(R.string.server_prod)
    }

    val items = listOf(
        SettingItem(DriverIconName.TRUCK, stringResource(R.string.settings_unit), stringResource(R.string.settings_unit_value)),
        SettingItem(
            icon = DriverIconName.LOCK,
            label = stringResource(R.string.settings_vin),
            value = savedVin?.let { "…$it" } ?: "—",
            valueMono = true,
        ),
        SettingItem(DriverIconName.WIFI, stringResource(R.string.settings_conn), stringResource(R.string.settings_conn_value)),
        SettingItem(DriverIconName.BELL, stringResource(R.string.settings_notif), stringResource(R.string.settings_notif_value)),
        SettingItem(DriverIconName.MOON, stringResource(R.string.settings_display), themeValue, trailing = { ThemeToggle(compact = true) }),
        SettingItem(
            icon = DriverIconName.SWAP,
            label = stringResource(R.string.settings_server),
            value = serverValue,
            onClick = { showServerDialog = true },
        ),
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
                    .clickable { showUnlinkDialog = true }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DriverIcon(DriverIconName.X, size = 16.dp, color = c.stop, strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.unlink), color = c.stop, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    if (showUnlinkDialog) {
        AlertDialog(
            onDismissRequest = { showUnlinkDialog = false },
            title = { Text(stringResource(R.string.unlink_confirm_title), color = c.text, fontWeight = FontWeight.ExtraBold) },
            text = { Text(stringResource(R.string.unlink_confirm_body), color = c.textMute) },
            confirmButton = {
                TextButton(onClick = {
                    showUnlinkDialog = false
                    onUnlink()
                }) {
                    Text(stringResource(R.string.unlink_confirm_cta), color = c.stop, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnlinkDialog = false }) {
                    Text(stringResource(R.string.cancel), color = c.textMute)
                }
            },
            containerColor = c.surface,
        )
    }

    if (showServerDialog) {
        ServerPickerDialog(
            current = serverMode,
            onPick = { mode ->
                scope.launch { prefs.setServerMode(mode) }
                showServerDialog = false
            },
            onDismiss = { showServerDialog = false },
        )
    }
}

@Composable
private fun ServerPickerDialog(
    current: ServerMode,
    onPick: (ServerMode) -> Unit,
    onDismiss: () -> Unit,
) {
    val c = LocalAppColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.server_change_title), color = c.text, fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(stringResource(R.string.server_change_body), color = c.textMute, fontSize = 13.sp)
                ServerOption(stringResource(R.string.server_prod), active = current == ServerMode.PROD) { onPick(ServerMode.PROD) }
                ServerOption(stringResource(R.string.server_local), active = current == ServerMode.LOCAL) { onPick(ServerMode.LOCAL) }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = c.textMute)
            }
        },
        containerColor = c.surface,
    )
}

@Composable
private fun ServerOption(label: String, active: Boolean, onClick: () -> Unit) {
    val c = LocalAppColors.current
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (active) c.infoBg else c.canvas)
            .border(1.dp, if (active) c.infoBorder else c.line, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            color = if (active) c.infoText else c.text,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
        )
        if (active) DriverIcon(DriverIconName.CHECK, size = 16.dp, color = c.go, strokeWidth = 2.5.dp)
    }
}

private data class SettingItem(
    val icon: DriverIconName,
    val label: String,
    val value: String,
    val onClick: (() -> Unit)? = null,
    val trailing: (@Composable () -> Unit)? = null,
    val valueMono: Boolean = false,
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
            Text(
                item.value,
                color = c.textMute,
                fontSize = 12.sp,
                fontFamily = if (item.valueMono) PlexMono else null,
            )
        }
        if (item.trailing != null) item.trailing.invoke()
        else if (item.onClick != null) DriverIcon(DriverIconName.CHEV_R, size = 16.dp, color = c.textFaint)
    }
}
