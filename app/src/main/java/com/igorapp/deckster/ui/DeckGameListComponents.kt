package com.igorapp.deckster.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SwipeableState
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.igorapp.deckster.R
import com.igorapp.deckster.feature.home.DecksterUiEvent
import com.igorapp.deckster.feature.home.DecksterUiState
import com.igorapp.deckster.model.Game
import com.igorapp.deckster.ui.home.GameStatus
import com.igorapp.deckster.ui.theme.*
import com.igorapp.deckster.ui.utils.dipToPx
import com.igorapp.deckster.ui.utils.getCapsuleUrl
import com.igorapp.deckster.ui.utils.getCapsuleUrl231
import com.igorapp.deckster.ui.utils.headerCapsuleImageUrl
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
        Text(
            color = Color.White,
            fontSize = 16.sp,
            text = "Loading...",
            style = steamTypographyBold.labelSmall,
        )
    }
}

@Composable
fun GifImage(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context).components {
        add(ImageDecoderDecoder.Factory())
    }
        .build()
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context).data(data = R.drawable.pal)
                .apply(block = {
                    size(Size.ORIGINAL)
                }).build(), imageLoader = imageLoader
        ),
        contentDescription = null,
        modifier = modifier.fillMaxWidth(),
    )
}

fun LazyListScope.deckGameListHeaderScreen(lazyListState: LazyListState, games: List<Game>) {
    item {
        SpotlightGames(lazyListState, games)
    }
}

@Composable
@OptIn(ExperimentalSnapperApi::class)
fun SpotlightGames(
    lazyListState: LazyListState,
    games: List<Game>
) {
    LazyRow(
        Modifier.padding(top = 16.dp, bottom = 8.dp),
        state = lazyListState,
        flingBehavior = rememberSnapperFlingBehavior(lazyListState),
    ) {
        items(
            count = games.size,
            key = { games[it].id }
        ) { idx ->
            GameGridItem(games[idx], idx)
        }
    }
}


@OptIn(ExperimentalSnapperApi::class)
fun LazyListScope.deckGameFilter(
    lazyListState: LazyListState,
    currentFilter: GameStatus,
    filterChanged: (String) -> Unit
) {
    val options = GameStatus.values().map(GameStatus::name)
    item {
        var filter by remember { mutableStateOf(currentFilter.name) }
        val onSelectionChange = { text: String ->
            filter = text
            filterChanged(filter)
        }
        LazyRow(
            state = lazyListState,
            flingBehavior = rememberSnapperFlingBehavior(lazyListState),
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .fillMaxWidth()
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
    idx: Int
) {
    Button(
        shape = RoundedCornerShape(size = 8.dp),
        modifier = Modifier
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
    games: List<Game>, onEvent: (onEvent: DecksterUiEvent) -> Unit,
) {
    items(
        count = games.size,
        key = { games[it].id }
    ) { idx ->
        GameListItem(games[idx], onEvent)
    }
}

fun LazyListScope.searchDeckGameListScreen(games: List<Game>) {
    items(
        count = games.size,
        key = { games[it].id }
    ) { idx ->
        SearchGameListItem(games[idx])
    }
}


@Composable
fun GameGridItem(item: Game, idx: Int) {
    Box(
        modifier = Modifier.padding(
            start = if (idx == 0) {
                20.dp
            } else {
                8.dp
            }, end = 8.dp
        ),
        contentAlignment = Alignment.BottomStart
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.headerCapsuleImageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                /*.size(196.dp, 342.dp)*/
                .size(342.dp, 196.dp)
        )
    }
}

@Composable
fun SearchGameListItem(item: Game) {
    var isExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(start = 16.dp),
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
            contentScale = ContentScale.Crop,
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
fun GameListItem(
    item: Game, onEvent: (onEvent: DecksterUiEvent) -> Unit,
) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
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
private fun SwipeableGameItem(
    swipeableState: SwipeableState<Int>,
    item: Game
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
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(item.getCapsuleUrl231)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
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
    val alpha: Float by animateFloatAsState(
        targetValue = swipeableState.progress.fraction,
        animationSpec = tween(durationMillis = 50, easing = FastOutSlowInEasing)
    )
    IconButton(
        modifier = Modifier
            .padding(start = 28.dp)
            .graphicsLayer(alpha = alpha),
        onClick = {
            scope.launch {
                onEvent(DecksterUiEvent.OnBookmarkToggle(item))
                swipeableState.animateTo(0, tween(400, 0))
            }
        }) {
        Icon(
            tint = Color.White,
            imageVector = if (item.isBookmarked) {
                Icons.Rounded.Favorite
            } else {
                Icons.Rounded.FavoriteBorder
            }, contentDescription = ""
        )
    }
}


fun getInputText(input: String) = when (input) {
    "gamepad" -> "\uD83C\uDFAE"
    "keyboard" -> "⌨"
    else -> input.toString().capitalize(Locale.getDefault())
}

@Composable
fun DeckGameListErrorScreen() {
    Text(text = "Error")
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun Toolbar(
    onEvent: (onEvent: DecksterUiEvent) -> Unit,
    state: DecksterUiState,
) {
    val queryString = if (state is DecksterUiState.Searching) {
        state.term.orEmpty()
    } else {
        ""
    }

    var showSearch by remember { mutableStateOf(false) }
    var query: String by rememberSaveable { mutableStateOf(queryString) }
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    AnimatedContent(
        targetState = showSearch,
        transitionSpec = {
            if (targetState > initialState) {
                slideInVertically { height -> height } + fadeIn() with
                        slideOutVertically { height -> -height } +
                        fadeOut()
            } else {
                slideInVertically { height -> -height } + fadeIn() with
                        slideOutVertically { height -> height } +
                        fadeOut()
            }
        }
    ) { searchIsVisible ->

        if (searchIsVisible) {
            val focusRequester = FocusRequester()

            OutlinedTextField(
                value = query,
                placeholder = {
                    Text(
                        color = WhiteIcon,
                        text = "Search game by Title",
                    )
                },
                onValueChange = { onQueryChanged ->
                    query = onQueryChanged
                    onEvent(DecksterUiEvent.OnSearch(query))
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    textColor = Color.White,
                    placeholderColor = Color.Transparent,
                    cursorColor = PurpleGrey40,
                ),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        color = topGradientColor
                    )
                    .onFocusChanged { focusState ->
                        showClearButton = (focusState.isFocused)
                    },
                maxLines = 1,
                textStyle = MaterialTheme.typography.subtitle1,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                }),
                trailingIcon = {
                    IconButton(onClick = {
                        if (query.isEmpty()) {
                            keyboardController?.hide()
                            showSearch = false
                            onEvent(DecksterUiEvent.OnSearchToggle(showSearch))
                        } else {
                            query = ""
                        }
                    }) {
                        androidx.compose.material.Icon(
                            imageVector = Icons.Rounded.Clear,
                            tint = WhiteIcon,
                            contentDescription = "Clear Icon"
                        )
                    }
                },
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        } else {
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
                            style = steamTypographyBold.labelSmall,
                        )


                    }
                },
                actions = {
                    Icon(
                        modifier = Modifier
                            .clickable {
                                showSearch = !showSearch
                                onEvent(DecksterUiEvent.OnSearchToggle(showSearch))
                            },
                        tint = WhiteIcon,
                        imageVector = Icons.Filled.Search, contentDescription = ""
                    )
                },
            )
        }
    }
}