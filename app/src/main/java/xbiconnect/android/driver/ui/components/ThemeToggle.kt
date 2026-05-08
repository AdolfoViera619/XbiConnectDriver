package xbiconnect.android.driver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import xbiconnect.android.driver.R
import xbiconnect.android.driver.ui.theme.LocalAppColors
import xbiconnect.android.driver.ui.theme.LocalThemeController
import xbiconnect.android.driver.ui.theme.ThemeMode

@Composable
fun ThemeToggle(modifier: Modifier = Modifier, compact: Boolean = true) {
    val controller = LocalThemeController.current
    val c = LocalAppColors.current
    val effectiveDark = !c.isLight
    var open by remember { mutableStateOf(false) }
    val triggerIcon = when (controller.mode) {
        ThemeMode.LIGHT -> DriverIconName.SUN
        ThemeMode.DARK -> DriverIconName.MOON
        ThemeMode.SYSTEM -> if (effectiveDark) DriverIconName.MOON else DriverIconName.SUN
    }
    val size = if (compact) 32.dp else 40.dp

    Box(modifier) {
        Box(
            Modifier
                .size(size)
                .clip(RoundedCornerShape(8.dp))
                .background(c.surfaceSoft)
                .border(1.dp, c.line, RoundedCornerShape(8.dp))
                .clickable { open = true },
            contentAlignment = Alignment.Center,
        ) {
            DriverIcon(triggerIcon, size = if (compact) 16.dp else 20.dp, color = c.text, strokeWidth = 2.dp)
        }
        DropdownMenu(
            expanded = open,
            onDismissRequest = { open = false },
        ) {
            ThemeOption(
                icon = DriverIconName.SUN,
                label = stringResource(R.string.theme_day),
                active = controller.mode == ThemeMode.LIGHT,
                onClick = { controller.setMode(ThemeMode.LIGHT); open = false },
            )
            ThemeOption(
                icon = DriverIconName.MOON,
                label = stringResource(R.string.theme_night),
                active = controller.mode == ThemeMode.DARK,
                onClick = { controller.setMode(ThemeMode.DARK); open = false },
            )
            ThemeOption(
                icon = DriverIconName.SETTINGS,
                label = stringResource(R.string.theme_system),
                active = controller.mode == ThemeMode.SYSTEM,
                onClick = { controller.setMode(ThemeMode.SYSTEM); open = false },
            )
        }
    }
}

@Composable
private fun ThemeOption(
    icon: DriverIconName,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    val c = LocalAppColors.current
    DropdownMenuItem(
        onClick = onClick,
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DriverIcon(icon, size = 18.dp, color = if (active) c.info else c.textMute, strokeWidth = 2.dp)
                Spacer(Modifier.width(10.dp))
                Text(
                    label,
                    color = if (active) c.text else c.textMute,
                    fontSize = 14.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
                )
                if (active) {
                    Spacer(Modifier.width(8.dp))
                    DriverIcon(DriverIconName.CHECK, size = 14.dp, color = c.go, strokeWidth = 2.5.dp)
                }
            }
        },
    )
}
