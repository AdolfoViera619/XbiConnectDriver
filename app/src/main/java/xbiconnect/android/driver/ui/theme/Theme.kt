package xbiconnect.android.driver.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColors(
    val isLight: Boolean,
    val pageBg: Color,
    val pageText: Color,
    val pageMuted: Color,
    val tabBg: Color,
    val tabBorder: Color,
    val tabStatusBar: Color,
    val tabStatusBarFg: Color,
    val canvas: Color,
    val surface: Color,
    val surfaceSoft: Color,
    val line: Color,
    val border: Color,
    val text: Color,
    val textMute: Color,
    val textFaint: Color,
    val textSecondary: Color,
    val brand: Color,
    val brandOn: Color,
    val go: Color,
    val goBg: Color,
    val goBgSoft: Color,
    val goBorder: Color,
    val goText: Color,
    val goDot: Color,
    val warn: Color,
    val warnBg: Color,
    val warnBgSoft: Color,
    val warnBorder: Color,
    val warnText: Color,
    val warnDot: Color,
    val info: Color,
    val infoBg: Color,
    val infoBorder: Color,
    val infoText: Color,
    val stop: Color,
    val stopBg: Color,
    val stopBorder: Color,
)

val LightAppColors = AppColors(
    isLight = true,
    pageBg = DriverPalette.PageBg,
    pageText = DriverPalette.PageText,
    pageMuted = DriverPalette.PageMuted,
    tabBg = DriverPalette.TabBg,
    tabBorder = DriverPalette.TabBorder,
    tabStatusBar = DriverPalette.TabStatusBar,
    tabStatusBarFg = DriverPalette.TabStatusBarFg,
    canvas = DriverPalette.Canvas,
    surface = DriverPalette.Surface,
    surfaceSoft = DriverPalette.SurfaceSoft,
    line = DriverPalette.Line,
    border = DriverPalette.Border,
    text = DriverPalette.Text,
    textMute = DriverPalette.TextMute,
    textFaint = DriverPalette.TextFaint,
    textSecondary = DriverPalette.TextSecondary,
    brand = DriverPalette.Navy,
    brandOn = Color.White,
    go = DriverPalette.Go,
    goBg = DriverPalette.GoBg,
    goBgSoft = DriverPalette.GoBgSoft,
    goBorder = DriverPalette.GoBorder,
    goText = DriverPalette.GoText,
    goDot = DriverPalette.GoDot,
    warn = DriverPalette.Warn,
    warnBg = DriverPalette.WarnBg,
    warnBgSoft = DriverPalette.WarnBgSoft,
    warnBorder = DriverPalette.WarnBorder,
    warnText = DriverPalette.WarnText,
    warnDot = DriverPalette.WarnDot,
    info = DriverPalette.Info,
    infoBg = DriverPalette.InfoBg,
    infoBorder = DriverPalette.InfoBorder,
    infoText = DriverPalette.InfoText,
    stop = DriverPalette.Stop,
    stopBg = DriverPalette.StopBg,
    stopBorder = DriverPalette.StopBorder,
)

val DarkAppColors = AppColors(
    isLight = false,
    pageBg = DriverPalette.PageBg,
    pageText = DriverPalette.PageText,
    pageMuted = DriverPalette.PageMuted,
    tabBg = DriverPalette.TabNightBg,
    tabBorder = DriverPalette.TabNightBorder,
    tabStatusBar = DriverPalette.TabNightStatusBar,
    tabStatusBarFg = DriverPalette.TabNightStatusBarFg,
    canvas = DriverPalette.NightBgApp,
    surface = DriverPalette.NightSurface,
    surfaceSoft = DriverPalette.NightElevated,
    line = DriverPalette.NightBorder,
    border = DriverPalette.NightBorderStrong,
    text = DriverPalette.NightText,
    textMute = DriverPalette.NightTextMute,
    textFaint = DriverPalette.NightTextFaint,
    textSecondary = DriverPalette.NightTextMute,
    brand = DriverPalette.Navy,
    brandOn = Color.White,
    go = DriverPalette.Go,
    goBg = Color(0xFF14321F),
    goBgSoft = Color(0xFF0F2618),
    goBorder = Color(0xFF1E5132),
    goText = Color(0xFF86EFAC),
    goDot = DriverPalette.GoDot,
    warn = DriverPalette.Warn,
    warnBg = Color(0xFF3A2A0E),
    warnBgSoft = Color(0xFF2A1F0A),
    warnBorder = Color(0xFF5C4015),
    warnText = Color(0xFFFCD34D),
    warnDot = DriverPalette.WarnDot,
    info = DriverPalette.Info,
    infoBg = Color(0xFF132034),
    infoBorder = Color(0xFF1E3258),
    infoText = Color(0xFF93C5FD),
    stop = DriverPalette.Stop,
    stopBg = Color(0xFF3A1414),
    stopBorder = Color(0xFF6B1F1F),
)

val LocalAppColors = compositionLocalOf { LightAppColors }

enum class ThemeMode { LIGHT, DARK, SYSTEM }

@Immutable
data class ThemeController(
    val mode: ThemeMode,
    val setMode: (ThemeMode) -> Unit,
)

val LocalThemeController = androidx.compose.runtime.staticCompositionLocalOf<ThemeController> {
    error("ThemeController not provided")
}

@Composable
fun XbiConnectDriverTheme(
    isLight: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colors = if (isLight) LightAppColors else DarkAppColors
    val scheme = if (isLight) {
        lightColorScheme(
            primary = colors.brand,
            onPrimary = colors.brandOn,
            secondary = colors.info,
            onSecondary = Color.White,
            background = colors.canvas,
            onBackground = colors.text,
            surface = colors.surface,
            onSurface = colors.text,
            surfaceVariant = colors.surfaceSoft,
            onSurfaceVariant = colors.textMute,
            error = colors.stop,
            onError = Color.White,
            outline = colors.line,
        )
    } else {
        darkColorScheme(
            primary = colors.brand,
            onPrimary = colors.brandOn,
            secondary = colors.info,
            onSecondary = Color.White,
            background = colors.canvas,
            onBackground = colors.text,
            surface = colors.surface,
            onSurface = colors.text,
            surfaceVariant = colors.surfaceSoft,
            onSurfaceVariant = colors.textMute,
            error = colors.stop,
            onError = Color.White,
            outline = colors.line,
        )
    }
    androidx.compose.runtime.CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            colorScheme = scheme,
            typography = DriverTypography,
            content = content,
        )
    }
}
