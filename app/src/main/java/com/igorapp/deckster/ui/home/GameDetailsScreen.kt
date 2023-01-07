package com.igorapp.deckster.ui.home

import android.widget.TextView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.BackdropValue
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
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
import com.igorapp.deckster.model.Game
import com.igorapp.deckster.ui.theme.GradientBackground
import com.igorapp.deckster.ui.theme.steamTypographyBold
import com.igorapp.deckster.ui.utils.headerUpright
import extensions.urlsOnly
import kotlinx.coroutines.launch
import model.GameInfoResult

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GameDetailsScreen(
    state: DecksterDetailUiState,
    navController: NavController,
    onEvent: (DecksterDetailUiEvent) -> Unit,
) {
    val backdropState = remember {
        BackdropScaffoldState(initialValue = BackdropValue.Revealed)
    }

    var previousProgress = 0f
    var previousScrollPosition = 0f

    val scale by animateFloatAsState(
        targetValue = if (backdropState.currentValue == BackdropValue.Revealed) 1f else 0f,
    )

    GradientBackground {
        val scroll = rememberScrollState(0)
        BackdropScaffold(
            scaffoldState = backdropState,
            stickyFrontLayer = true,
            persistentAppBar = false,
            frontLayerScrimColor = Color.Transparent,
            backLayerContentColor = MaterialTheme.colorScheme.onBackground,
            backLayerBackgroundColor = Color.Unspecified,
            peekHeight = 0.dp,
            headerHeight = 390.dp,
            frontLayerContent = {
                if (state is DecksterDetailUiState.Content) {
                    val (game, gameInfo) = state.gameData
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .size(60.dp, 4.dp)
                                .size(scale.dp),
//                                .scale(scale),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                            onClick = { /*TODO*/ }) {
                        }
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            text = gameInfo?.data?.name.toString(),
                            style = steamTypographyBold.titleLarge
                        )
                        DeckStatusChip(game, onEvent)
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scroll)
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            text = gameInfo?.data?.aboutTheGame.removeAllHtml(),
                            style = steamTypographyBold.titleMedium
                        )
                    }
                }

            },
            appBar = { /*TODO*/ }, backLayerContent = {
                when (state) {
                    is DecksterDetailUiState.Content -> GamePhotosScreen(state.gameData)
                    is DecksterDetailUiState.Error -> Text(text = "Error ${state.throwable}")
                    is DecksterDetailUiState.Loading -> Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        )


    }
}

@Composable
fun DeckStatusChip(
    game: Game, onEvent: (onEvent: DecksterDetailUiEvent) -> Unit
) {
    val isBookmarked = remember { mutableStateOf(game.isBookmarked) }
    val scope = rememberCoroutineScope()

    Row(
        horizontalArrangement = Arrangement.Center, modifier = Modifier
            .padding(8.dp)
            .background(
                Color.Black,
                RoundedCornerShape(16.dp)
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_deckicon_white),
            contentDescription = stringResource(id = R.string.app_name),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(2.dp)
                .size(28.dp)
        )
        Image(
            painter = painterResource(id = getStatusIcon(game.status)),
            contentDescription = stringResource(id = R.string.app_name),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(2.dp)
                .size(28.dp)
        )
        IconButton(
            modifier = Modifier
                .padding(2.dp)
                .size(28.dp),
            onClick = {
                scope.launch {
                    isBookmarked.value = !isBookmarked.value
                    onEvent(DecksterDetailUiEvent.OnDetailsBookmarkToggle(game))
                }
            }) {
            Icon(
                tint = Color.White,
                imageVector = if (isBookmarked.value) {
                    Icons.Rounded.Favorite
                } else {
                    Icons.Rounded.FavoriteBorder
                },
                contentDescription = ""
            )
        }
    }
}

fun getStatusIcon(status: String): Int {
    val gameStatus = GameStatus.values().find {
        it.code.toString() == status
    }?: GameStatus.Unknown

    return when (gameStatus) {
        GameStatus.Verified -> R.drawable.badge_verified
        GameStatus.Playable -> R.drawable.badge_playable
        else -> R.drawable.badge_unknown
    }
}

private fun String?.removeAllHtml(): String {
    return this.toString()
        .replace("""</strong><br><br>""", "\n")
        .replace("""<br><br><strong>""", "\n")
        .replace("""</h2><strong>""", "\n")
        .replace("<h2 class=\"bb_tag\">", "\n\n")
        .replace(regex = Regex("\\<.*?>"), replacement = "")
}

@Composable
fun Html(text: String) {
    AndroidView(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxHeight(),
        factory = { context ->
            TextView(context).apply {
                setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
            }
        })
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

        HorizontalPager(
            count = urls.size,
            itemSpacing = 10.dp,
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            if (page == 0) {
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
                .wrapContentWidth()
                .padding(top = 16.dp),
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
//            .size(396.dp, 342.dp)//maintains proportions!
            .size(196.dp, 342.dp)//maintains proportions!
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

