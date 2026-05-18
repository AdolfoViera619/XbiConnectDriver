package xbiconnect.android.driver.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xbiconnect.android.driver.R
import xbiconnect.android.driver.data.DriverConfig
import xbiconnect.android.driver.data.DriverPreferences
import xbiconnect.android.driver.data.LocalDriverPreferences
import xbiconnect.android.driver.data.PairedVehicle
import xbiconnect.android.driver.data.Resource
import xbiconnect.android.driver.data.ServerMode
import xbiconnect.android.driver.data.network.ApiClient
import xbiconnect.android.driver.data.chat.ChatSessionHolder
import xbiconnect.android.driver.data.repository.ChatwootRepository
import xbiconnect.android.driver.data.repository.DriversRepository
import xbiconnect.android.driver.data.repository.VehiclePairingRepository
import xbiconnect.android.driver.data.state.ChatState
import xbiconnect.android.driver.data.state.DriverState
import xbiconnect.android.driver.data.state.PairingState
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

private const val DRIVER_REFRESH_INTERVAL_MS = 30_000L

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val prefs = remember { DriverPreferences(context.applicationContext) }
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
            LocalDriverPreferences provides prefs,
        ) {
            DriverApp()
        }
    }
}

@Composable
private fun DriverApp() {
    val c = LocalAppColors.current
    val prefs = LocalDriverPreferences.current
    val scope = rememberCoroutineScope()

    val pairingRepo = remember { VehiclePairingRepository(ApiClient.mainApi) }
    val driversRepo = remember { DriversRepository(ApiClient.mainApi) }

    val paired by prefs.pairedVehicle.collectAsState(initial = INITIAL_VEHICLE)

    var pairingState by remember { mutableStateOf<PairingState>(PairingState.Idle) }
    var stage by rememberSaveable { mutableStateOf<Stage?>(null) }
    var section by rememberSaveable { mutableStateOf(Section.HOME) }
    var isMoving by rememberSaveable { mutableStateOf(false) }
    var emergency by rememberSaveable { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }
    var driverState by remember { mutableStateOf<DriverState>(DriverState.Loading) }

    // Resolve the initial stage from persisted state. Sentinel guards against
    // running before DataStore has emitted (would briefly flash the wrong screen).
    LaunchedEffect(paired) {
        if (paired === INITIAL_VEHICLE) return@LaunchedEffect
        if (stage == null) {
            stage = if (paired == null) Stage.ONBOARD else Stage.APP
        }
    }

    // Refresh driver info while in the app shell. Uses a polling loop bound to
    // the paired VIN so it stops automatically on unlink.
    LaunchedEffect(stage, paired?.vin) {
        val vin = paired?.vin
        if (stage != Stage.APP || vin.isNullOrBlank()) return@LaunchedEffect
        while (true) {
            driverState = resolveDriverState(driversRepo.driversByVin(vin))
            delay(DRIVER_REFRESH_INTERVAL_MS)
        }
    }

    LaunchedEffect(toast) {
        if (toast != null) {
            delay(2200)
            toast = null
        }
    }

    Box(Modifier.fillMaxSize().background(c.pageBg)) {
        TabletFrame {
            when (stage) {
                null -> { /* waiting for DataStore to emit */ }
                Stage.ONBOARD -> ScreenOnboarding(
                    pairingState = pairingState,
                    onSearch = { vin ->
                        pairingState = PairingState.Loading
                        scope.launch {
                            pairingState = resolvePairingResult(
                                pairingRepo.validateVin(vin)
                            )
                            if (pairingState is PairingState.Found) {
                                stage = Stage.FOUND
                            }
                        }
                    },
                    onClearError = { pairingState = PairingState.Idle },
                )
                Stage.FOUND -> {
                    val found = pairingState as? PairingState.Found
                    if (found == null) {
                        // Defensive: somehow landed on FOUND without a payload — bounce back.
                        LaunchedEffect(Unit) { stage = Stage.ONBOARD }
                    } else {
                        ScreenTruckFound(
                            response = found.response,
                            onConfirm = {
                                scope.launch {
                                    val resp = found.response
                                    prefs.savePairing(
                                        vin = resp.vehicle?.vin ?: "",
                                        vehicle = resp.vehicle,
                                        customer = resp.customer,
                                        instanceUrl = resp.instanceUrl,
                                        database = resp.database,
                                    )
                                    pairingState = PairingState.Idle
                                    driverState = DriverState.Loading
                                    stage = Stage.APP
                                }
                            },
                            onReject = {
                                pairingState = PairingState.Idle
                                stage = Stage.ONBOARD
                            },
                        )
                    }
                }
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
                            paired = paired,
                            driverState = driverState,
                            onUnlink = {
                                scope.launch {
                                    prefs.clearPairing()
                                    driverState = DriverState.Loading
                                    section = Section.HOME
                                    stage = Stage.ONBOARD
                                }
                            },
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
    paired: PairedVehicle?,
    driverState: DriverState,
    onUnlink: () -> Unit,
) {
    val prefs = LocalDriverPreferences.current
    val serverMode by prefs.serverMode.collectAsState(initial = ServerMode.PROD)

    // Chat session lives at the shell so it persists across tab switches and
    // the WebSocket keeps streaming while the user is in Trip / Comunicados /
    // anything else. Disposed when AppShell leaves composition (unlink, etc.).
    val chatSession = rememberChatSession(serverMode, paired)

    // Bumping last_seen the moment the user enters Chat keeps the badge in
    // sync with the user's actual attention and matches what dispatchers see.
    LaunchedEffect(section, chatSession) {
        if (section == Section.CHAT) chatSession?.markRead()
    }

    // While the user is looking at Chat the badge should be 0 even if more
    // messages arrive — the unread-by-VIN count is correct, it's just not the
    // signal we want to render at that moment.
    val chatBadge = if (section == Section.CHAT) 0 else (chatSession?.unreadCount ?: 0)

    val items = listOf(
        RailItem(Section.HOME, DriverIconName.HOME, stringResource(R.string.nav_trip)),
        RailItem(Section.ANNOUNCEMENTS, DriverIconName.BELL, stringResource(R.string.nav_announcements), badge = 1),
        RailItem(Section.CHAT, DriverIconName.CHAT, stringResource(R.string.nav_chat), badge = chatBadge),
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
                    paired = paired,
                    driverState = driverState,
                )
                Section.ANNOUNCEMENTS -> ScreenAnnouncements()
                Section.CHAT -> ScreenChat(
                    onBack = { onSection(Section.HOME) },
                    state = chatSession?.state ?: ChatState.Error("Chat no disponible — la tableta no está enlazada o el servidor no devolvió inbox."),
                    onSend = { text -> chatSession?.send(text) },
                )
                Section.TEAM -> ScreenTeam()
                Section.SETTINGS -> ScreenSettings(onUnlink = onUnlink)
            }
        }
    }
}

