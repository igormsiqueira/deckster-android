package com.igorapp.deckster

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.toSize
import com.igorapp.deckster.model.Game
import com.igorapp.deckster.ui.theme.DecksterTheme
import com.igorapp.deckster.ui.theme.topGradientColor
import com.igorapp.deckster.ui.theme.bottomGradientColor

@Preview
@Composable
fun HomeListScreenPreviewLight(@PreviewParameter(HomeListScreenPreviewProvider::class) uiState: DecksterUiState) {
    DecksterTheme(darkTheme = false) {
        HomeListScreen(decksterUiState = uiState)
    }
}

@Composable
internal fun HomeListScreen(decksterUiState: DecksterUiState) {
    var size by remember { mutableStateOf(Size.Zero) }

    DecksterTheme {
        Box(
            modifier = Modifier
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
            LazyColumn {
                item {
                    Toolbar()
                }
                when (decksterUiState) {
                    is DecksterUiState.Success -> {
                        deckGameListHeaderScreen(decksterUiState.choiceGames)
                        deckGameListScreen(decksterUiState.games)
                    }

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
}


class HomeListScreenPreviewProvider : PreviewParameterProvider<DecksterUiState> {
    override val values: Sequence<DecksterUiState>
        get() = sequenceOf(
            DecksterUiState.Loading,
            DecksterUiState.Error,
            DecksterUiState.Success(PreviewFactory.games, PreviewFactory.games),
        )
}

object PreviewFactory {
    val games: List<Game> = mutableListOf(
        Game("123", "God of War", "3")
    )
}