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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xbiconnect.android.driver.R
import xbiconnect.android.driver.ui.components.DriverIcon
import xbiconnect.android.driver.ui.components.DriverIconName
import xbiconnect.android.driver.ui.theme.LocalAppColors

private data class ChatMsg(
    val id: Long,
    val fromMe: Boolean,
    val who: String,
    val text: String,
    val time: String,
)

@Composable
fun ScreenChat(onBack: () -> Unit, team: Boolean) {
    val c = LocalAppColors.current
    var input by rememberSaveable { mutableStateOf("") }
    var showVoice by remember { mutableStateOf(false) }
    val msgDispatch = stringResource(R.string.msg_dispatch_neal)
    val msgTeam = stringResource(R.string.msg_team_response)

    val messages = remember(team) {
        val initial = mutableListOf(
            ChatMsg(1, false, "Dispatch (Neal)", msgDispatch, "3:10 PM"),
        )
        if (team) initial.add(ChatMsg(2, true, "Roberto Luna (co-driver)", msgTeam, "3:12 PM"))
        initial.toMutableStateList()
    }

    Column(Modifier.fillMaxSize()) {
        ChatHeader(onBack = onBack, team = team)
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(c.surfaceSoft)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(c.line)
                            .padding(horizontal = 10.dp, vertical = 3.dp),
                    ) {
                        Text(stringResource(R.string.chat_today), color = c.textFaint, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            items(messages.size) { idx ->
                val m = messages[idx]
                MessageBubble(m)
            }
        }
        Composer(
            input = input,
            onInput = { input = it },
            onSend = {
                if (input.isNotBlank()) {
                    val author = if (team) "Roberto Luna (co-driver)" else "Carlos Méndez (driver)"
                    messages.add(ChatMsg(System.currentTimeMillis(), true, author, input, "ahora"))
                    input = ""
                }
            },
            onQuickSend = { txt ->
                val author = if (team) "Roberto Luna (co-driver)" else "Carlos Méndez (driver)"
                messages.add(ChatMsg(System.currentTimeMillis(), true, author, txt, "ahora"))
            },
            onMic = { showVoice = true },
            team = team,
        )
    }

    if (showVoice) {
        VoiceModal(
            onDismiss = { showVoice = false },
            onSend = { txt ->
                val author = if (team) "Roberto Luna (co-driver)" else "Carlos Méndez (driver)"
                messages.add(ChatMsg(System.currentTimeMillis(), true, author, txt, "ahora"))
                showVoice = false
            },
        )
    }
}

@Composable
private fun ChatHeader(onBack: () -> Unit, team: Boolean) {
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
        if (team) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(c.warnBg)
                    .border(1.dp, c.warnBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(stringResource(R.string.answering_as), color = c.warnText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Text(stringResource(R.string.chat_online), color = c.textMute, fontSize = 11.sp)
        }
    }
    Box(Modifier.fillMaxWidth().height(1.dp).background(c.line))
}

@Composable
private fun MessageBubble(m: ChatMsg) {
    val c = LocalAppColors.current
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (m.fromMe) Arrangement.End else Arrangement.Start,
    ) {
        val bg = if (m.fromMe) c.brand else c.surface
        val fg = if (m.fromMe) c.brandOn else c.text
        val whoFg = if (m.fromMe) c.warnDot else c.info
        val timeFg = if (m.fromMe) c.brandOn.copy(alpha = 0.55f) else c.textFaint
        val shape = if (m.fromMe)
            RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp)
        else
            RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)
        Column(
            Modifier
                .widthIn(max = 420.dp)
                .clip(shape)
                .background(bg)
                .let { if (!m.fromMe) it.border(1.dp, c.line, shape) else it }
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(m.who, color = whoFg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(3.dp))
            Text(m.text, color = fg, fontSize = 14.sp, lineHeight = 20.sp)
            Spacer(Modifier.height(4.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(m.time, color = timeFg, fontSize = 10.sp)
                if (m.fromMe) {
                    Spacer(Modifier.width(4.dp))
                    DriverIcon(DriverIconName.CHECK_ALL, size = 12.dp, color = Color(0xFF53BDEB), strokeWidth = 2.dp)
                }
            }
        }
    }
}

@Composable
private fun Composer(
    input: String,
    onInput: (String) -> Unit,
    onSend: () -> Unit,
    onQuickSend: (String) -> Unit,
    onMic: () -> Unit,
    team: Boolean,
) {
    val c = LocalAppColors.current
    Column(
        Modifier
            .fillMaxWidth()
            .background(c.surface)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            QuickChip(stringResource(R.string.chat_quick_got_it), c.goBg, c.goBorder, c.goText) { onQuickSend(it) }
            QuickChip(stringResource(R.string.chat_quick_arrived), c.goBg, c.goBorder, c.goText) { onQuickSend(it) }
            QuickChip(stringResource(R.string.chat_quick_delayed), c.warnBg, c.warnBorder, c.warnText) { onQuickSend(it) }
        }
        Spacer(Modifier.height(8.dp))
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
                        if (team) stringResource(R.string.chat_input_team) else stringResource(R.string.chat_input_placeholder),
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
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.brand)
                    .clickable(onClick = onSend),
                contentAlignment = Alignment.Center,
            ) {
                DriverIcon(DriverIconName.SEND, size = 18.dp, color = c.brandOn)
            }
        }
    }
}

@Composable
private fun QuickChip(text: String, bg: Color, border: Color, fg: Color, onClick: (String) -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.5.dp, border, RoundedCornerShape(10.dp))
            .clickable { onClick(text) }
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(text, color = fg, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
