package xbiconnect.android.driver.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import xbiconnect.android.driver.R

val JakartaSans = FontFamily(
    Font(R.font.jakarta_regular, FontWeight.Normal),
    Font(R.font.jakarta_medium, FontWeight.Medium),
    Font(R.font.jakarta_semibold, FontWeight.SemiBold),
    Font(R.font.jakarta_bold, FontWeight.Bold),
    Font(R.font.jakarta_extrabold, FontWeight.ExtraBold),
)

val PlexMono = FontFamily(
    Font(R.font.plex_mono_regular, FontWeight.Normal),
    Font(R.font.plex_mono_medium, FontWeight.Medium),
    Font(R.font.plex_mono_bold, FontWeight.Bold),
)

val DriverTypography = Typography(
    displayLarge = TextStyle(fontFamily = JakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 84.sp, letterSpacing = (-3).sp),
    displayMedium = TextStyle(fontFamily = JakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 42.sp, letterSpacing = (-1).sp),
    headlineLarge = TextStyle(fontFamily = JakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontFamily = JakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, letterSpacing = (-0.3).sp),
    titleLarge = TextStyle(fontFamily = JakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp),
    titleMedium = TextStyle(fontFamily = JakartaSans, fontWeight = FontWeight.Bold, fontSize = 15.sp),
    titleSmall = TextStyle(fontFamily = JakartaSans, fontWeight = FontWeight.Bold, fontSize = 13.sp),
    bodyLarge = TextStyle(fontFamily = JakartaSans, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = JakartaSans, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = JakartaSans, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp),
    labelLarge = TextStyle(fontFamily = JakartaSans, fontWeight = FontWeight.Bold, fontSize = 13.sp),
    labelMedium = TextStyle(fontFamily = JakartaSans, fontWeight = FontWeight.Bold, fontSize = 11.sp),
    labelSmall = TextStyle(fontFamily = JakartaSans, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 0.5.sp),
)
