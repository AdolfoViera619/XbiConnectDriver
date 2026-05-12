package xbiconnect.android.driver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import xbiconnect.android.driver.ui.theme.LocalAppColors

enum class DriverStatus { DRIVING, SLEEPER, AVAILABLE, OFF }

/**
 * Maps the raw ELD duty status code to a [DriverStatus] for visual styling.
 * Server-side `current_duty_status_label` should be passed separately to
 * [DriverBadge.label] so the wording on screen matches what the backend chose.
 *
 * Codes seen so far: `D` (Driving), `OFF` (Off Duty), `SB` (Sleeper Berth).
 * `ON` (On Duty) is part of the ELD standard but hasn't been observed yet —
 * mapped to AVAILABLE which uses the info-blue palette.
 */
fun driverStatusFromCode(code: String?): DriverStatus = when (code?.uppercase()) {
    "D" -> DriverStatus.DRIVING
    "ON" -> DriverStatus.AVAILABLE
    "OFF" -> DriverStatus.OFF
    "SB" -> DriverStatus.SLEEPER
    else -> DriverStatus.OFF
}

private data class BadgeStyle(val bg: Color, val border: Color, val fg: Color, val dot: Color, val label: String)

@Composable
private fun styleFor(s: DriverStatus): BadgeStyle {
    val c = LocalAppColors.current
    return when (s) {
        DriverStatus.DRIVING -> BadgeStyle(c.goBg, c.goBorder, c.goText, c.goDot, "Driving")
        DriverStatus.SLEEPER -> BadgeStyle(c.warnBg, c.warnBorder, c.warnText, c.warnDot, "Sleeper")
        DriverStatus.AVAILABLE -> BadgeStyle(c.infoBg, c.infoBorder, c.infoText, c.info, "Available")
        DriverStatus.OFF -> BadgeStyle(c.canvas, c.line, c.textMute, c.textFaint, "Off")
    }
}

@Composable
fun DriverBadge(
    status: DriverStatus,
    name: String? = null,
    label: String? = null,
    compact: Boolean = false,
) {
    val s = styleFor(status)
    val displayLabel = label?.takeIf { it.isNotBlank() } ?: s.label
    Row(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(s.bg)
            .border(1.dp, s.border, RoundedCornerShape(8.dp))
            .padding(
                horizontal = if (compact) 8.dp else 10.dp,
                vertical = if (compact) 3.dp else 4.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(if (compact) 6.dp else 8.dp)
                .clip(CircleShape)
                .background(s.dot),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = if (name != null) "$name — $displayLabel" else displayLabel,
            color = s.fg,
            fontSize = if (compact) 9.sp else 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
