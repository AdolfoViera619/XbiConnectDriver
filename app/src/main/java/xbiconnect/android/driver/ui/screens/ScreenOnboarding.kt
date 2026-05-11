package xbiconnect.android.driver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xbiconnect.android.driver.R
import xbiconnect.android.driver.ui.components.DriverIcon
import xbiconnect.android.driver.ui.components.DriverIconName
import xbiconnect.android.driver.ui.components.SystemBar
import xbiconnect.android.driver.ui.components.ThemeToggle
import xbiconnect.android.driver.ui.theme.LocalAppColors
import xbiconnect.android.driver.ui.theme.PlexMono

private const val VIN_LENGTH = 17

@Composable
fun ScreenOnboarding(onFound: (String) -> Unit) {
    val c = LocalAppColors.current
    var vin by rememberSaveable { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        SystemBar(
            title = "XBI Connect",
            unit = null,
            subtitle = stringResource(R.string.setup_subtitle),
            right = { ThemeToggle() },
        )
        Row(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(c.tabBg)
                .padding(28.dp),
        ) {
            // Left — VIN input
            Column(
                Modifier
                    .weight(1f)
                    .padding(end = 28.dp),
            ) {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(c.canvas)
                        .border(2.dp, c.line, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    DriverIcon(DriverIconName.TRUCK, size = 32.dp, color = c.brand, strokeWidth = 1.8.dp)
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    stringResource(R.string.onboarding_title),
                    color = c.text,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    stringResource(R.string.onboarding_sub),
                    color = c.textMute,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                )
                Spacer(Modifier.height(22.dp))
                VinInput(
                    value = vin,
                    onValueChange = { input ->
                        vin = input.uppercase().filter { it.isLetterOrDigit() }.take(VIN_LENGTH)
                    },
                    onSubmit = { if (vin.length == VIN_LENGTH) onFound(vin) },
                )
                Spacer(Modifier.height(18.dp))
                SearchButton(onClick = { onFound(vin) }, enabled = vin.length == VIN_LENGTH)
            }

            // Vertical divider
            Box(
                Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(c.line),
            )
            Spacer(Modifier.width(28.dp))

            // Right — Help
            Column(Modifier.width(280.dp).verticalScroll(rememberScrollState())) {
                Text(
                    stringResource(R.string.vin_help_title),
                    color = c.text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
                Spacer(Modifier.height(12.dp))
                HelpItem(
                    n = "1",
                    title = stringResource(R.string.vin_help_1_title),
                    body = stringResource(R.string.vin_help_1_body),
                )
                Spacer(Modifier.height(10.dp))
                HelpItem(
                    n = "2",
                    title = stringResource(R.string.vin_help_2_title),
                    body = stringResource(R.string.vin_help_2_body),
                )
                Spacer(Modifier.height(10.dp))
                HelpItem(
                    n = "3",
                    title = stringResource(R.string.vin_help_3_title),
                    body = stringResource(R.string.vin_help_3_body),
                )
            }
        }
    }
}

@Composable
private fun VinInput(value: String, onValueChange: (String) -> Unit, onSubmit: () -> Unit) {
    val c = LocalAppColors.current
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    stringResource(R.string.vin_placeholder),
                    color = c.textFaint,
                    fontFamily = PlexMono,
                )
            },
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = PlexMono,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = c.text,
                letterSpacing = 2.sp,
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = c.brand,
                unfocusedBorderColor = c.border,
                focusedContainerColor = c.surfaceSoft,
                unfocusedContainerColor = c.surfaceSoft,
                cursorColor = c.brand,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(6.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                stringResource(R.string.vin_label),
                color = c.textMute,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                stringResource(R.string.vin_counter, value.length),
                color = if (value.length == VIN_LENGTH) c.go else c.textMute,
                fontSize = 11.sp,
                fontFamily = PlexMono,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SearchButton(onClick: () -> Unit, enabled: Boolean) {
    val c = LocalAppColors.current
    Row(
        Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (enabled) c.brand else c.brand.copy(alpha = 0.4f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 28.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DriverIcon(DriverIconName.SEARCH, size = 18.dp, color = c.brandOn, strokeWidth = 2.dp)
        Spacer(Modifier.width(8.dp))
        Text(stringResource(R.string.btn_search), color = c.brandOn, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
private fun HelpItem(n: String, title: String, body: String) {
    val c = LocalAppColors.current
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.surfaceSoft)
            .border(1.dp, c.line, RoundedCornerShape(12.dp))
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(c.infoBg),
                contentAlignment = Alignment.Center,
            ) {
                Text(n, color = c.infoText, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.width(8.dp))
            Text(title, color = c.text, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            body,
            color = c.textMute,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            modifier = Modifier.padding(start = 32.dp),
        )
    }
}
