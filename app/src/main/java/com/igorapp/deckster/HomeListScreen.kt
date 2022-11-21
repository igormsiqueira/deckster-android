package com.igorapp.deckster

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.igorapp.deckster.model.Game
import com.igorapp.deckster.ui.theme.DecksterTheme

@Preview
@Composable
fun HomeListScreenPreviewLight(@PreviewParameter(HomeListScreenPreviewProvider::class) uiState: DecksterUiState) {
    DecksterTheme(darkTheme = false) {
        HomeListScreen(decksterUiState = uiState)
    }
}

@Composable
internal fun HomeListScreen(decksterUiState: DecksterUiState) {
    DecksterTheme {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (decksterUiState) {
                is DecksterUiState.Success ->
                    deckGameListScreen(this, decksterUiState.games)

                is DecksterUiState.Loading -> item {
                    DeckGameListLoadingIndicator()
                }

                is DecksterUiState.Error -> item {
                    DeckGameListErrorScreen()
                }

            }
        }
    }
}


class HomeListScreenPreviewProvider : PreviewParameterProvider<DecksterUiState> {
    override val values: Sequence<DecksterUiState>
        get() = sequenceOf(
            DecksterUiState.Loading,
            DecksterUiState.Error,
            DecksterUiState.Success(PreviewFactory.games),
        )
}

object PreviewFactory {
    val games: List<Game> = mutableListOf(
        Game("123", "God of War", "3")
    )
}