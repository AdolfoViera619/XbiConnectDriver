package xbiconnect.android.driver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import xbiconnect.android.driver.ui.theme.LocalAppColors

@Composable
fun TabletFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val c = LocalAppColors.current
    Column(
        modifier
            .fillMaxSize()
            .background(c.tabStatusBar)
            .systemBarsPadding(),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(c.tabBg),
        ) { content() }
    }
}
