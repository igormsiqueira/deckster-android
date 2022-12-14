package com.igorapp.deckster.platform


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.igorapp.deckster.feature.home.DecksterUiState
import com.igorapp.deckster.feature.home.viewmodel.HomeListViewModel
import com.igorapp.deckster.ui.home.HomeListScreen

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            val viewModel: HomeListViewModel = hiltViewModel()
            val state: DecksterUiState by viewModel.uiState.collectAsStateWithLifecycle()
            HomeListScreen(state, viewModel::onEvent)
        }
        composable("search") {
            Text(text = "search")
        }
    }

}