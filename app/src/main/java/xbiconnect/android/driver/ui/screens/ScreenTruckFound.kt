package xbiconnect.android.driver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import xbiconnect.android.driver.ui.components.DriverButton
import xbiconnect.android.driver.ui.components.DriverButtonVariant
import xbiconnect.android.driver.ui.components.DriverIcon
import xbiconnect.android.driver.ui.components.DriverIconName
import xbiconnect.android.driver.ui.components.SystemBar
import xbiconnect.android.driver.ui.components.ThemeToggle
import xbiconnect.android.driver.ui.theme.LocalAppColors
import xbiconnect.android.driver.ui.theme.PlexMono

@Composable
fun ScreenTruckFound(vin: String, onConfirm: () -> Unit, onReject: () -> Unit) {
    val c = LocalAppColors.current
    // TODO: replace mock truck data with real lookup against XBI middleware
    // once the VIN→truck endpoint is wired. For now we just echo the entered VIN.
    val displayVin = if (vin.isNotEmpty()) "…$vin" else "—"
    val data = listOf(
        Triple(stringResource(R.string.field_unit), "45", DataKind.BIG),
        Triple(stringResource(R.string.field_brand), "Kenworth", DataKind.NORMAL),
        Triple(stringResource(R.string.field_model), "T680", DataKind.NORMAL),
        Triple(stringResource(R.string.field_plates), "ABC-123", DataKind.NORMAL),
        Triple(stringResource(R.string.field_vin), displayVin, DataKind.SMALL_MONO),
    )

    Column(Modifier.fillMaxSize()) {
        SystemBar(
            title = "XBI Connect",
            unit = null,
            subtitle = stringResource(R.string.link_subtitle),
            right = { ThemeToggle() },
        )
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(c.tabBg)
                .padding(36.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                Modifier.widthIn(max = 720.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(c.goBg)
                        .border(3.dp, c.goBorder, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    DriverIcon(DriverIconName.CHECK, size = 36.dp, color = c.go, strokeWidth = 3.dp)
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    stringResource(R.string.truck_found),
                    color = c.text,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    stringResource(R.string.truck_found_sub),
                    color = c.textMute,
                    fontSize = 14.sp,
                )
                Spacer(Modifier.height(28.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    data.forEach { (k, v, kind) -> DataTile(k, v, kind) }
                }
                Spacer(Modifier.height(28.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DriverButton(
                        label = stringResource(R.string.btn_not_this),
                        onClick = onReject,
                        variant = DriverButtonVariant.SECONDARY,
                    )
                    DriverButton(
                        label = stringResource(R.string.btn_confirm),
                        onClick = onConfirm,
                        variant = DriverButtonVariant.PRIMARY,
                        icon = DriverIconName.CHECK,
                    )
                }
            }
        }
    }
}

private enum class DataKind { BIG, NORMAL, SMALL_MONO }

@Composable
private fun DataTile(key: String, value: String, kind: DataKind) {
    val c = LocalAppColors.current
    Column(
        Modifier
            .widthIn(min = 120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(c.surfaceSoft)
            .border(1.5.dp, c.line, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            key.uppercase(),
            color = c.textMute,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
        )
        Spacer(Modifier.height(4.dp))
        when (kind) {
            DataKind.BIG -> Text(value, color = c.text, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
            DataKind.NORMAL -> Text(value, color = c.text, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            DataKind.SMALL_MONO -> Text(
                value,
                color = c.textMute,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                fontFamily = PlexMono,
            )
        }
    }
}
