package xbiconnect.android.driver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xbiconnect.android.driver.ui.theme.LocalAppColors

@Composable
fun InfoCallout(text: String) {
    val c = LocalAppColors.current
    Box(Modifier.fillMaxWidth().height(1.dp).background(c.infoBorder))
    Row(
        Modifier
            .fillMaxWidth()
            .background(c.infoBg)
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DriverIcon(DriverIconName.INFO, size = 14.dp, color = c.infoText, strokeWidth = 2.dp)
        Spacer(Modifier.width(8.dp))
        Text(text, color = c.infoText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
