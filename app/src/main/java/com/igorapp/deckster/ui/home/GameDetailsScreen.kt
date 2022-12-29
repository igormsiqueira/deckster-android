package com.igorapp.deckster.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.igorapp.deckster.R
import com.igorapp.deckster.feature.home.DecksterDetailUiEvent
import com.igorapp.deckster.feature.home.DecksterDetailUiState
import com.igorapp.deckster.feature.home.DecksterDetailUiState.Content
import com.igorapp.deckster.feature.home.DecksterDetailUiState.Error
import com.igorapp.deckster.feature.home.DecksterDetailUiState.Loading
import com.igorapp.deckster.model.Game
import com.igorapp.deckster.ui.theme.steamTypographyBold
import com.igorapp.deckster.ui.utils.headerUpright
import model.GameInfoResult
import urlsOnly

@Composable
fun GameDetailsScreen(
    state: DecksterDetailUiState,
    navController: NavController,
    onEvent: (DecksterDetailUiEvent) -> Unit,
) {

    when (state) {
        is Content -> GamePhotosScreen(state.gameData)
        is Error -> Text(text = "Error ${state.throwable}")
        is Loading -> CircularProgressIndicator()
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GamePhotosScreen(data: Pair<Game, GameInfoResult?>) {
    val (game, gameInfo) = data
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    if (gameInfo == null) {
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val urls = gameInfo.data.screenshots.urlsOnly
        val halfPoint = urls.size / 2

        LaunchedEffect(key1 = halfPoint, block = {
            pagerState.scrollToPage(halfPoint)
        })

        HorizontalPager(
            count = urls.size,
            itemSpacing = 1.dp,
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            if (page == halfPoint) {
                GameDetailsScreen(game)
            } else {
                GameScreenShotView(urls[page])
            }
        }

        HorizontalPagerIndicator(
            activeColor = Color.White,
            indicatorWidth = 12.dp,
            indicatorHeight = 2.dp,
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
        )
        Text(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            textAlign = TextAlign.Center,
            color = Color.White,
            text = game.game,
            style = steamTypographyBold.headlineSmall
        )
    }
}

@Composable
fun GameDetailsScreen(game: Game) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(game.headerUpright)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = stringResource(R.string.app_name),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(396.dp, 342.dp)//maintains proportions!
//            .size(196.dp, 342.dp)//maintains proportions!
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun GameScreenShotView(info: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(info)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = stringResource(R.string.app_name),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(396.dp, 342.dp)//maintains proportions!
//            .size(196.dp, 342.dp)//maintains proportions!
            .clip(RoundedCornerShape(8.dp))
    )
}

