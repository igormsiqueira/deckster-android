package com.igorapp.deckster


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.igorapp.deckster.model.Game

@Composable
fun DeckGameListLoadingIndicator() {
    Text(text = "Loading")
}


fun deckGameListScreen(listScope:LazyListScope, games: List<Game>) {
    listScope.items(
        count = games.size,
        key = { games[it].id }
    ) { idx ->
        GameListItem(games[idx])
    }
}

@Composable
fun GameListItem(game: Game) {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
             modifier = Modifier.padding(16.dp),
            text = game.name
        )
    }
}

@Composable
fun DeckGameListErrorScreen() {
    Text(text = "Error")
}