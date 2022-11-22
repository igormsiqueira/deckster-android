package com.igorapp.deckster.ui.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow

@Composable
fun LazyListState.onBottomReached(loadMore: () -> Unit) {
    val thresholdReached = remember {
        derivedStateOf {
            layoutInfo.visibleItemsInfo.lastOrNull()?.index == (layoutInfo.totalItemsCount - 1)
        }
    }
    LaunchedEffect(key1 = thresholdReached) {
        snapshotFlow(thresholdReached::value).collect { shouldFetchMore ->
            if (shouldFetchMore) loadMore.invoke()
        }
    }
}