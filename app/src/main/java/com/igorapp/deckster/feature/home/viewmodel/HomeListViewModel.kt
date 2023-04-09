package com.igorapp.deckster.feature.home.viewmodel

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
import com.igorapp.deckster.ui.home.GameStatus.Verified
import com.igorapp.deckster.ui.home.GameStatus.valueOf
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
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var _uiState = MutableStateFlow<DecksterUiState>(DecksterUiState.Loading)
    val uiState: StateFlow<DecksterUiState> = _uiState

    private val localGamesStream: Flow<List<Game>> = getFilteredGamesFlow()
    private val spotlightGamesStream: Flow<List<Game>> = repository.getSpotlightGames()
    private val state = setUi(localGamesStream, spotlightGamesStream)

    private fun setUi(
        localGamesStream: Flow<List<Game>>,
        spotlightGamesStream: Flow<List<Game>>
    ): Flow<DecksterUiState> {
        return combine(localGamesStream, spotlightGamesStream, ::Pair).asResult()
            .map { result ->
                when (result) {
                    is Success -> {
                        DecksterUiState.Content(
                            result.data.first,
                            result.data.second,
                            savedStateHandle.get<GameStatus>(GAME_FILTER) ?: Verified
                        )
                    }

                    is Error -> DecksterUiState.Error(result.exception)
                    is Loading -> DecksterUiState.Loading
                }
            }
    }


    init {
        viewModelScope.launch {
            state.collect { resultingState ->
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
            GameStatus.Backlog.code -> repository.getBacklogGames()
            else -> repository.getGamesByFilter(filter.code)
        }
    }

    fun onEvent(decksterUiEvent: DecksterUiEvent) {
        return when (decksterUiEvent) {
            is DecksterUiEvent.OnLoadMore -> onLoadMore()
            is DecksterUiEvent.OnFilterChange -> filterGames(decksterUiEvent.option)
            is DecksterUiEvent.OnBookmarkToggle -> toggleBookmark(decksterUiEvent.game)
            is DecksterUiEvent.OnSearch -> Unit
        }
    }

    private fun toggleBookmark(game: Game) {
        viewModelScope.launch {
            repository.updateGame(game.id, !game.isBookmarked)
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
                savedStateHandle.get<GameStatus>(GAME_FILTER) ?: Verified
            ).flowOn(Dispatchers.IO).collect { games ->
                repository.addGames(games)
                INITIAL_PAGE++ //todo get page by count e.g count/size = page or implement pagging3
            }
        }
    }

    companion object {
        var INITIAL_PAGE = 0
        const val SIZE = 15
        const val GAME_FILTER = "game_status_filter"
    }

}