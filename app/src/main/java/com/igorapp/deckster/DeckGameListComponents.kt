package com.igorapp.deckster


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
    Row(Modifier.padding(8.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(ImageUrlBuilder.getHeaderUrl(game.id))
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier.clip(RoundedCornerShape(8.dp)).size(100.dp,60.dp)
        )
        Column(Modifier.padding(8.dp)) {
            Text(
                color = Color.White,
                text = game.name
            )
            Text(
                color = Color.White,
                text = game.id
            )
        }
    }
}

@Composable
fun DeckGameListErrorScreen() {
    Text(text = "Error")
}