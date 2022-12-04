package com.igorapp.deckster.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.toSize
import com.igorapp.deckster.feature.home.DecksterUiEvent
import com.igorapp.deckster.feature.home.DecksterUiState
import com.igorapp.deckster.model.Game
import com.igorapp.deckster.ui.*
import com.igorapp.deckster.ui.home.StatusOptions.Verified
import com.igorapp.deckster.ui.theme.DecksterTheme
import com.igorapp.deckster.ui.theme.bottomGradientColor
import com.igorapp.deckster.ui.theme.topGradientColor
import com.igorapp.deckster.ui.utils.onBottomReached

@Preview
@Composable
fun HomeListScreenPreviewLight(@PreviewParameter(HomeListScreenPreviewProvider::class) uiState: DecksterUiState) {
    DecksterTheme(darkTheme = false) {
        HomeListScreen(decksterUiState = uiState) {}
    }
}

@Composable
internal fun HomeListScreen(
    decksterUiState: DecksterUiState,
    onEvent: (onEvent: DecksterUiEvent) -> Unit,
) {
    var size by remember { mutableStateOf(Size.Zero) }
    val listState = rememberLazyListState()
    val gridListState = rememberLazyListState()
    DecksterTheme {
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(topGradientColor, bottomGradientColor),
                    )
                )
                .onGloballyPositioned { coord ->
                    size = coord.size.toSize()
                }
        ) {
            LazyColumn(
                state = listState,
            ) {
                item {
                    Toolbar()
                }
                when (decksterUiState) {
                    is DecksterUiState.Success -> {
                        deckGameListHeaderScreen(gridListState,decksterUiState.choiceGames)
                        deckGameFilter {
                            onEvent(DecksterUiEvent.OnFilterChange(it))
                        }
                        deckGameListScreen(decksterUiState.games)
                    }

                    is DecksterUiState.Loading -> item {
                        DeckGameListLoadingIndicator()
                    }

                    is DecksterUiState.Error -> item {
                        DeckGameListErrorScreen()
                    }
                }
                item {
                    listState.onBottomReached {
                        onEvent(DecksterUiEvent.OnLoadMore)
                    }
                }
            }
        }

    }
}


class HomeListScreenPreviewProvider : PreviewParameterProvider<DecksterUiState> {
    override val values: Sequence<DecksterUiState>
        get() = sequenceOf(
            DecksterUiState.Loading,
            DecksterUiState.Error(Exception()),
            DecksterUiState.Success(PreviewFactory.games, PreviewFactory.games, Verified),
        )
}

object PreviewFactory {
    val games: List<Game> = mutableListOf(
        Game("123", "God of War", "3", "keyboard", "proton7-3-2")
    )
}