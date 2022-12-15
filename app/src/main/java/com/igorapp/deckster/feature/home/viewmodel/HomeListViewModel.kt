package com.igorapp.deckster.feature.home.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igorapp.deckster.data.GameRepository
import com.igorapp.deckster.feature.home.DecksterUiEvent
import com.igorapp.deckster.feature.home.DecksterUiState
import com.igorapp.deckster.model.Game
import com.igorapp.deckster.network.Deckster
import com.igorapp.deckster.network.Result.*
import com.igorapp.deckster.network.asResult
import com.igorapp.deckster.ui.home.GameStatus
import com.igorapp.deckster.ui.home.GameStatus.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeListViewModel @Inject constructor(
    private val gameService: Deckster,
    private val repository: GameRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var choice: List<Game> = emptyList()
    private var games: List<Game> = emptyList()
    private var _uiState = MutableStateFlow<DecksterUiState>(DecksterUiState.Loading)
    val uiState: StateFlow<DecksterUiState> = _uiState

    init {
        loadFirstPage()
        setupUiState()
    }

    private fun setupUiState() {
        viewModelScope.launch {
            val localGamesStream: Flow<List<Game>> = getFilteredGamesFlow()
            val spotlightGamesStream: Flow<List<Game>> = gameService.loadChoiceGames()

            combine(localGamesStream, spotlightGamesStream, ::Pair).asResult()
                .map { result ->
                    when (result) {
                        is Success -> {
                            games = result.data.first
                            choice = result.data.second
                            DecksterUiState.Content(
                                result.data.first,
                                result.data.second,
                                savedStateHandle.get<GameStatus>(GAME_FILTER) ?: Verified
                            )
                        }

                        is Error -> DecksterUiState.Error(result.exception)
                        is Loading -> DecksterUiState.Loading
                    }
                }.distinctUntilChanged().collect { resultingState ->
                    _uiState.value = resultingState
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getFilteredGamesFlow() = savedStateHandle.getStateFlow(
        GAME_FILTER,
        Verified
    ).flatMapLatest { filter ->
        when (filter.code) {
            Backlog.code -> repository.getBacklogGames()
            else -> repository.getGamesByFilter(filter.code)
        }
    }


    fun onEvent(decksterUiEvent: DecksterUiEvent) {
        return when (decksterUiEvent) {
            is DecksterUiEvent.OnLoadMore -> onLoadMore()
            is DecksterUiEvent.OnSearchToggle -> {
                when (decksterUiEvent.searchIsVisible) {
                    true -> showSearch()
                    false -> hideSearch()
                }
            }

            is DecksterUiEvent.OnSearch -> searchForGames(decksterUiEvent.term)
            is DecksterUiEvent.OnFilterChange -> filterGames(decksterUiEvent.option)
            is DecksterUiEvent.OnBookmarkToggle -> toggleBookmark(decksterUiEvent.game)
        }
    }

    private fun toggleBookmark(game: Game) {
        viewModelScope.launch {
            repository.updateGame(game.copy(isBookmarked = game.isBookmarked.not()))
        }
    }

    private fun hideSearch() {
        _uiState.value = DecksterUiState.Content(games, choice, Verified)
    }

    private fun showSearch() {
        _uiState.value = DecksterUiState.Searching()
    }

    private fun searchForGames(term: String) {
        viewModelScope.launch {
            gameService.searchByGame(term).collect { games ->
                _uiState.value = DecksterUiState.Searching(games, term)
            }
        }
    }

    private fun filterGames(option: String) {
        savedStateHandle[GAME_FILTER] = valueOf(option)
    }

    private fun onLoadMore() {
        viewModelScope.launch {
            gameService.loadGames(INITIAL_PAGE, SIZE).flowOn(Dispatchers.IO).collect { games ->
                repository.addGames(games)
                INITIAL_PAGE++ //todo get page by count e.g count/size = page or implement pagging3
            }
        }
    }

    private fun loadFirstPage() {
        // TODO: move to splashscreen
        viewModelScope.launch {
            gameService.loadGames(INITIAL_PAGE, SIZE).flowOn(Dispatchers.IO).collect { games ->
                repository.addGames(games)
            }
        }
    }

    companion object {
        var INITIAL_PAGE = 0
        const val SIZE = 20
        const val GAME_FILTER = "game_status_filter"
    }

}