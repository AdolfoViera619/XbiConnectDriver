package xbiconnect.android.driver.data.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import xbiconnect.android.driver.data.api.dto.MessageDto
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/** Events streamed by the Chatwoot ActionCable channel to the driver tablet. */
sealed interface DriverWsEvent {
    data class MessageReceived(val message: MessageDto, val conversationId: Int) : DriverWsEvent
    data class MessageUpdated(val message: MessageDto, val conversationId: Int) : DriverWsEvent
    data object Connected : DriverWsEvent
    data object Disconnected : DriverWsEvent
    data class Error(val reason: String) : DriverWsEvent
}

/**
 * Minimal Chatwoot ActionCable client for the Driver app — adapted from the
 * agent app's `ActionCableService` but simplified for the **Contact-based**
 * public Channel::Api model:
 *
 *  - No `accountId` / `userId`: the `pubsub_token` returned by
 *    `POST /public/.../contacts` is scoped enough to identify the channel.
 *  - We subscribe to a single `RoomChannel` per contact and listen for the
 *    `message.created` and `message.updated` events for any conversation
 *    that contact participates in.
 *  - Auto-reconnect with exponential backoff (up to 30s) for as long as
 *    [connect] hasn't been undone by [disconnect].
 *  - Exposes a [SharedFlow] so multiple collectors can listen (e.g. the
 *    chat screen for live messages, AppShell for the unread badge).
 *
 * Lifecycle: one instance per (baseUrl, pubsubToken) pair. Instantiate via
 * `remember(baseUrl, pubsubToken) { DriverActionCableService() }` in a
 * composable and pair it with a `DisposableEffect { onDispose { disconnect() } }`
 * so the WebSocket closes when the screen leaves composition.
 */
class DriverActionCableService(
    private val gson: Gson = Gson(),
) {

    companion object {
        private const val TAG = "DriverActionCable"
        private const val MAX_RECONNECT_DELAY_MS = 30_000L
    }

    private val wsClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.SECONDS) // ws stays open
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private var webSocket: WebSocket? = null
    @Volatile private var isConnected = false
    private val isConnecting = AtomicBoolean(false)
    private val shouldReconnect = AtomicBoolean(false)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var reconnectJob: Job? = null
    @Volatile private var reconnectAttempt = 0

    private var savedBaseUrl: String? = null
    private var savedPubsubToken: String? = null

    private val _events = MutableSharedFlow<DriverWsEvent>(
        replay = 0,
        extraBufferCapacity = 64,
    )
    val events: SharedFlow<DriverWsEvent> = _events.asSharedFlow()

    fun connect(baseUrl: String, pubsubToken: String) {
        if (isConnected) {
            // Late subscriber still gets a Connected event so it can sync UI.
            _events.tryEmit(DriverWsEvent.Connected)
            return
        }
        if (!isConnecting.compareAndSet(false, true)) return

        savedBaseUrl = baseUrl
        savedPubsubToken = pubsubToken
        shouldReconnect.set(true)

        val wsUrl = baseUrl
            .replace("https://", "wss://")
            .replace("http://", "ws://")
            .trimEnd('/') + "/cable"

        Log.d(TAG, "Connecting to $wsUrl")
        webSocket = wsClient.newWebSocket(
            Request.Builder().url(wsUrl).build(),
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    isConnected = true
                    isConnecting.set(false)
                    reconnectAttempt = 0
                    reconnectJob?.cancel()
                    _events.tryEmit(DriverWsEvent.Connected)
                    subscribe(pubsubToken)
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    handleIncoming(text)
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d(TAG, "ws closed: $code $reason")
                    isConnected = false
                    isConnecting.set(false)
                    _events.tryEmit(DriverWsEvent.Disconnected)
                    scheduleReconnect()
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e(TAG, "ws failure: ${t.message}", t)
                    isConnected = false
                    isConnecting.set(false)
                    _events.tryEmit(DriverWsEvent.Error(t.message ?: "unknown"))
                    scheduleReconnect()
                }
            },
        )
    }

    fun disconnect() {
        shouldReconnect.set(false)
        reconnectJob?.cancel()
        reconnectJob = null
        reconnectAttempt = 0
        webSocket?.close(1000, "driver disconnect")
        webSocket = null
        isConnected = false
        isConnecting.set(false)
        scope.cancel()
    }

    fun isConnected(): Boolean = isConnected

    private fun scheduleReconnect() {
        if (!shouldReconnect.get()) return
        val url = savedBaseUrl ?: return
        val token = savedPubsubToken ?: return

        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            val delayMs = min(MAX_RECONNECT_DELAY_MS, 1000L * (1L shl min(reconnectAttempt, 4)))
            reconnectAttempt++
            delay(delayMs)
            connect(url, token)
        }
    }

    private fun subscribe(pubsubToken: String) {
        // ActionCable wants `identifier` as a JSON-encoded STRING (not nested object).
        val identifierJson = gson.toJson(
            mapOf(
                "channel" to "RoomChannel",
                "pubsub_token" to pubsubToken,
            ),
        )
        val payload = JsonObject().apply {
            addProperty("command", "subscribe")
            addProperty("identifier", identifierJson)
        }
        webSocket?.send(payload.toString())
    }

    private fun handleIncoming(text: String) {
        try {
            val root = JsonParser.parseString(text).asJsonObject

            // ActionCable control frames — ignore but log for debugging.
            if (root.has("type")) {
                when (root.get("type").asString) {
                    "ping", "welcome" -> return
                    "confirm_subscription" -> { Log.d(TAG, "subscription confirmed"); return }
                    "reject_subscription" -> { Log.w(TAG, "subscription rejected"); return }
                }
            }

            // Application frames come wrapped under `message`.
            val message = root.getAsJsonObject("message") ?: return
            val event = message.get("event")?.asString ?: return
            val data = message.getAsJsonObject("data") ?: return

            when (event) {
                "message.created" -> emitMessageEvent(data, isUpdate = false)
                "message.updated" -> emitMessageEvent(data, isUpdate = true)
                else -> Log.v(TAG, "ignoring event $event")
            }
        } catch (e: Exception) {
            Log.e(TAG, "parse error: ${e.message}", e)
        }
    }

    private fun emitMessageEvent(data: JsonObject, isUpdate: Boolean) {
        val parsed = runCatching { gson.fromJson(data, MessageDto::class.java) }
            .getOrNull() ?: return
        val conversationId = data.get("conversation_id")?.asInt
            ?: parsed.conversationId.takeIf { it > 0 }
            ?: return
        val ev = if (isUpdate) {
            DriverWsEvent.MessageUpdated(parsed, conversationId)
        } else {
            DriverWsEvent.MessageReceived(parsed, conversationId)
        }
        _events.tryEmit(ev)
    }
}
