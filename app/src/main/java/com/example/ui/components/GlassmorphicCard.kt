package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modern Apple / Samsung style Frosted Glass Card with translucent specular gradient border.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shapeRadius: Dp = 22.dp,
    tintColor: Color = Color(0xFF14241E),
    strokeAlpha: Float = 0.25f,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(shapeRadius)

    val glassBorderBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = strokeAlpha),
            Color.White.copy(alpha = strokeAlpha * 0.25f),
            Color(0xFF48D5A3).copy(alpha = strokeAlpha * 0.4f),
            Color.White.copy(alpha = strokeAlpha * 0.1f)
        )
    )

    val glassBackground = Brush.verticalGradient(
        colors = listOf(
            tintColor.copy(alpha = 0.85f),
            tintColor.copy(alpha = 0.65f),
            Color(0xFF0C1713).copy(alpha = 0.88f)
        )
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(glassBackground)
            .border(BorderStroke(1.dp, glassBorderBrush), shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        // Specular highlight subtle line at the top (Apple liquid glass reflection)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = strokeAlpha * 1.5f),
                            Color(0xFFD4AF37).copy(alpha = strokeAlpha),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            content = content
        )
    }
}

/**
 * Compact Frosted Glass Pill / Badge.
 */
@Composable
fun GlassPill(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0x331E332B),
    borderColor: Color = Color(0x40FFFFFF),
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(BorderStroke(0.8.dp, borderColor), shape)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        content = content
    )
}
