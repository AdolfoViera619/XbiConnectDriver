package xbiconnect.android.driver.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay
import xbiconnect.android.driver.R
import xbiconnect.android.driver.ui.components.DriverIconName
import xbiconnect.android.driver.ui.components.QuickToast
import xbiconnect.android.driver.ui.components.RailItem
import xbiconnect.android.driver.ui.components.SideRail
import xbiconnect.android.driver.ui.components.TabletFrame
import xbiconnect.android.driver.ui.nav.Section
import xbiconnect.android.driver.ui.nav.Stage
import xbiconnect.android.driver.ui.screens.ScreenAnnouncements
import xbiconnect.android.driver.ui.screens.ScreenChat
import xbiconnect.android.driver.ui.screens.ScreenDrivingLock
import xbiconnect.android.driver.ui.screens.ScreenEmergencyOverlay
import xbiconnect.android.driver.ui.screens.ScreenOnboarding
import xbiconnect.android.driver.ui.screens.ScreenSettings
import xbiconnect.android.driver.ui.screens.ScreenTeam
import xbiconnect.android.driver.ui.screens.ScreenTrip
import xbiconnect.android.driver.ui.screens.ScreenTruckFound
import xbiconnect.android.driver.ui.theme.LocalAppColors
import xbiconnect.android.driver.ui.theme.LocalThemeController
import xbiconnect.android.driver.ui.theme.ThemeController
import xbiconnect.android.driver.ui.theme.ThemeMode
import xbiconnect.android.driver.ui.theme.XbiConnectDriverTheme

@Composable
fun AppRoot() {
    var mode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
    val systemDark = isSystemInDarkTheme()
    val isLight = when (mode) {
        ThemeMode.LIGHT -> true
        ThemeMode.DARK -> false
        ThemeMode.SYSTEM -> !systemDark
    }
    XbiConnectDriverTheme(isLight = isLight) {
        CompositionLocalProvider(
            LocalThemeController provides ThemeController(mode) { mode = it },
        ) {
            DriverApp()
        }
    }
}

@Composable
private fun DriverApp() {
    val c = LocalAppColors.current
    var stage by rememberSaveable { mutableStateOf(Stage.ONBOARD) }
    var section by rememberSaveable { mutableStateOf(Section.HOME) }
    var isMoving by rememberSaveable { mutableStateOf(false) }
    var emergency by rememberSaveable { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(toast) {
        if (toast != null) {
            delay(2200)
            toast = null
        }
    }

    Box(Modifier.fillMaxSize().background(c.pageBg)) {
        TabletFrame {
            when (stage) {
                Stage.ONBOARD -> ScreenOnboarding(onFound = { stage = Stage.FOUND })
                Stage.FOUND -> ScreenTruckFound(
                    onConfirm = { stage = Stage.APP },
                    onReject = { stage = Stage.ONBOARD },
                )
                Stage.APP -> {
                    if (isMoving) {
                        val sentText = stringResource(R.string.toast_sent)
                        ScreenDrivingLock(
                            onUnlock = { isMoving = false },
                            onEmergency = { emergency = true },
                            onVoice = {
                                isMoving = false
                                section = Section.CHAT
                            },
                            onQuickReply = { msg -> toast = sentText.format(msg) },
                        )
                    } else {
                        AppShell(
                            section = section,
                            onSection = { section = it },
                            onSimulateDrive = { isMoving = true },
                            onEmergency = { emergency = true },
                        )
                    }
                }
            }
        }
        if (emergency) {
            ScreenEmergencyOverlay(onClose = { emergency = false })
        }
        toast?.let { QuickToast(text = it) }
    }
}

@Composable
private fun AppShell(
    section: Section,
    onSection: (Section) -> Unit,
    onSimulateDrive: () -> Unit,
    onEmergency: () -> Unit,
) {
    val items = listOf(
        RailItem(Section.HOME, DriverIconName.HOME, stringResource(R.string.nav_trip)),
        RailItem(Section.ANNOUNCEMENTS, DriverIconName.BELL, stringResource(R.string.nav_announcements), badge = 1),
        RailItem(Section.CHAT, DriverIconName.CHAT, stringResource(R.string.nav_chat), badge = 2),
        RailItem(Section.TEAM, DriverIconName.USERS, stringResource(R.string.nav_team)),
        RailItem(Section.SETTINGS, DriverIconName.SETTINGS, stringResource(R.string.nav_settings)),
    )
    Row(Modifier.fillMaxSize()) {
        SideRail(
            items = items,
            active = section,
            onChange = onSection,
            sosLabel = stringResource(R.string.nav_sos),
            onSos = onEmergency,
        )
        Box(Modifier.fillMaxSize()) {
            when (section) {
                Section.HOME -> ScreenTrip(
                    onOpenChat = { onSection(Section.CHAT) },
                    onOpenAnnouncement = { onSection(Section.ANNOUNCEMENTS) },
                    onSimulateDrive = onSimulateDrive,
                )
                Section.ANNOUNCEMENTS -> ScreenAnnouncements()
                Section.CHAT -> ScreenChat(
                    onBack = { onSection(Section.HOME) },
                    team = false,
                )
                Section.TEAM -> ScreenTeam()
                Section.SETTINGS -> ScreenSettings()
            }
        }
    }
}
