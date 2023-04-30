package com.igorapp.deckster.feature.home.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igorapp.deckster.data.GameRepository
import com.igorapp.deckster.network.Deckster
import com.igorapp.deckster.ui.home.GameStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
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
            val count = repository.getGamesCount()

            if (count > 0) {
                callback.invoke()
            }

            val spotGames = gameService.loadChoiceGames()
            val allGames = gameService.loadGames(
                INITIAL_PAGE,
                SIZE,
                savedStateHandle.get<GameStatus>(HomeListViewModel.GAME_FILTER)
                    ?: GameStatus.AllGames
            )
            combine(spotGames, allGames, ::Pair).flowOn(Dispatchers.IO).collect { games ->
                repository.addGames(games.first + games.second)
                callback.invoke()
            }
        }
    }

    companion object {
        var INITIAL_PAGE = 1
        const val SIZE = 20
    }

}