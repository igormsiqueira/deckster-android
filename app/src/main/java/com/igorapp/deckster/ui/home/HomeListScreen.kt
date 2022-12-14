package com.igorapp.deckster.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.igorapp.deckster.feature.home.DecksterUiEvent
import com.igorapp.deckster.feature.home.DecksterUiState
import com.igorapp.deckster.model.Game
import com.igorapp.deckster.ui.*
import com.igorapp.deckster.ui.theme.DecksterTheme
import com.igorapp.deckster.ui.theme.bottomGradientColor
import com.igorapp.deckster.ui.theme.steamTypographyBold
import com.igorapp.deckster.ui.theme.topGradientColor

@Preview
@Composable
fun HomeListScreenPreviewLight(@PreviewParameter(HomeListScreenPreviewProvider::class) uiState: DecksterUiState) {
    DecksterTheme(darkTheme = false) {
        HomeListScreen(state = uiState) {}
    }
}

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
internal fun HomeListScreen(
    state: DecksterUiState,
    onEvent: (onEvent: DecksterUiEvent) -> Unit,
) {
    var size by remember { mutableStateOf(Size.Zero) }
//    val state  =  remember { decksterUiState }
    val listState = rememberLazyListState()
    val gridListState = rememberLazyListState()
    val filterListState = rememberLazyListState()

    DecksterTheme {
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
            Toolbar(onEvent, state)
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                when (state) {
                    is DecksterUiState.Error -> TODO()
                    is DecksterUiState.Loading -> item { DeckGameListLoadingIndicator() }
                    is DecksterUiState.Success -> {
                        deckGameListHeaderScreen(
                            gridListState,
                            state.choiceGames.reversed()
                        )
                        deckGameFilter(filterListState, state.filter) {
                            onEvent(DecksterUiEvent.OnFilterChange(it))
                        }
                        deckGameListScreen(state.games)
                    }

                    is DecksterUiState.Searching -> {
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
                }
            }
        }
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

//        Scaffold(
//            Modifier.background(topGradientColor),
//            topBar = { Toolbar(onEvent) }, content = {
//                Box(
//                    modifier =
//                    Modifier
//                        .fillMaxSize()
//                        .background(
//                            brush = Brush.linearGradient(
//                                colors = listOf(topGradientColor, bottomGradientColor),
//                            )
//                        )
//                        .onGloballyPositioned { coord ->
//                            size = coord.size.toSize()
//                        }
//                ) {
//                    if (decksterUiState is DecksterUiState.Searching) {
//                        Text(text = "Search view")
//                    } else if (decksterUiState is DecksterUiState.Success) {
//                        LazyColumn(
//                            state = listState,
//                            modifier = Modifier.fillMaxSize()
//                        ) {
//                            deckGameListHeaderScreen(
//                                gridListState,
//                                decksterUiState.choiceGames.reversed()
//                            )
//                            deckGameFilter(filterListState, decksterUiState.filter) {
//                                onEvent(DecksterUiEvent.OnFilterChange(it))
//                            }
//                            deckGameListScreen(decksterUiState.games)
//                        }
//                    } else {
//
//                    }
//                }
//            })
//            LazyColumn(
//                state = listState,
//                modifier = Modifier.fillMaxSize()
//            ) {
//                item {
//                    Toolbar(onEvent)
//                }
//                when (decksterUiState) {
//                    is DecksterUiState.Error -> TODO()
//                    is DecksterUiState.Success -> {
//                        deckGameListHeaderScreen(
//                            gridListState,
//                            decksterUiState.choiceGames.reversed()
//                        )
//                        deckGameFilter(filterListState, decksterUiState.filter) {
//                            onEvent(DecksterUiEvent.OnFilterChange(it))
//                        }
//                        deckGameListScreen(decksterUiState.games)
//
//                    }
//
//                    DecksterUiState.Loading -> item { Text(text = "Loading") }
//                    DecksterUiState.Searching -> item { Text(text = "Searching") }
//                }
//                item {
//                    listState.onBottomReached {
//                        onEvent(DecksterUiEvent.OnLoadMore)
//                    }
//                }
//            }

//        }


class HomeListScreenPreviewProvider : PreviewParameterProvider<DecksterUiState> {
    override val values: Sequence<DecksterUiState>
        get() = sequenceOf(
            DecksterUiState.Loading,
            DecksterUiState.Error(Exception()),
            DecksterUiState.Success(
                PreviewFactory.games,
                PreviewFactory.games,
                GameStatus.Verified
            ),
        )
}

object PreviewFactory {
    val games: List<Game> = mutableListOf(
        Game("123", "God of War", "3", "keyboard", "proton7-3-2")
    )
}