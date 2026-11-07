package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Modern Minimal Dark Theme
    themeColor: AppThemeColor = AppThemeColor.EMERALD,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = themeColor.accentColor,
            onPrimary = Color.Black,
            primaryContainer = themeColor.primaryColor,
            onPrimaryContainer = themeColor.accentColor,
            secondary = themeColor.secondaryColor,
            onSecondary = Color.Black,
            secondaryContainer = themeColor.primaryColor.copy(alpha = 0.5f),
            onSecondaryContainer = themeColor.secondaryColor,
            tertiary = themeColor.secondaryColor,
            background = themeColor.darkBackground,
            onBackground = Color(0xFFF1F5F9),
            surface = themeColor.darkSurface,
            onSurface = Color(0xFFF1F5F9),
            surfaceVariant = themeColor.darkSurfaceVariant,
            onSurfaceVariant = Color(0xFFCBD5E1)
        )
    } else {
        lightColorScheme(
            primary = themeColor.primaryColor,
            onPrimary = Color.White,
            primaryContainer = themeColor.accentColor.copy(alpha = 0.2f),
            onPrimaryContainer = themeColor.primaryColor,
            secondary = themeColor.secondaryColor,
            onSecondary = Color.Black,
            background = Color(0xFFF8FAFC),
            onBackground = Color(0xFF0F172A),
            surface = Color.White,
            onSurface = Color(0xFF0F172A),
            surfaceVariant = Color(0xFFF1F5F9),
            onSurfaceVariant = Color(0xFF475569)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
