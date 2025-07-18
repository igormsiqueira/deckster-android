package com.igorapp.deckster.feature.home.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igorapp.deckster.data.GameRepository
import com.igorapp.deckster.feature.home.DecksterSearchUiState
import com.igorapp.deckster.feature.home.DecksterUiEvent
import com.igorapp.deckster.network.Deckster
import com.igorapp.deckster.ui.home.GameStatus
import com.igorapp.deckster.ui.home.GameStatus.valueOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchListViewModel @Inject constructor(
    private val gameService: Deckster,
    private val repository: GameRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {


    private var _uiState = MutableStateFlow<DecksterSearchUiState>(DecksterSearchUiState.Empty())
    val uiState: StateFlow<DecksterSearchUiState> = _uiState

    fun onEvent(decksterUiEvent: DecksterUiEvent) {
        return when (decksterUiEvent) {
            is DecksterUiEvent.OnLoadMore -> onLoadMore()
            is DecksterUiEvent.OnSearch -> searchForGames(decksterUiEvent.term)
            is DecksterUiEvent.OnFilterChange -> filterGames(decksterUiEvent.option)
            else -> {}
        }
    }

    private fun searchForGames(term: String) {
        viewModelScope.launch {
            gameService.searchByGame(term).collect { games ->
                _uiState.value = DecksterSearchUiState.Content(games, term)
            }
        }
    }

    private fun filterGames(option: String) {
        savedStateHandle[GAME_FILTER] = valueOf(option)
    }

    private fun onLoadMore() {
        viewModelScope.launch {
            gameService.loadGames(
                INITIAL_PAGE,
                SIZE,
                savedStateHandle.get<GameStatus>(HomeListViewModel.GAME_FILTER) ?: GameStatus.Verified
            ).flowOn(Dispatchers.IO).collect { games ->
                repository.addGames(games)
                INITIAL_PAGE++ //todo get page by count e.g count/size = page or implement pagging3
            }
        }
    }


    companion object {
        // Keep in sync with SplashScreenViewModel to prevent duplicate first
        // page fetches when loading additional results.
        var INITIAL_PAGE = 1
        const val SIZE = 20
        const val GAME_FILTER = "game_status_filter"
    }

}