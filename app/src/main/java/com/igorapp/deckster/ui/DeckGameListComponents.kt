package com.igorapp.deckster.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.ImageDecoderDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.igorapp.deckster.R
import com.igorapp.deckster.feature.home.DecksterUiEvent
import com.igorapp.deckster.feature.home.DecksterUiState
import com.igorapp.deckster.model.Game
import com.igorapp.deckster.ui.home.GameStatus
import com.igorapp.deckster.ui.theme.*
import com.igorapp.deckster.ui.utils.dipToPx
import com.igorapp.deckster.ui.utils.getCapsuleUrl231
import com.igorapp.deckster.ui.utils.headerCapsule6x3ImageUrl
import com.igorapp.deckster.ui.utils.navigateToGameDetails
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt


@Composable
fun DeckGameListLoadingIndicator() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GifImage()
    }
}

@Composable
fun GifImage(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context).components {
        add(ImageDecoderDecoder.Factory())
    }.build()
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context).data(data = R.drawable.pal_splash_tiny_big)
                .apply(block = {
                    size(Size.ORIGINAL)
                }).build(), imageLoader = imageLoader
        ),
        contentDescription = null,
        modifier = modifier.wrapContentSize(),
    )
}

fun LazyListScope.deckGameListHeaderScreen(
    state: DecksterUiState.Content,
    navController: NavController,
    onEvent: (onEvent: DecksterUiEvent) -> Unit,
) {
    item { SpotlightGames(state.choiceGames, navController, onEvent) }
}

@Composable
@OptIn(ExperimentalPagerApi::class)
fun SpotlightGames(
    games: List<Game>,
    navController: NavController = rememberNavController(),
    onEvent: (onEvent: DecksterUiEvent) -> Unit,
) {
    val pagerState = rememberPagerState()

    Column(Modifier.fillMaxSize()) {
        HorizontalPager(count = games.size, state = pagerState) { page ->
            GameGridItem(games[page], page, navController, onEvent)
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
    }
}


@OptIn(ExperimentalSnapperApi::class)
fun LazyListScope.deckGameFilter(
    state: DecksterUiState.Content,
    filterChanged: (String) -> Unit,
) {
    item {
        Spacer(modifier = Modifier.padding(4.dp))
    }
    val options = listOf(GameStatus.AllGames, GameStatus.Backlog).map(GameStatus::name)

    item {
        val filterListState = rememberLazyListState()
        var filter by remember { mutableStateOf(state.filter.name) }
        val onSelectionChange = { text: String ->
            filter = text
            filterChanged(filter)
        }
        LazyRow(
            state = filterListState,
            flingBehavior = rememberSnapperFlingBehavior(filterListState),
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp, end = 16.dp)
                .wrapContentSize()
        ) {
            items(
                count = options.size,
                key = { options[it] }
            ) { idx ->
                FilterButton(options[idx], filter, onSelectionChange, idx)
            }
        }
    }
}

