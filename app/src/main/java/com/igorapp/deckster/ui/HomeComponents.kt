package com.igorapp.deckster.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.igorapp.deckster.feature.home.DecksterUiEvent
import com.igorapp.deckster.feature.home.DecksterUiState
import com.igorapp.deckster.ui.home.GameStatus
import com.igorapp.deckster.ui.theme.steamTypographyBold

fun LazyListScope.deckGameList(
    state: DecksterUiState.Content,
    navController: NavController,
    onEvent: (onEvent: DecksterUiEvent) -> Unit
) {
    if (state.games.isEmpty()) {
        item {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .alpha(0.7f),
                textAlign = TextAlign.Center,
                text = "No Games under this filter",
                color = Color.White,
                style = steamTypographyBold.titleMedium,
            )
        }
    } else {
        when (state.filter) {
            GameStatus.Backlog -> deckBacklogGameListScreen(navController, state.games, onEvent)
            GameStatus.Playable, GameStatus.Verified, GameStatus.Unknown -> deckGameListScreen(
                navController,
                state.games,
                onEvent
            )
        }
    }
}