@Composable
private fun rememberChatSession(
    serverMode: ServerMode,
    paired: PairedVehicle?,
): ChatSessionHolder? {
    val baseUrl = DriverConfig.resolveInstanceUrl(serverMode, paired)
    val inboxIdentifier = DriverConfig.resolveInboxIdentifier(serverMode, paired) ?: return null
    val sourceId = paired?.vin?.takeIf { it.isNotBlank() } ?: return null
    val contactName = paired.label?.let { "Truck $it" } ?: sourceId
    val attributes = buildContactAttributes(paired)
    val scope = rememberCoroutineScope()
    val holder = remember(baseUrl, inboxIdentifier, sourceId) {
        val repo = ChatwootRepository(
            api = ApiClient.instanceApi(baseUrl),
            inboxIdentifier = inboxIdentifier,
        )
        ChatSessionHolder(
            scope = scope,
            repo = repo,
            sourceId = sourceId,
            contactName = contactName,
            contactAttributes = attributes,
            wsBaseUrl = baseUrl,
        ).also { it.start() }
    }
    DisposableEffect(holder) {
        onDispose { holder.dispose() }
    }
    return holder
}

private fun buildContactAttributes(p: PairedVehicle): Map<String, Any?> = buildMap {
    put("vin", p.vin)
    p.label?.let { put("unit_number", it) }
    p.make?.let { put("make", it) }
    p.model?.let { put("model", it) }
    p.year?.let { put("year", it) }
}

private fun resolvePairingResult(
    result: Resource<xbiconnect.android.driver.data.api.dto.ValidateVinResponse>,
): PairingState = when (result) {
    is Resource.Success -> {
        val r = result.data
        if (r.success && r.vehicle != null) {
            PairingState.Found(r)
        } else {
            PairingState.Error(r.message ?: "VIN no encontrado.")
        }
    }
    is Resource.Error -> PairingState.Error(result.message)
    Resource.Idle, Resource.Loading -> PairingState.Loading
}

private fun resolveDriverState(
    result: Resource<xbiconnect.android.driver.data.api.dto.DriversByVinResponse>,
): DriverState = when (result) {
    is Resource.Success -> {
        val r = result.data
        when {
            !r.success -> DriverState.NoData(r.message)
            r.drivers?.main != null -> DriverState.Active(r.drivers.main, r.drivers.coDriver)
            else -> DriverState.NoData(r.message)
        }
    }
    is Resource.Error -> DriverState.Error(result.message)
    Resource.Idle, Resource.Loading -> DriverState.Loading
}

// Sentinel: collectAsState's initial value reference is used to distinguish
// "DataStore not yet emitted" from "no pairing saved".
private val INITIAL_VEHICLE: PairedVehicle? = PairedVehicle(
    vin = "__INITIAL__", vehicleId = null, label = null, make = null, model = null,
    year = null, driveLine = null, engine = null, customerId = null, customerName = null,
    customerLogoUrl = null, instanceUrl = null, apiToken = null, database = null,
    inboxIdentifier = null,
)
