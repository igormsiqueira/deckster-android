package com.igorapp.deckster.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.igorapp.deckster.feature.home.DecksterUiEvent
import com.igorapp.deckster.feature.home.DecksterUiState
import com.igorapp.deckster.model.Game
import com.igorapp.deckster.ui.*
import com.igorapp.deckster.ui.theme.DecksterTheme
import com.igorapp.deckster.ui.theme.WhiteIcon
import com.igorapp.deckster.ui.theme.bottomGradientColor
import com.igorapp.deckster.ui.theme.topGradientColor

@Preview
@Composable
fun HomeListScreenPreviewLight(@PreviewParameter(HomeListScreenPreviewProvider::class) uiState: DecksterUiState) {
    DecksterTheme(darkTheme = false) {
        HomeListScreen(decksterUiState = uiState) {}
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun HomeListScreen(
    decksterUiState: DecksterUiState,
    onEvent: (onEvent: DecksterUiEvent) -> Unit,
) {
    var size by remember { mutableStateOf(Size.Zero) }
    val listState = rememberLazyListState()
    val gridListState = rememberLazyListState()
    val filterListState = rememberLazyListState()

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
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Toolbar(onEvent)
                }
                when (decksterUiState) {
                    is DecksterUiState.Error -> TODO()
                    is DecksterUiState.Success -> {
                        deckGameListHeaderScreen(
                            gridListState,
                            decksterUiState.choiceGames.reversed()
                        )
                        deckGameFilter(filterListState, decksterUiState.filter) {
                            onEvent(DecksterUiEvent.OnFilterChange(it))
                        }
                        deckGameListScreen(decksterUiState.games)

                    }
                    DecksterUiState.Loading -> item { Text(text = "Loading") }
                    DecksterUiState.Searching -> item { Text(text = "Searching") }
                }
            }


////                when (decksterUiState) {
//////                    is DecksterUiState.Success -> {
//////                        deckGameListHeaderScreen(
//////                            gridListState,
//////                            decksterUiState.choiceGames.reversed()
//////                        )
//////                        deckGameFilter(filterListState, decksterUiState.filter) {
//////                            onEvent(DecksterUiEvent.OnFilterChange(it))
//////                        }
//////
//////                        deckGameListScreen(decksterUiState.games)
//////                    }
//////
//////                    is DecksterUiState.Loading -> item {
//////                        DeckGameListLoadingIndicator()
//////                    }
//////
//////                    is DecksterUiState.Error -> item {
//////                        DeckGameListErrorScreen()
//////                    }
//////
//////                    DecksterUiState.Searching -> item {
////                        FullHeightBottomSheet(header = {
////                            Toolbar()
////                        }, body = {
////                        })
//            }
//                }
//                item {
//                    listState.onBottomReached {
//                        onEvent(DecksterUiEvent.OnLoadMore)
//                    }
//                }
        }

    }
}



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