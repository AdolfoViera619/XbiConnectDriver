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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import xbiconnect.android.driver.R
import xbiconnect.android.driver.data.api.dto.MessageDto
import xbiconnect.android.driver.data.api.dto.MessageType
import xbiconnect.android.driver.data.state.ChatState
import xbiconnect.android.driver.ui.components.DriverIcon
import xbiconnect.android.driver.ui.components.DriverIconName
import xbiconnect.android.driver.ui.theme.LocalAppColors

/**
 * Pure renderer for the chat. All state and side effects (initial load,
 * WebSocket subscription, optimistic sends, mark-as-read) live in
 * [xbiconnect.android.driver.data.chat.ChatSessionHolder] up at the AppShell
 * level so they survive tab switches.
 *
 * This screen is intentionally dumb: it draws [state] and forwards user
 * actions through [onSend].
 */
@Composable
fun ScreenChat(
    onBack: () -> Unit,
    state: ChatState,
    onSend: (String) -> Unit,
) {
    val c = LocalAppColors.current
    var input by rememberSaveable { mutableStateOf("") }
    var showVoice by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        ChatHeader(onBack = onBack)
        when (state) {
            ChatState.Idle, ChatState.Loading -> LoadingPanel()
            is ChatState.Error -> ErrorPanel(state.message)
            is ChatState.Ready -> {
                MessagesList(
                    messages = state.messages,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(c.surfaceSoft),
                )
                Composer(
                    input = input,
                    onInput = { input = it },
                    sending = state.sending,
                    onSend = {
                        val text = input.trim()
                        if (text.isEmpty()) return@Composer
                        input = ""
                        onSend(text)
                    },
                    onMic = { showVoice = true },
                )
            }
        }
    }

    if (showVoice) {
        VoiceModal(
            onDismiss = { showVoice = false },
            onSend = { dictated ->
                onSend(dictated)
                showVoice = false
            },
        )
    }
}

@Composable
private fun ChatHeader(onBack: () -> Unit) {
    val c = LocalAppColors.current
    Row(
        Modifier
            .fillMaxWidth()
            .background(c.tabStatusBar)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            Modifier.clickable(onClick = onBack),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DriverIcon(DriverIconName.CHEV_L, size = 16.dp, color = c.text, strokeWidth = 2.5.dp)
            Spacer(Modifier.width(4.dp))
            Text(stringResource(R.string.chat_back), color = c.text, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Text(stringResource(R.string.chat_header), color = c.text, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(stringResource(R.string.chat_online), color = c.textMute, fontSize = 11.sp)
    }
    Box(Modifier.fillMaxWidth().height(1.dp).background(c.line))
}

@Composable
private fun LoadingPanel() {
    val c = LocalAppColors.current
    Box(
        Modifier.fillMaxSize().background(c.surfaceSoft),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = c.brand, strokeWidth = 2.5.dp)
            Spacer(Modifier.height(12.dp))
            Text("Cargando mensajes…", color = c.textMute, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ErrorPanel(message: String) {
    val c = LocalAppColors.current
    Box(
        Modifier.fillMaxSize().background(c.surfaceSoft).padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DriverIcon(DriverIconName.ALERT, size = 32.dp, color = c.warn, strokeWidth = 2.dp)
            Spacer(Modifier.height(12.dp))
            Text(
                message,
                color = c.text,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun MessagesList(messages: List<MessageDto>, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }
    LazyColumn(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(messages.size) { idx ->
            MessageBubble(messages[idx])
        }
    }
}

@Composable
private fun MessageBubble(m: MessageDto) {
    val c = LocalAppColors.current
    // Public Channel::Api convention: INCOMING = from the contact (the truck).
    val fromTruck = m.messageType == MessageType.INCOMING
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (fromTruck) Arrangement.End else Arrangement.Start,
    ) {
        val bg = if (fromTruck) c.brand else c.surface
        val fg = if (fromTruck) c.brandOn else c.text
        val timeFg = if (fromTruck) c.brandOn.copy(alpha = 0.55f) else c.textFaint
        val whoFg = if (fromTruck) c.warnDot else c.info
        val shape = if (fromTruck)
            RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp)
        else
            RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)
        Column(
            Modifier
                .widthIn(max = 420.dp)
                .clip(shape)
                .background(bg)
                .let { if (!fromTruck) it.border(1.dp, c.line, shape) else it }
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            val who = m.sender?.name ?: if (fromTruck) "Tú" else "Despacho"
            Text(who, color = whoFg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(3.dp))
            Text(m.content.orEmpty(), color = fg, fontSize = 14.sp, lineHeight = 20.sp)
            Spacer(Modifier.height(4.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(formatTime(m.createdAt), color = timeFg, fontSize = 10.sp)
                if (fromTruck) {
                    Spacer(Modifier.width(4.dp))
                    DriverIcon(DriverIconName.CHECK_ALL, size = 12.dp, color = Color(0xFF53BDEB), strokeWidth = 2.dp)
                }
            }
        }
    }
}

private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

private fun formatTime(unixSeconds: Long): String =
    if (unixSeconds <= 0) "ahora" else timeFmt.format(Date(unixSeconds * 1000))

@Composable
private fun Composer(
    input: String,
    onInput: (String) -> Unit,
    sending: Boolean,
    onSend: () -> Unit,
    onMic: () -> Unit,
) {
    val c = LocalAppColors.current
    Column(
        Modifier
            .fillMaxWidth()
            .background(c.surface)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.canvas)
                    .border(1.5.dp, c.line, RoundedCornerShape(12.dp))
                    .clickable(onClick = onMic),
                contentAlignment = Alignment.Center,
            ) {
                DriverIcon(DriverIconName.MIC, size = 18.dp, color = c.text)
            }
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.canvas)
                    .border(1.5.dp, c.line, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (input.isEmpty()) {
                    Text(
                        stringResource(R.string.chat_input_placeholder),
                        color = c.textFaint,
                        fontSize = 14.sp,
                    )
                }
                BasicTextField(
                    value = input,
                    onValueChange = onInput,
                    textStyle = TextStyle(color = c.text, fontSize = 14.sp),
                    cursorBrush = SolidColor(c.brand),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSend() }),
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (sending) c.brand.copy(alpha = 0.4f) else c.brand)
                    .clickable(enabled = !sending, onClick = onSend),
                contentAlignment = Alignment.Center,
            ) {
                if (sending) {
                    CircularProgressIndicator(
                        color = c.brandOn,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp),
                    )
                } else {
                    DriverIcon(DriverIconName.SEND, size = 18.dp, color = c.brandOn)
                }
            }
        }
    }
}
