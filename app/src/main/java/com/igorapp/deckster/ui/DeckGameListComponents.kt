package com.igorapp.deckster.ui

import android.view.SearchEvent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
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
import com.igorapp.deckster.model.Game
import com.igorapp.deckster.ui.home.GameStatus
import com.igorapp.deckster.ui.theme.*
import com.igorapp.deckster.ui.utils.ImageUrlBuilder
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
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

fun LazyListScope.deckGameListScreen(games: List<Game>) {
    items(
        count = games.size,
        key = { games[it].id }
    ) { idx ->
        GameListItem(games[idx])
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
                .data(ImageUrlBuilder.getCapsuleHeaderUrl(item.id))
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
fun GameListItem(item: Game) {
    var isExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(start = 20.dp)
            .animateContentSize()
            .sizeBasedOnStatus(isExpanded)
            .clickable {
                isExpanded = !isExpanded
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
//        AsyncImage(
//            model = ImageRequest.Builder(LocalContext.current)
//                .data(ImageUrlBuilder.getCapsuleUrl231(item.id))
//                .crossfade(true)
//                .build(),
//            placeholder = painterResource(R.drawable.ic_launcher_foreground),
//            contentDescription = stringResource(R.string.app_name),
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .clip(RoundedCornerShape(8.dp))
//                .size(70.dp, 45.dp)
//        )
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

enum class States {
    EXPANDED,
    COLLAPSED
}

@ExperimentalMaterialApi
@Composable
fun FullHeightBottomSheet(
    header: @Composable () -> Unit,
    body: @Composable () -> Unit
) {
    val swipeableState = rememberSwipeableState(initialValue = States.COLLAPSED)
    val scrollState = rememberScrollState()

    BoxWithConstraints {
        val constraintsScope = this
        val maxHeight = with(LocalDensity.current) {
            constraintsScope.maxHeight.toPx()
        }

        val connection = remember {
            object : NestedScrollConnection {

                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    val delta = available.y
                    return if (delta > 0) {
                        swipeableState.performDrag(delta).toOffset()
                    } else {
                        Offset.Zero
                    }
                }

                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    val delta = available.y
                    return swipeableState.performDrag(delta).toOffset()
                }

                override suspend fun onPreFling(available: Velocity): Velocity {
                    return if (available.y > 0 && scrollState.value == 0) {
                        swipeableState.performFling(available.y)
                        available
                    } else {
                        Velocity.Zero
                    }
                }

                override suspend fun onPostFling(
                    consumed: Velocity,
                    available: Velocity
                ): Velocity {
                    swipeableState.performFling(velocity = available.y)
                    return super.onPostFling(consumed, available)
                }

                private fun Float.toOffset() = Offset(0f, this)
            }
        }

        Box(
            Modifier
                .swipeable(
                    state = swipeableState,
                    orientation = Orientation.Vertical,
                    anchors = mapOf(
                        0f to States.EXPANDED,
                        maxHeight to States.COLLAPSED,
                    )
                )
                .nestedScroll(connection)
                .offset {
                    IntOffset(
                        0,
                        swipeableState.offset.value.roundToInt()
                    )
                }
        ) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .background(Color.White)
            ) {
                header()
                Box(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                ) {
                    body()
                }
            }
        }
    }
}

private fun Modifier.sizeBasedOnStatus(isExpanded: Boolean) =
    composed { if (isExpanded) fillMaxSize() else this }

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
fun Toolbar(onEvent: (onEvent: DecksterUiEvent) -> Unit) {

    var showSearch by remember { mutableStateOf(false) }
    var query: String by rememberSaveable { mutableStateOf("") }
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current


    AnimatedContent(
        targetState = showSearch,
        transitionSpec = {
            if (targetState > initialState) {
                slideInVertically { height -> height } + fadeIn() with
                        slideOutVertically { height -> -height } + fadeOut()
            } else {
                slideInVertically { height -> -height } + fadeIn() with
                        slideOutVertically { height -> height } + fadeOut()
            }.using(
                SizeTransform(clip = false)
            )
        }
    ) { targetState ->
        if (targetState) {
            onEvent(DecksterUiEvent.OnSearchToggle)
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
                    if (onQueryChanged.isNotEmpty()) {
                        // performQuery(onQueryChanged)
                        onEvent(DecksterUiEvent.OnSearch(query))
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    textColor = Color.White,
                    placeholderColor = Color.Transparent,
                    cursorColor = PurpleGrey40,
                ),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth().padding(bottom = 6.dp)
                    .onFocusChanged { focusState ->
                        showClearButton = (focusState.isFocused)
                    },
                maxLines = 1,
                textStyle = MaterialTheme.typography.subtitle1,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                }),
                trailingIcon = {
                    IconButton(onClick = {
                        if (query.isEmpty()) {
                            keyboardController?.hide()
                            showSearch = false
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
                    Text(
                        color = Color.White,
                        text = "Deck",
                        style = steamTypographyBold.titleSmall
                    )

                },
                actions = {
                    Icon(
                        modifier = Modifier
                            .clickable {
                                showSearch = !showSearch
                            },
                        tint = WhiteIcon,
                        imageVector = Icons.Filled.Search, contentDescription = ""
                    )
                },
            )
        }
    }
}