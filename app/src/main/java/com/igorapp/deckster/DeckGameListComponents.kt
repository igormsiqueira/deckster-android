package com.igorapp.deckster


import android.graphics.fonts.FontStyle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.igorapp.deckster.model.Game

@Composable
fun DeckGameListLoadingIndicator() {
    Text(text = "Loading")
}


fun LazyListScope.deckGameListHeaderScreen(games: List<Game>) {
    item {
        LazyRow {
            items(
                count = games.size,
                key = { games[it].id }
            ) { idx ->
                GameGridItem(games[idx])
            }
        }
    }
}

fun LazyListScope.deckGameListScreen(games: List<Game>) {
    items(
        count = games.size,
        key = { games[it].id }
    ) { idx ->
        GameListItem(games[idx])
    }
}


@Composable
fun GameGridItem(game: Game) {
    Box(
        modifier = Modifier.padding(8.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(ImageUrlBuilder.getHeaderUrl(game.id))
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(300.dp, 180.dp)
        )
        Row(Modifier.padding(8.dp)) {
            Text(
                color = Color.White,
                text = game.name,
                fontWeight = FontWeight.Bold,
            )
            /*  Text(
                  color = Color.White,
                  text = game.id
              )*/
        }
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
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(100.dp, 60.dp)
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


@Composable
fun Toolbar() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 20.dp, top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 30.sp,
            text = "Deckster App"
        )
        Icon(
            imageVector = Icons.Default.Search,
            tint = Color.White,
            contentDescription = stringResource(id = R.string.app_name)
        )

    }
}