package com.igorapp.deckster.feature.home

import com.igorapp.deckster.model.Game
import com.igorapp.deckster.ui.home.GameStatus

sealed class DecksterUiState {
    class Error(throwable: Throwable?) : DecksterUiState()
    object Loading : DecksterUiState()
    object Searching : DecksterUiState()
    data class Success(
        val games: List<Game>,
        val choiceGames: List<Game>,
        val filter: GameStatus
    ) : DecksterUiState()
}

