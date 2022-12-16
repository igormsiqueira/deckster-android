package com.igorapp.deckster.platform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.igorapp.deckster.feature.home.DecksterUiState
import com.igorapp.deckster.feature.home.viewmodel.HomeListViewModel
import com.igorapp.deckster.ui.home.HomeListScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            HomeListScreen()
        }
    }


    @OptIn(ExperimentalLifecycleComposeApi::class)
    @Composable
    fun HomeListScreen(viewModel: HomeListViewModel = hiltViewModel()) {
        val state: DecksterUiState by viewModel.uiState.collectAsStateWithLifecycle()
        HomeListScreen(state, viewModel::onEvent)
    }
}
