package com.igorapp.deckster.ui.home

import GameInfoResult
import android.content.Intent
import android.net.Uri
import android.widget.TextView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.BackdropValue
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.startActivity
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.igorapp.deckster.R
import com.igorapp.deckster.feature.home.DecksterDetailUiEvent
import com.igorapp.deckster.feature.home.DecksterDetailUiState
import com.igorapp.deckster.model.Game
import com.igorapp.deckster.ui.theme.GradientBackground
import com.igorapp.deckster.ui.theme.VerifiedGreen
import com.igorapp.deckster.ui.theme.WhiteIcon
import com.igorapp.deckster.ui.theme.bottomGradientColor
import com.igorapp.deckster.ui.theme.steamTypographyBold
import com.igorapp.deckster.ui.theme.topGradientColor
import com.igorapp.deckster.ui.utils.headerUpright
import extensions.urlsOnly
import kotlinx.coroutines.launch
import model.appdetails.Highlighted
import model.appdetails.Movie
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GameDetailsScreen(
    state: DecksterDetailUiState,
    navController: NavController,
    onEvent: (DecksterDetailUiEvent) -> Unit,
) {
    val backdropState = remember {
        BackdropScaffoldState(initialValue = BackdropValue.Revealed)
    }

    val scale by animateFloatAsState(
        targetValue = if (backdropState.currentValue == BackdropValue.Revealed) 1f else 0f,
    )

    GradientBackground {
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
                        GameInfoTab(gameInfo)
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

@OptIn(
    ExperimentalPagerApi::class, ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun GameInfoTab(gameInfo: GameInfoResult?) {

    val pages = listOf("About","On Deck" ,"Screenshots", "Videos", "Achievements", "Info")
    val pagerState = rememberPagerState()
    val scroll = rememberScrollState(0)
    val scope = rememberCoroutineScope()
    val viewingScreenshot = remember { mutableStateOf("") }
    val modifier = Modifier
        .fillMaxWidth()
//        .verticalScroll(scroll)
        .padding(8.dp)


    val interactionSource = remember { MutableInteractionSource() }


    var angle by remember { mutableStateOf(0f) }
    var zoom by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp.value
    val screenHeight = configuration.screenHeightDp.dp.value

    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = Color.Unspecified,
        contentColor = bottomGradientColor,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }
    ) {
        pages.forEachIndexed { index, title ->
            Tab(
                text = {
                    Text(
                        textAlign = TextAlign.Start,
                        text = title,
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            )
        }
    }


    if (viewingScreenshot.value.isNotEmpty()) {
        Dialog(
            onDismissRequest = {
                viewingScreenshot.value = ""
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                topBar = {
                    DialogToolbar {
                        angle = 0f
                        zoom = 1f
                        offsetX = 0f
                        offsetY = 0f
                        viewingScreenshot.value = ""
                    }
                },
                content = {
                    Box(
                        modifier = Modifier
                            .clip(RectangleShape)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                angle = 0f
                                zoom = 1f
                                offsetX = 0f
                                offsetY = 0f
                                viewingScreenshot.value = ""
                            }
                            .fillMaxSize()
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(viewingScreenshot.value)
                                .build(),
                            contentDescription = stringResource(R.string.app_name),
                            contentScale = ContentScale.Inside,
                            modifier = Modifier
                                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                                .align(Alignment.Center)
                                .graphicsLayer(
                                    scaleX = zoom,
                                    scaleY = zoom,
                                    rotationZ = angle
                                )
                                .pointerInput(Unit) {
                                    detectTransformGestures(
                                        onGesture = { _, pan, gestureZoom, _ ->
                                            zoom = (zoom * gestureZoom).coerceIn(1F..4F)
                                            if (zoom > 1) {
                                                val x = (pan.x * zoom)
                                                val y = (pan.y * zoom)
                                                val angleRad = angle * PI / 180.0

                                                offsetX =
                                                    (offsetX + (x * cos(angleRad) - y * sin(angleRad)).toFloat()).coerceIn(
                                                        -(screenWidth * zoom)..(screenWidth * zoom)
                                                    )
                                                offsetY =
                                                    (offsetY + (x * sin(angleRad) + y * cos(angleRad)).toFloat()).coerceIn(
                                                        -(screenHeight * zoom)..(screenHeight * zoom)
                                                    )
                                            } else {
                                                offsetX = 0F
                                                offsetY = 0F
                                            }
                                        }
                                    )
                                }
                        )
                    }
                }
            )
        }
    }

    HorizontalPager(
        count = pages.size,
        state = pagerState,
    ) { page ->

        when (page) {
            0 -> {
                Text(
                    modifier = modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
                            scaleX = 1f - pageOffset.coerceIn(0f, 1f)
                            scaleY = 1f - pageOffset.coerceIn(0f, 1f)
                        },
                    textAlign = TextAlign.Center,
                    text = gameInfo?.data?.detailedDescription.removeAllHtml(),
                    style = steamTypographyBold.titleMedium
                )
            }
            1 -> {
                Text(
                    text = "TODO Deck expanded report + protonDB info ", //TODO()
                    modifier = modifier
                )
            }

            2 -> {
                val thumbs = gameInfo?.data?.screenshots
                ImagesGrid(
                    modifier = modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
                            scaleX = 1f - pageOffset.coerceIn(0f, 1f)
                            scaleY = 1f - pageOffset.coerceIn(0f, 1f)
                        },
                    thumbs?.map { Pair(it.pathThumbnail, it.pathFull) }, 3
                ) { url ->
                    viewingScreenshot.value = url
                }
            }

            3 -> VideoGrid(
                modifier = modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
                        scaleX = 1f - pageOffset.coerceIn(0f, 1f)
                        scaleY = 1f - pageOffset.coerceIn(0f, 1f)
                    }, gameInfo
            )

            4 -> {
                val thumbs = gameInfo?.data?.achievements?.highlighted
                IconsGrid(
                    modifier = modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
                            scaleX = 1f - pageOffset.coerceIn(0f, 1f)
                            scaleY = 1f - pageOffset.coerceIn(0f, 1f)
                        },
                    thumbs,
                )
            }

            else -> {
                Text(
                    text = gameInfo?.data?.developers?.joinToString("-").orEmpty() + "\n" + gameInfo?.data?.aboutTheGame.orEmpty(),
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
fun DialogToolbar(onBack: () -> Unit) {
    SmallTopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = topGradientColor,
            titleContentColor = WhiteIcon,
            navigationIconContentColor = VerifiedGreen
        ),
        title = {
            Icon(
                modifier = Modifier
                    .clickable {
                        onBack.invoke()
                    },
                tint = WhiteIcon,
                imageVector = Icons.Filled.Close, contentDescription = ""
            )
        },
    )
}


