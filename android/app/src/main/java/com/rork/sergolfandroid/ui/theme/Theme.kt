package com.rork.sergolfandroid.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SerColorScheme = darkColorScheme(
    primary = AppColors.Gold,
    onPrimary = AppColors.BackgroundDark,
    secondary = AppColors.GoldLight,
    onSecondary = AppColors.BackgroundDark,
    background = AppColors.BackgroundDark,
    onBackground = AppColors.TextPrimary,
    surface = AppColors.CardGreen,
    onSurface = AppColors.TextPrimary,
    surfaceVariant = AppColors.CardGreen,
    onSurfaceVariant = AppColors.TextSecondary,
    error = AppColors.Error,
    onError = AppColors.White,
    outline = AppColors.CardBorder,
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SerColorScheme,
        content = content
    )
}
