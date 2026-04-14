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
    primary = PurplePrimary,
    onPrimary = TextLight,
    primaryContainer = PurplePrimaryDark,
    onPrimaryContainer = TextLight,
    secondary = GreenSecondary,
    onSecondary = TextDark,
    secondaryContainer = GreenContainer,
    onSecondaryContainer = TextDark,
    background = DarkBackground,
    onBackground = TextLight,
    surface = DarkSurface,
    onSurface = TextLight,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = TextLight,
    outline = OutlineLight,
    error = RejectRed,
    onError = TextLight
)

private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = TextLight,
    primaryContainer = PurpleContainer,
    onPrimaryContainer = TextDark,
    secondary = GreenSecondary,
    onSecondary = TextLight,
    secondaryContainer = GreenContainer,
    onSecondaryContainer = TextDark,
    background = LightBackground,
    onBackground = TextDark,
    surface = LightSurface,
    onSurface = TextDark,
    surfaceVariant = LightSurface,
    onSurfaceVariant = TextDark,
    outline = OutlineLight,
    error = RejectRed,
    onError = TextLight
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