@Composable
private fun FilterButton(
    text: String,
    selectedOption: String,
    onSelectionChange: (String) -> Unit,
    idx: Int,
) {
    Button(
        shape = RoundedCornerShape(size = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .alpha(
                if (text == selectedOption) {
                    1.0f
                } else {
                    0.5f
                }
            )
            .padding(
                start = if (idx == 0) {
                    20.dp
                } else {
                    8.dp
                }, end = 8.dp
            ),
        onClick = { onSelectionChange(text) }) {
        Text(text = text)
    }
}

fun LazyListScope.deckGameListScreen(
    navController: NavController, games: List<Game>, onEvent: (onEvent: DecksterUiEvent) -> Unit,
) {
    items(
        count = games.size,
        key = { games[it].id }
    ) { idx ->
        GameListItem(navController, games[idx], onEvent)
    }
}

fun LazyListScope.deckBacklogGameListScreen(
    navController: NavController,
    games: List<Game>,
    onEvent: (onEvent: DecksterUiEvent) -> Unit,
) {
    items(
        count = games.size,
        key = { games[it].id }
    ) { idx ->
        GameBookmarkListItem(navController, games[idx], idx, onEvent)
    }
}

fun LazyListScope.searchDeckGameListScreen(games: List<Game>, navController: NavController) {
    items(
        count = games.size,
        key = { games[it].id }
    ) { idx ->
        SearchGameListItem(games[idx], navController)
    }
}


@Composable
fun GameGridItem(
    item: Game,
    idx: Int,
    navController: NavController,
    onEvent: (onEvent: DecksterUiEvent) -> Unit
) {
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .padding(
                top = 16.dp,
                start = if (idx == 0) {
                    1.dp
                } else {
                    8.dp
                }, end = 8.dp
            )
            .clickable {
                navController.navigateToGameDetails(item.id)
            },
        contentAlignment = Alignment.BottomStart
    ) {
        val isBookmarked = remember { mutableStateOf(item.isBookmarked) }
        HeaderImage(item)
        IconButton(
            onClick = {
                scope.launch {
                    isBookmarked.value = !isBookmarked.value
                    onEvent(DecksterUiEvent.OnBookmarkToggle(item))
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

@Composable
fun HeaderImage(item: Game) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(item.headerCapsule6x3ImageUrl)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = stringResource(R.string.app_name),
        contentScale = ContentScale.FillBounds,
//            modifier = Modifier
//                .clip(RoundedCornerShape(0, 0, 10, 10))
//                .size(396.dp, 342.dp)//maintains proportions!
////                .size(396.dp, 342.dp)//maintains proportions!

        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
//                .size(196.dp, 342.dp)
            .size(342.dp, 196.dp)
    )
}

@Composable
fun SearchGameListItem(item: Game, navController: NavController) {
    Row(
        modifier = Modifier
            .padding(start = 16.dp)
            .clickable {
                navController.navigateToGameDetails(item.id)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.getCapsuleUrl231)
                .crossfade(false)
                .build(),
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .size(100.dp, 65.dp)
                .padding(top = 8.dp)
        )
        Column(Modifier.padding(8.dp)) {
            Text(
                maxLines = 1,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                text = item.game
            )
            Text(
                color = secondaryText,
                fontSize = 12.sp,
                text = "${getInputText(item.input)} ${item.runtime.capitalize(Locale.getDefault())}"
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GameBookmarkListItem(
    navController: NavController, item: Game, idx: Int, onEvent: (onEvent: DecksterUiEvent) -> Unit,
) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigateToGameDetails(item.id)
            }
            .swipeable(
                state = swipeableState,
                anchors = mapOf(
                    0f to 0,
                    context.dipToPx(70f) to 1,
                    context.dipToPx(70f) to 2,
                ),
                thresholds = { _, _ ->
                    FractionalThreshold(0.1f)
                },
                orientation = Orientation.Horizontal
            )
    ) {
        BookMarkIcon(scope, swipeableState, onEvent, item)
        SwipeableBookmarkGameItem(swipeableState, item, idx)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GameListItem(
    navController: NavController, item: Game, onEvent: (onEvent: DecksterUiEvent) -> Unit,
) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigateToGameDetails(item.id)
            }
            .swipeable(
                state = swipeableState,
                anchors = mapOf(
                    0f to 0,
                    context.dipToPx(70f) to 1,
                    context.dipToPx(70f) to 2,
                ),
                thresholds = { _, _ ->
                    FractionalThreshold(0.1f)
                },
                orientation = Orientation.Horizontal
            )
    ) {
        BookMarkIcon(scope, swipeableState, onEvent, item)
        SwipeableGameItem(swipeableState, item)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeableBookmarkGameItem(
    swipeableState: SwipeableState<Int>,
    item: Game,
    idx: Int,
) {
    Row(
        modifier = Modifier
            .padding(start = 20.dp)
            .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            maxLines = 1,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            text = "${idx}.", modifier = Modifier.padding(end = 6.dp)
        )
        GameCover(item)
        Column(
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                maxLines = 1,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                text = item.game
            )
            Text(
                color = secondaryText,
                fontSize = 12.sp,
                text = "${getInputText(item.input)} ${item.runtime.capitalize(Locale.getDefault())}"
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeableGameItem(
    swipeableState: SwipeableState<Int>,
    item: Game,
) {
    Row(
        modifier = Modifier
            .padding(start = 20.dp)
            .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        GameCover(item)
        Column(
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                maxLines = 1,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                text = item.game
            )
            Text(
                color = secondaryText,
                fontSize = 12.sp,
                text = "${getInputText(item.input)} ${item.runtime.capitalize(Locale.getDefault())}"
            )
        }
    }
}

@Composable
private fun GameCover(item: Game) {
    val game = remember {
        item
    }
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(game.getCapsuleUrl231)
            .crossfade(false)
            .diskCacheKey(game.id)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = stringResource(R.string.app_name),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .size(70.dp, 45.dp)
    )
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun BookMarkIcon(
    scope: CoroutineScope,
    swipeableState: SwipeableState<Int>,
    onEvent: (onEvent: DecksterUiEvent) -> Unit,
    item: Game,
) {
    val targetAlphaForDirection = swipeableState.progress.fraction
    val alpha: Float by animateFloatAsState(
        targetValue = targetAlphaForDirection,
        animationSpec = tween(durationMillis = 50, easing = FastOutSlowInEasing)
    )
    IconButton(
        modifier = Modifier
            .padding(start = 28.dp)
            .graphicsLayer(alpha = alpha),
        onClick = {
            scope.launch {
                swipeableState.animateTo(0, tween(300, 0))
                onEvent(DecksterUiEvent.OnBookmarkToggle(item))
            }
        }) {
        Icon(
            tint = Color.White,
            imageVector =
            if (item.isBookmarked) {
                Icons.Rounded.Favorite
            } else {
                Icons.Rounded.FavoriteBorder
            },
            contentDescription = ""
        )
    }
}


fun getInputText(input: String) = when (input) {
    "gamepad" -> "\uD83C\uDFAE"
    "keyboard" -> "⌨"
    else -> input.capitalize(Locale.getDefault())
}

@Composable
fun Toolbar(
    navController: NavController,
) {
    SmallTopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = topGradientColor,
            titleContentColor = WhiteIcon,
            navigationIconContentColor = VerifiedGreen
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    color = Color.White,
                    text = "Deck",
                    style = steamTypographyBold.titleSmall
                )
                Text(
                    color = Color.White,
                    fontSize = 30.sp,
                    text = "Verified",
                    style = steamTypographyBold.labelMedium,
                )
            }
        },
    )
}
