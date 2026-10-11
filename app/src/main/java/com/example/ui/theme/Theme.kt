package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val EliasLightColorScheme = lightColorScheme(
    primary = Violet600,
    onPrimary = Color.White,
    primaryContainer = Violet100,
    onPrimaryContainer = Violet700,
    secondary = Pink500,
    onSecondary = Color.White,
    secondaryContainer = Pink50,
    onSecondaryContainer = Pink600,
    tertiary = Emerald500,
    onTertiary = Color.White,
    background = Violet50,
    onBackground = Slate800,
    surface = Color.White,
    onSurface = Slate800,
    surfaceVariant = Slate50,
    onSurfaceVariant = Slate600,
    outline = Slate300,
    error = Red500,
    onError = Color.White
)

val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp)
)

@Composable
fun EliasQrTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EliasLightColorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
