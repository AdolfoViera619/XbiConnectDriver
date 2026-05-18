package xbiconnect.android.driver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import xbiconnect.android.driver.data.api.dto.HosDriverDto
import xbiconnect.android.driver.data.state.DriverState
import xbiconnect.android.driver.ui.components.DriverCard
import xbiconnect.android.driver.ui.components.InfoCallout
import xbiconnect.android.driver.ui.components.SystemBar
import xbiconnect.android.driver.ui.components.driverStatusFromCode

@Composable
fun ScreenTeam(driverState: DriverState) {
    val c = xbiconnect.android.driver.ui.theme.LocalAppColors.current
    Column(Modifier.fillMaxSize()) {
        SystemBar(title = "XBI Connect", subtitle = "Equipo · Unidad")
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
                stringResource(R.string.team_drivers),
                color = c.text,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
            )

            when (driverState) {
                is DriverState.Loading -> LoadingBlock()
                is DriverState.NoData -> EmptyBlock(driverState.message ?: "Sin chofer asignado a esta unidad.")
                is DriverState.Error -> EmptyBlock(driverState.message)
                is DriverState.Active -> ActiveTeamBlock(driverState.main, driverState.coDriver)
            }
        }
        InfoCallout(stringResource(R.string.team_callout))
    }
}

@Composable
private fun LoadingBlock() {
    val c = xbiconnect.android.driver.ui.theme.LocalAppColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(strokeWidth = 2.dp, color = c.brand, modifier = Modifier.size(18.dp))
        Spacer(Modifier.size(10.dp))
        Text("Cargando equipo…", color = c.textMute, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun EmptyBlock(message: String) {
    val c = xbiconnect.android.driver.ui.theme.LocalAppColors.current
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.surface)
            .border(1.dp, c.line, RoundedCornerShape(12.dp))
            .padding(20.dp),
    ) {
        Text(message, color = c.textMute, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ActiveTeamBlock(main: HosDriverDto, coDriver: HosDriverDto?) {
    if (coDriver != null) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DriverCardFromHos(main, Modifier.weight(1f))
            DriverCardFromHos(coDriver, Modifier.weight(1f))
        }
    } else {
        DriverCardFromHos(main, Modifier.fillMaxWidth())
    }
}

@Composable
private fun DriverCardFromHos(driver: HosDriverDto, modifier: Modifier) {
    val name = driver.name?.takeIf { it.isNotBlank() } ?: "—"
    // Metrics intentionally omitted for now (HOS + Km dropped per product
    // decision 2026-05-18). Bring back when the team aligns on which numbers
    // belong on this card and which endpoints feed them.
    DriverCard(
        initials = initialsFromName(name),
        name = name,
        status = driverStatusFromCode(driver.statusCode),
        statusLabel = driver.statusLabel,
        metrics = emptyList(),
        modifier = modifier,
    )
}

private fun initialsFromName(name: String): String {
    val words = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    val parts = when {
        words.isEmpty() -> listOf("—")
        words.size == 1 -> listOf(words[0].take(2))
        else -> listOf(words[0].take(1), words[1].take(1))
    }
    return parts.joinToString("").uppercase()
}
