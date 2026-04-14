package dk.soerensen.chorechamp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DragonGold,
    secondary = DragonGreen,
    tertiary = ChoreBlue,
    background = BackgroundDark,
    surface = BackgroundMedium,
    onPrimary = BackgroundDark,
    onSecondary = TextOnDark,
    onTertiary = TextOnDark,
    onBackground = TextOnDark,
    onSurface = TextOnDark,
    primaryContainer = DragonGoldLight,
    secondaryContainer = DragonGreenLight,
    tertiaryContainer = ChoreBlueLight,
    onPrimaryContainer = BackgroundDark,
    onSecondaryContainer = BackgroundDark,
    onTertiaryContainer = BackgroundDark,
    error = RejectRed,
    onError = TextOnDark
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = DragonGoldLight,
    surface = SurfaceCard,
    onPrimary = TextOnDark,
    onSecondary = TextOnDark,
    onTertiary = TextOnDark,
    onBackground = BackgroundDark,
    onSurface = BackgroundDark,
    primaryContainer = Purple80,
    secondaryContainer = PurpleGrey80,
    tertiaryContainer = Pink80
)

@Composable
fun ChoreChampTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
