package com.igorapp.deckster.ui.home

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.igorapp.deckster.feature.home.DecksterSearchUiState
import com.igorapp.deckster.feature.home.DecksterUiEvent
import com.igorapp.deckster.ui.searchDeckGameListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchListScreen(
    state: DecksterSearchUiState,
    navController: NavController = rememberNavController(),
    onEvent: (onEvent: DecksterUiEvent) -> Unit,
) {
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = { SearchToolbar(onEvent, state, navController) },
        content = {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (state) {
                        is DecksterSearchUiState.Content -> deckGameSearchScreen(state)
                        is DecksterSearchUiState.Error -> TODO()
                        DecksterSearchUiState.Loading -> {
                            item {
                                Text(text = "Loading")
                            }
                        }
                        is DecksterSearchUiState.Empty -> {
                            searchEmptyState(if (state.term.isNullOrBlank()) {
                                "Start a search by typing the name of a game.\n\nUnplayable games still show in the search and can be added to you backlog."
                            } else {
                                "No results for ${state.term}"
                            })
                        }
                    }
                }
            }
        }
    )
}


private fun LazyListScope.deckGameSearchScreen(state: DecksterSearchUiState.Content) {
    var text = ""

    if (state.term.isNullOrBlank()) {
        text =
            "Start a search by typing the name of a game.\n\nUnplayable games still show in the search and can be added to you backlog."
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
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.padding(top = 20.dp))
    }
}
