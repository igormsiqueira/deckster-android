package com.igorapp.deckster.ui

import androidx.compose.foundation.Image
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
import com.igorapp.deckster.ui.home.StatusOptions
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
            Modifier.padding(8.dp),
            state = lazyListState,
            flingBehavior = rememberSnapperFlingBehavior(lazyListState),
        ) {
            items(
                count = games.size,
                key = { games[it].id }
            ) { idx ->
                GameGridItem(games[idx])
            }
        }
    }
}


fun LazyListScope.deckGameFilter(filterChanged: (String) -> Unit) {
    item {
        val options = StatusOptions.values().map {
            it.name
        }
        var selectedOption by remember { mutableStateOf("currentFilter") }
        val onSelectionChange = { text: String ->
            selectedOption = text
            filterChanged(selectedOption)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            options.forEach { text ->
                Row() {
                    Button(
                        shape = RoundedCornerShape(size = 8.dp),
                        modifier = Modifier.alpha(
                            if (text == selectedOption) {
                                1.0f
                            } else {
                                0.5f
                            }
                        ),
                        onClick = { onSelectionChange(text) }) {
                        Text(text = text)
                    }
                }
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
fun GameGridItem(item: Game) {
    Box(
        modifier = Modifier.padding(6.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(ImageUrlBuilder.getHeaderUrl(item.id))
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .size(340.dp, 180.dp)
        )
        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.badge_verified),
                tint = VerifiedGreen,
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
    Row(
        modifier = Modifier.padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(ImageUrlBuilder.getHeaderUrl(item.id))
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

@Composable
fun Toolbar_() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 20.dp, top = 20.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.badge_verified),
            tint = WhiteIcon,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 4.dp),
            contentDescription = "Verified Icon"
        )


    }
}