package com.example.sporex_app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = SporexGreen,
    onPrimary = SporexWhite,

    background = SporexWhite,
    onBackground = SporexBlack,

    surface = SporexGrey,
    onSurface = SporexBlack,

    secondary = SporexGreenSoft
)

private val DarkColors = darkColorScheme(
    primary = SporexGreenDark,
    onPrimary = SporexBlack,

    background = SporexBlack,
    onBackground = SporexTextPrimary,

    surface = SporexSurface,
    onSurface = SporexTextPrimary,

    secondary = SporexGreenSoft
)

@Composable
fun SPOREX_AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}