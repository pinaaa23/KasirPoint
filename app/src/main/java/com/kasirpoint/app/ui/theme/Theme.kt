package com.kasirpoint.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = SurfaceWhite,
    primaryContainer = PrimaryOrangeLight,
    onPrimaryContainer = TextDark,
    secondary = ActionBrown,
    onSecondary = SurfaceWhite,
    secondaryContainer = ChipPeach,
    onSecondaryContainer = TextDark,
    background = BackgroundPeach,
    onBackground = TextDark,
    surface = SurfaceWhite,
    onSurface = TextDark,
    surfaceVariant = SurfaceWhite,
    onSurfaceVariant = TextMuted,
    outline = BorderLight
)

@Composable
fun KasirPointTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Application uses warm theme universally
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
