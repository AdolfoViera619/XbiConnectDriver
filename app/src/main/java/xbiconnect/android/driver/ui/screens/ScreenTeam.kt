package xbiconnect.android.driver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import xbiconnect.android.driver.ui.components.DriverCard
import xbiconnect.android.driver.ui.components.DriverMetric
import xbiconnect.android.driver.ui.components.DriverStatus
import xbiconnect.android.driver.ui.components.HOSKind
import xbiconnect.android.driver.ui.components.HOSLegend
import xbiconnect.android.driver.ui.components.HOSSegment
import xbiconnect.android.driver.ui.components.HOSTimeline
import xbiconnect.android.driver.ui.components.InfoCallout
import xbiconnect.android.driver.ui.components.SystemBar
import xbiconnect.android.driver.ui.theme.LocalAppColors

@Composable
fun ScreenTeam() {
    val c = LocalAppColors.current
    Column(Modifier.fillMaxSize()) {
        SystemBar(title = "XBI Connect", subtitle = "Equipo · Unidad 45")
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
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DriverCard(
                    initials = "CM",
                    name = "Carlos Méndez",
                    status = DriverStatus.DRIVING,
                    metrics = listOf(
                        DriverMetric("HOS", "3:20"),
                        DriverMetric("Turno", "6:42"),
                        DriverMetric("Km hoy", "412"),
                    ),
                    modifier = Modifier.weight(1f),
                )
                DriverCard(
                    initials = "RL",
                    name = "Roberto Luna",
                    status = DriverStatus.SLEEPER,
                    metrics = listOf(
                        DriverMetric("Descanso", "4:15"),
                        DriverMetric("Disponible", "5:45", color = c.go),
                        DriverMetric("Km hoy", "208"),
                    ),
                    modifier = Modifier.weight(1f),
                )
            }
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
                    Text(stringResource(R.string.eld_sync_30s), color = c.textFaint, fontSize = 10.sp)
                }
                Spacer(Modifier.height(10.dp))
                HOSTimeline(
                    "Carlos M.",
                    listOf(
                        HOSSegment(HOSKind.REST, 22f),
                        HOSSegment(HOSKind.ON_DUTY, 8f),
                        HOSSegment(HOSKind.DRIVE, 28f),
                        HOSSegment(HOSKind.ON_DUTY, 4f),
                        HOSSegment(HOSKind.REST, 12f),
                        HOSSegment(HOSKind.DRIVE, 22f),
                        HOSSegment(HOSKind.ON_DUTY, 4f),
                    ),
                )
                Spacer(Modifier.height(10.dp))
                HOSTimeline(
                    "Roberto L.",
                    listOf(
                        HOSSegment(HOSKind.DRIVE, 24f),
                        HOSSegment(HOSKind.REST, 36f),
                        HOSSegment(HOSKind.ON_DUTY, 6f),
                        HOSSegment(HOSKind.DRIVE, 18f),
                        HOSSegment(HOSKind.REST, 16f),
                    ),
                )
                Spacer(Modifier.height(12.dp))
                HOSLegend()
            }
        }
        InfoCallout(stringResource(R.string.team_callout))
    }
}
