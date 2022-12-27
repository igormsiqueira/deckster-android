package com.igorapp.deckster.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.igorapp.deckster.feature.home.DecksterUiEvent
import com.igorapp.deckster.feature.home.DecksterUiState
import com.igorapp.deckster.model.Game
import com.igorapp.deckster.platform.Destinations
import com.igorapp.deckster.ui.*
import com.igorapp.deckster.ui.theme.DecksterTheme
import com.igorapp.deckster.ui.utils.onBottomReached
import kotlinx.coroutines.launch

@Preview
@Composable
fun HomeListScreenPreviewLight(@PreviewParameter(HomeListScreenPreviewProvider::class) uiState: DecksterUiState) {
    DecksterTheme(darkTheme = false) {
        HomeListScreen(uiState) {}
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
internal fun HomeListScreen(
    state: DecksterUiState,
    navController: NavController = rememberNavController(),
    onEvent: (onEvent: DecksterUiEvent) -> Unit,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scope.launch {
                    navController.navigate(Destinations.Search.name)
                }
            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            }
        },
        topBar = { Toolbar(navController) },
        content = {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                when (state) {
                    is DecksterUiState.Error -> {}
                    is DecksterUiState.Loading -> loading()
                    is DecksterUiState.Content -> content(state, onEvent, navController)
                }
                item {
                    listState.onBottomReached {
                        onEvent(DecksterUiEvent.OnLoadMore)
                    }
                }
            }
        }
    )
}

private fun LazyListScope.loading() {
    item {
        DeckGameListLoadingIndicator()
    }
}

private fun LazyListScope.content(
    state: DecksterUiState.Content,
    onEvent: (onEvent: DecksterUiEvent) -> Unit,
    navController: NavController,
) {
    deckGameListHeaderScreen(state.choiceGames.reversed(), onEvent)

    deckGameFilter(state.filter) {
        onEvent(DecksterUiEvent.OnFilterChange(it))
    }

    if (state.filter == GameStatus.Backlog) {
        deckBacklogGameListScreen(navController, state.games, onEvent)
    } else {
        deckGameListScreen(navController, state.games, onEvent)
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