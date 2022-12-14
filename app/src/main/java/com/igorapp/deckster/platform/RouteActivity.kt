package com.igorapp.deckster.platform

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.igorapp.deckster.feature.home.DecksterUiState
import com.igorapp.deckster.feature.home.viewmodel.HomeListViewModel
import com.igorapp.deckster.feature.home.viewmodel.SplashScreenViewModel
import com.igorapp.deckster.ui.DeckGameListLoadingIndicator
import com.igorapp.deckster.ui.home.HomeListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        val viewModel: SplashScreenViewModel by viewModels()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            true
        }


        setContent {
            DeckGameListLoadingIndicator()
        }
        viewModel.loadFirstPage {
            splashScreen.setKeepOnScreenCondition {
                false
            }
        }
    }

    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

}
