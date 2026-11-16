package com.gracekim.saju.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.gracekim.saju.core.Element

private val Purple = Color(0xFF5B2A86)
private val Gold = Color(0xFFE8B04B)

private val LightColors = lightColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    secondary = Gold,
    background = Color(0xFFFBF8F3),
    surface = Color(0xFFFBF8F3),
    surfaceVariant = Color(0xFFF1EBE2),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFCDB4F0),
    onPrimary = Color(0xFF2E1146),
    secondary = Gold,
    background = Color(0xFF16131A),
    surface = Color(0xFF16131A),
    surfaceVariant = Color(0xFF2A2530),
)

@Composable
fun SajuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}

/** 오행별 전통 색상 */
fun Element.color(): Color = when (this) {
    Element.WOOD -> Color(0xFF3E9B4F)
    Element.FIRE -> Color(0xFFD9433B)
    Element.EARTH -> Color(0xFFC59A2B)
    Element.METAL -> Color(0xFF8A8F98)
    Element.WATER -> Color(0xFF2B4C8C)
}
