package com.igorapp.deckster.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun DeckBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val color = LocalBackgroundTheme.current.color
    val tonalElevation = LocalBackgroundTheme.current.tonalElevation

    Surface(
        color = if (color == Color.Unspecified) Color.Transparent else color,
        tonalElevation = if (tonalElevation == Dp.Unspecified) 0.dp else tonalElevation,
        modifier = modifier.fillMaxSize(),
    ) {
        CompositionLocalProvider(LocalAbsoluteTonalElevation provides 0.dp) {
            content()
        }
    }
}

@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    topColor: Color = topGradientColor,
    bottomColor: Color = bottomGradientColor,
    content: @Composable () -> Unit,
) {
    val currentTopColor by rememberUpdatedState(topColor)
    val currentBottomColor by rememberUpdatedState(bottomColor)

    DeckBackground(modifier = modifier) {
        Box(
            Modifier
                .fillMaxSize()
                .drawWithCache {
                    val gradientBackground = Brush.linearGradient(
                        colors = listOf(currentTopColor, currentBottomColor),
                    )
                    onDrawBehind {
                        drawRect(gradientBackground)
                    }
                }
        ) {
            content()
        }
    }
}


@Immutable
data class BackgroundTheme(
    val color: Color = Color.Unspecified,
    val tonalElevation: Dp = Dp.Unspecified,
)

/**
 * A composition local for [BackgroundTheme].
 */
val LocalBackgroundTheme = staticCompositionLocalOf { BackgroundTheme() }
