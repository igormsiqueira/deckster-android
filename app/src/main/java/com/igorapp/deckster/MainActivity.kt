package com.igorapp.deckster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.igorapp.deckster.ui.theme.DecksterTheme
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
        HomeListScreen(state, viewModel::onEvent)
    }
}