@Composable
fun VideoGrid(modifier: Modifier, gameInfo: GameInfoResult?) {
    val videos = gameInfo?.data?.movies
    LazyVerticalGrid(
        modifier = modifier.padding(start = 16.dp, end = 16.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        videos?.forEach {
            item {
                VideoItem(video = it)
            }

        }
    }
}

@Composable
fun ImagesGrid(
    modifier: Modifier,
    gameInfo: List<Pair<String, String>>?,
    count: Int = 4,
    onClick: (String) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier.padding(start = 16.dp, end = 16.dp),
        columns = GridCells.Adaptive(80.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        gameInfo?.forEach {
            item {
                ImageItem(data = it, onClick)
            }
        }
    }
}

@Composable
fun IconsGrid(
    modifier: Modifier,
    achievements: List<Highlighted>?,
    onClick: (String) -> Unit = { }
) {
    LazyVerticalGrid(
        modifier = modifier.padding(start = 16.dp, end = 16.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        achievements?.forEach {
            item {
                IconItem(data = it, onClick)
            }
        }
    }
}

@Composable
fun IconItem(data: Highlighted, onClick: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(data.path)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp, 80.dp)
                .clickable {
                    onClick.invoke(data.name)
                }
                .clip(RoundedCornerShape(8.dp))
        )
        Text(
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            text = data.name,
            color = Color.Black,
        )
    }
}

@Composable
fun ImageItem(data: Pair<String, String>, onClick: (String) -> Unit) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(data.first)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = stringResource(R.string.app_name),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(80.dp, 80.dp)
            .clickable {
                onClick.invoke(data.second)
            }
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun VideoItem(video: Movie) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(video.thumbnail)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = stringResource(R.string.app_name),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(128.dp, 128.dp)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.mp4.max))
                intent.setDataAndType(Uri.parse(video.mp4.max), "video/mp4")
                startActivity(context, intent, null)
            }
            .clip(RoundedCornerShape(8.dp))
    )
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
    } ?: GameStatus.Unknown

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

