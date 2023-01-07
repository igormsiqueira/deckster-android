package com.igorapp.deckster.feature.home.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igorapp.deckster.data.GameRepository
import com.igorapp.deckster.network.Deckster
import com.igorapp.deckster.ui.home.GameStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val gameService: Deckster,
    private val repository: GameRepository,
    private val savedStateHandle: SavedStateHandle,
    ) : ViewModel() {

     fun loadFirstPage(callback: () -> Unit) {
        viewModelScope.launch {
            gameService.loadGames(
                INITIAL_PAGE,
                SIZE,
                savedStateHandle.get<GameStatus>(HomeListViewModel.GAME_FILTER) ?: GameStatus.Verified
            ).flowOn(Dispatchers.IO).collect { games ->
                repository.addGames(games)
                callback()
            }
        }
    }

    companion object {
        var INITIAL_PAGE = 1
        const val SIZE = 20
    }

}