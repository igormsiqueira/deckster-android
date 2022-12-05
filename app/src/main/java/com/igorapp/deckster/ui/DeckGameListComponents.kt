package com.igorapp.deckster.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.igorapp.deckster.R
import com.igorapp.deckster.model.Game
import com.igorapp.deckster.ui.home.GameStatus
import com.igorapp.deckster.ui.theme.*
import com.igorapp.deckster.ui.utils.ImageUrlBuilder
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import java.util.*

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

@OptIn(ExperimentalSnapperApi::class)
fun LazyListScope.deckGameListHeaderScreen(lazyListState: LazyListState, games: List<Game>) {
    item {
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
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(342.dp, 196.dp)
        )
        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.badge_verified),
                tint = WhiteIcon,
                modifier = Modifier
                    .padding(4.dp),
                contentDescription = "Verified Icon"
            )
            Text(
                color = Color.White,
                text = item.game,
                fontWeight = FontWeight.Bold,
            )

        }
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
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(ImageUrlBuilder.getCapsuleUrl231(item.id))
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(70.dp, 45.dp)
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


@Composable
fun Toolbar() {
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
                modifier = Modifier.size(30.dp),
                tint = WhiteIcon,
                imageVector = Icons.Filled.Search, contentDescription = ""
            )
        },
        navigationIcon = {
            Icon(
                modifier = Modifier
                    .size(40.dp),
                painter = painterResource(id = R.drawable.ic_deckicon), contentDescription = ""
            )
        }
    )
}