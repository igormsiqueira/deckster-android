package com.igorapp.deckster.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.igorapp.deckster.feature.home.DecksterUiEvent
import com.igorapp.deckster.feature.home.DecksterUiState
import com.igorapp.deckster.model.Game
import com.igorapp.deckster.ui.*
import com.igorapp.deckster.ui.theme.DecksterTheme
import com.igorapp.deckster.ui.theme.bottomGradientColor
import com.igorapp.deckster.ui.theme.steamTypographyBold
import com.igorapp.deckster.ui.theme.topGradientColor
import com.igorapp.deckster.ui.utils.onBottomReached

@Preview
@Composable
fun HomeListScreenPreviewLight(@PreviewParameter(HomeListScreenPreviewProvider::class) uiState: DecksterUiState) {
    DecksterTheme(darkTheme = false) {
        HomeListScreen(state = uiState) {}
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
internal fun HomeListScreen(
    state: DecksterUiState,
    onEvent: (onEvent: DecksterUiEvent) -> Unit,
) {
    var size by remember { mutableStateOf(Size.Zero) }
    val listState = rememberLazyListState()
    val pagerState = rememberPagerState()
    val filterListState = rememberLazyListState()

    DecksterTheme {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            topBar = { Toolbar(onEvent, state) },
            content = {
                Column(
                    modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(topGradientColor, bottomGradientColor),
                            )
                        )
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when (state) {
                            is DecksterUiState.Error -> {}
                            is DecksterUiState.Loading -> item { DeckGameListLoadingIndicator() }
                            is DecksterUiState.Content -> {
                                deckGameListHeaderScreen(
                                    pagerState,
                                    state.choiceGames.reversed()
                                )
                                deckGameFilter(filterListState, state.filter) {
                                    onEvent(DecksterUiEvent.OnFilterChange(it))
                                }
                                deckGameListScreen(state.games, onEvent)
                            }

                            is DecksterUiState.Searching -> {
                                deckGameSearchScreen(state)
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
        )

    }
}


private fun LazyListScope.deckGameSearchScreen(state: DecksterUiState.Searching) {
    var text = ""

    if (state.term.isNullOrBlank()) {
        text = "Start a search by typing the name of a game."
    } else if (state.games.isEmpty()) {
        text = "No results for ${state.term}"
    }

    if (text.isEmpty()) {
        searchDeckGameListScreen(state.games)
    } else {
        searchEmptyState(text)
    }
}

private fun LazyListScope.searchEmptyState(text: String) {
    item {
        Spacer(modifier = Modifier.padding(bottom = 20.dp))
        Text(
            color = Color.White,
            fontSize = 14.sp,
            text = text,
            style = steamTypographyBold.labelSmall,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.padding(top = 20.dp))
    }
}

class HomeListScreenPreviewProvider : PreviewParameterProvider<DecksterUiState> {
    override val values: Sequence<DecksterUiState>
        get() = sequenceOf(
            DecksterUiState.Loading,
            DecksterUiState.Error(Exception()),
            DecksterUiState.Content(
                PreviewFactory.games,
                PreviewFactory.games,
                GameStatus.Verified
            ),
        )
}

object PreviewFactory {
    val games: List<Game> = mutableListOf(
        Game("123", "God of War", "3", "keyboard", "proton7-3-2", false)
    )
}