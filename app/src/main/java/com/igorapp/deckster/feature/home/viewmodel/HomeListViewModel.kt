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
import com.igorapp.deckster.ui.home.StatusOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeListViewModel @Inject constructor(
    private val service: Deckster,
    private val repository: GameRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    init {
        loadFirstPage()
    }

    val uiState: StateFlow<DecksterUiState> = decksterUiState(
        gameService = service,
        repository = repository,
        savedStateHandle = savedStateHandle
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DecksterUiState.Loading
    )

    private fun decksterUiState(
        gameService: Deckster,
        repository: GameRepository,
        savedStateHandle: SavedStateHandle,
    ): Flow<DecksterUiState> {
        val localGamesStream =
            savedStateHandle.getStateFlow(
                GAME_FILTER,
                StatusOptions.Verified
            ).flatMapLatest { query ->
                repository.getGamesByFilter(query.option)
            }

        val choiceStream: Flow<List<Game>> = gameService.loadChoiceGames()

        return combine(localGamesStream, choiceStream, ::Pair).asResult()
            .map { result ->
                when (result) {
                    is Success -> DecksterUiState.Success(
                        result.data.first,
                        result.data.second,
                        StatusOptions.Verified
                    )

                    is Error -> DecksterUiState.Error(result.exception)
                    is Loading -> DecksterUiState.Loading
                }
            }
    }

    fun onEvent(decksterUiEvent: DecksterUiEvent) {
        return when (decksterUiEvent) {
            is DecksterUiEvent.OnLoadMore -> onLoadMore()
            is DecksterUiEvent.OnSearch -> Unit //todo
            is DecksterUiEvent.OnFilterChange -> filterGames(decksterUiEvent.option)
        }
    }

    private fun filterGames(option: String) {
        savedStateHandle[GAME_FILTER] = StatusOptions.valueOf(option)
    }

    private fun onLoadMore() {
        viewModelScope.launch {
            service.loadGames(INITIAL_PAGE, SIZE).flowOn(Dispatchers.IO).collect { games ->
                repository.addGames(games)
                INITIAL_PAGE++ //todo get page by count e.g count/size = page or implement pagging3
            }
        }
    }

    private fun loadFirstPage() {
        // TODO: move to splashscreen
        viewModelScope.launch {
            service.loadGames(INITIAL_PAGE, SIZE).flowOn(Dispatchers.IO).collect { games ->
                repository.addGames(games)
            }
        }
    }

    companion object {
        var INITIAL_PAGE = 1
        const val SIZE = 20
        const val GAME_FILTER = "game_status_filter"
    }

}