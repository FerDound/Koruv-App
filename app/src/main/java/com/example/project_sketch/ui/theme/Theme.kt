package com.example.project_sketch.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = KoruvAzul,
    secondary = KoruvNaranja,
    background = FondoClaro,
    surface = SuperficieClaro,
    onBackground = TextoPrimarioClaro,
    onSurface = TextoPrimarioClaro,
    outline = BordeClaro,
    onSurfaceVariant = TextoSecundarioClaro,
    surfaceVariant = SuperficieClaro,
    tertiary = TextoTercioClaro,
)

private val DarkColorScheme = darkColorScheme(
    primary = KoruvAzul,
    secondary = KoruvNaranja,
    background = FondoOscuro,
    surface = SuperficieOscuro,
    onBackground = TextoPrimarioOscuro,
    onSurface = TextoPrimarioOscuro,
    outline = BordeOscuro,
    onSurfaceVariant = TextoSecundarioOscuro,
    surfaceVariant = SuperficieOscuro,
    tertiary = TextoTercioOscuro,
)

@Composable
fun Project_sketchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
