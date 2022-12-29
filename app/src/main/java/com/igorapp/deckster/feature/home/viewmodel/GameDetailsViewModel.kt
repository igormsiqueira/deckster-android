package com.igorapp.deckster.feature.home.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igorapp.deckster.data.GameRepository
import com.igorapp.deckster.feature.home.DecksterDetailUiEvent
import com.igorapp.deckster.feature.home.DecksterDetailUiState
import com.igorapp.deckster.model.Game
import com.igorapp.deckster.network.Deckster
import com.igorapp.deckster.network.Result
import com.igorapp.deckster.network.asResult
import com.igorapp.deckster.platform.Arguments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import model.GameInfoResult
import model.SteamShots
import javax.inject.Inject

@HiltViewModel
class GameDetailsViewModel @Inject constructor(
    private val gameService: Deckster,
    private val repository: GameRepository,
    private val savedStateHandle: SavedStateHandle,
    private val steamShots: SteamShots,
) : ViewModel() {

    private val gameId: String = checkNotNull(savedStateHandle[Arguments.gameId.name])
    private val game: Flow<Game> = repository.searchGameById(gameId)
    private val gameInfo: Flow<GameInfoResult?> = steamShots.obtain(gameId)


    private var _uiState = MutableStateFlow<DecksterDetailUiState>(DecksterDetailUiState.Loading)
    val uiState: StateFlow<DecksterDetailUiState> = _uiState

    fun onEvent(decksterUiEvent: DecksterDetailUiEvent) {
        when (decksterUiEvent) {
            is DecksterDetailUiEvent.OnDetailsBookmarkToggle -> onBookmarkToggle(decksterUiEvent.game)
            is DecksterDetailUiEvent.OnGameDetails -> Unit
        }
    }


    init {
        viewModelScope.launch {
            combine(game, gameInfo, ::Pair).asResult().map { result ->
                when (result) {
                    is Result.Error -> DecksterDetailUiState.Error(result.exception)
                    is Result.Success -> DecksterDetailUiState.Content(result.data)
                    Result.Loading -> DecksterDetailUiState.Loading
                }
            }.collect { resultingState ->
                _uiState.value = resultingState
            }
        }
    }

    private fun onBookmarkToggle(game: Game) {

    }
}

