package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = FlameOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF381508),
    onPrimaryContainer = FlameOrangeLight,
    secondary = ElectricCyan,
    onSecondary = Color(0xFF001A20),
    secondaryContainer = Color(0xFF003844),
    onSecondaryContainer = ElectricCyan,
    tertiary = NeonLime,
    onTertiary = Color(0xFF002200),
    tertiaryContainer = Color(0xFF143800),
    onTertiaryContainer = NeonLime,
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = DarkSurfaceBorder,
    error = RoastRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
