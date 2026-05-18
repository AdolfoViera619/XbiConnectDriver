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
import androidx.compose.foundation.layout.height
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
import xbiconnect.android.driver.ui.components.DriverMetric
import xbiconnect.android.driver.ui.components.DriverStatus
import xbiconnect.android.driver.ui.components.HOSKind
import xbiconnect.android.driver.ui.components.HOSLegend
import xbiconnect.android.driver.ui.components.HOSSegment
import xbiconnect.android.driver.ui.components.HOSTimeline
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

            HosTimelineBlock(driverState as? DriverState.Active)
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
    DriverCard(
        initials = initialsFromName(name),
        name = name,
        status = driverStatusFromCode(driver.statusCode),
        statusLabel = driver.statusLabel,
        metrics = listOfNotNull(
            driver.drivingTime?.takeIf { it.isNotBlank() }?.let { DriverMetric("HOS", it) },
            driver.onDutyTime?.takeIf { it.isNotBlank() }?.let { DriverMetric("Turno", it) },
        ),
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

@Composable
private fun HosTimelineBlock(active: DriverState.Active?) {
    val c = xbiconnect.android.driver.ui.theme.LocalAppColors.current
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.surface)
            .border(1.dp, c.line, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                stringResource(R.string.hos_24h),
                color = c.textMute,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            // TODO: real 24h HOS log endpoint pending from backend. Until then
            // we keep the demo segments so the UI doesn't go blank — the
            // labels make the names match the actual drivers when available.
            Text("Datos de ejemplo · pendiente del server", color = c.textFaint, fontSize = 10.sp)
        }
        Spacer(Modifier.height(10.dp))
        HOSTimeline(
            active?.main?.name ?: "Chofer principal",
            DEMO_TIMELINE_PRIMARY,
        )
        Spacer(Modifier.height(10.dp))
        HOSTimeline(
            active?.coDriver?.name ?: "Co-chofer",
            DEMO_TIMELINE_SECONDARY,
        )
        Spacer(Modifier.height(12.dp))
        HOSLegend()
    }
}

// Demo segments kept verbatim from the original UI design. Will be replaced
// once the server exposes a per-driver HOS history endpoint.
private val DEMO_TIMELINE_PRIMARY = listOf(
    HOSSegment(HOSKind.REST, 22f),
    HOSSegment(HOSKind.ON_DUTY, 8f),
    HOSSegment(HOSKind.DRIVE, 28f),
    HOSSegment(HOSKind.ON_DUTY, 4f),
    HOSSegment(HOSKind.REST, 12f),
    HOSSegment(HOSKind.DRIVE, 22f),
    HOSSegment(HOSKind.ON_DUTY, 4f),
)
private val DEMO_TIMELINE_SECONDARY = listOf(
    HOSSegment(HOSKind.DRIVE, 24f),
    HOSSegment(HOSKind.REST, 36f),
    HOSSegment(HOSKind.ON_DUTY, 6f),
    HOSSegment(HOSKind.DRIVE, 18f),
    HOSSegment(HOSKind.REST, 16f),
)
