package com.igorapp.deckster.platform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.igorapp.deckster.feature.home.DecksterDetailUiState
import com.igorapp.deckster.feature.home.DecksterSearchUiState
import com.igorapp.deckster.feature.home.DecksterUiState
import com.igorapp.deckster.feature.home.viewmodel.GameDetailsViewModel
import com.igorapp.deckster.feature.home.viewmodel.HomeListViewModel
import com.igorapp.deckster.feature.home.viewmodel.SearchListViewModel
import com.igorapp.deckster.feature.home.viewmodel.SplashScreenViewModel
import com.igorapp.deckster.platform.Destinations.Companion.gameDetails
import com.igorapp.deckster.platform.Destinations.Companion.gameDetailsArgs
import com.igorapp.deckster.ui.home.GameDetailsScreen
import com.igorapp.deckster.ui.home.HomeListScreen
import com.igorapp.deckster.ui.home.SearchListScreen
import com.igorapp.deckster.ui.theme.DecksterTheme
import com.igorapp.deckster.ui.theme.GradientBackground
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        val splash = installSplashScreen().apply {
//            setKeepOnScreenCondition {
//                true
//            }
        }
        super.onCreate(savedInstanceState)
        setContent {
            val splashScreenViewModel: SplashScreenViewModel = hiltViewModel()
            splashScreenViewModel.loadFirstPage {
                splash.setKeepOnScreenCondition {
                    false
                }
            }

            val navController = rememberNavController()
            DecksterTheme {
                GradientBackground {
                    NavHost(
                        navController = navController,
                        startDestination = Destinations.Home.name
                    ) {
                        composable(Destinations.Home.name) { HomeListScreen(navController) }
                        composable(
                            gameDetails,
                            gameDetailsArgs
                        ) {
                            GamesDetailsScreen(navController)
                        }
                        composable(Destinations.Settings.name) { Text(text = "Settings") }
                        composable(Destinations.Search.name) {
                            SearchGamesScreen(navController)
                        }
                    }
                }
            }

        }
    }


    @OptIn(ExperimentalLifecycleComposeApi::class)
    @Composable
    fun HomeListScreen(
        navController: NavController,
        viewModel: HomeListViewModel = hiltViewModel(),
    ) {
        val state: DecksterUiState by viewModel.uiState.collectAsStateWithLifecycle()
        HomeListScreen(state, navController, viewModel::onEvent)
    }

    @OptIn(ExperimentalLifecycleComposeApi::class)
    @Composable
    fun SearchGamesScreen(
        navController: NavController,
        viewModel: SearchListViewModel = hiltViewModel(),
    ) {
        val state: DecksterSearchUiState by viewModel.uiState.collectAsStateWithLifecycle()
        SearchListScreen(state, navController, viewModel::onEvent)
    }

    @OptIn(ExperimentalLifecycleComposeApi::class)
    @Composable
    fun GamesDetailsScreen(
        navController: NavController,
        viewModel: GameDetailsViewModel = hiltViewModel(),
    ) {
        val state: DecksterDetailUiState by viewModel.uiState.collectAsStateWithLifecycle()
        GameDetailsScreen(state, navController, viewModel::onEvent)
    }

    @OptIn(ExperimentalLifecycleComposeApi::class)
    @Composable
    fun SettingsScreen(
        navController: NavController,
        viewModel: HomeListViewModel = hiltViewModel(),
    ) {
        val state: DecksterUiState by viewModel.uiState.collectAsStateWithLifecycle()
        HomeListScreen(state, navController, viewModel::onEvent)
    }
}

enum class Destinations {
    Home,
    Search,
    Settings,
    Details;

    companion object {
        val gameDetailsArgs: List<NamedNavArgument> =
            listOf(navArgument(Arguments.gameId.name) { type = NavType.StringType })
        val gameDetails: String = "details/{${Arguments.gameId.name}}"
    }
}

enum class Arguments {
    gameId
}


