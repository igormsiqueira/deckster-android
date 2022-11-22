package com.igorapp.deckster.platform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.igorapp.deckster.feature.home.DecksterUiState
import com.igorapp.deckster.feature.home.viewmodel.HomeListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: HomeListViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeListScreen(viewModel)
        }
    }

    @OptIn(ExperimentalLifecycleComposeApi::class)
    @Composable
    fun HomeListScreen(viewModel: HomeListViewModel = hiltViewModel()) {
        val state: DecksterUiState by viewModel.uiState.collectAsStateWithLifecycle()
        com.igorapp.deckster.ui.home.HomeListScreen(state, viewModel::onEvent)
    }

}
