package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = ForestGreenLight,
  onPrimary = Color.White,
  primaryContainer = ForestGreenPrimary,
  onPrimaryContainer = Color.White,
  secondary = WarmGoldAccent,
  onSecondary = Color.Black,
  tertiary = TerracottaTertiary,
  background = DarkPineBackground,
  onBackground = Color(0xFFE4EDE7),
  surface = DarkPineSurface,
  onSurface = Color(0xFFE4EDE7),
  surfaceVariant = DarkSurfaceContainerHigh,
  onSurfaceVariant = Color(0xFFB8C7BF)
)

private val LightColorScheme = lightColorScheme(
  primary = ForestGreenPrimary,
  onPrimary = Color.White,
  primaryContainer = ForestGreenLight,
  onPrimaryContainer = Color.White,
  secondary = TerracottaTertiary,
  onSecondary = Color.White,
  secondaryContainer = GoldenHoneySecondary,
  onSecondaryContainer = Color.Black,
  tertiary = WarmGoldAccent,
  background = WarmParchmentBackground,
  onBackground = WarmTextPrimary,
  surface = CreamSurface,
  onSurface = WarmTextPrimary,
  surfaceVariant = SurfaceContainerHighLight,
  onSurfaceVariant = WarmTextSecondary
)

@Composable
fun RusticOrchardTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  RusticOrchardTheme(darkTheme = darkTheme, content = content)
}
