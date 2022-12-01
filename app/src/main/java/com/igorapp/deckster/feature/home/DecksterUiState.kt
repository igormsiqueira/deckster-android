package com.igorapp.deckster.feature.home

import com.igorapp.deckster.model.Game
import com.igorapp.deckster.ui.home.StatusOptions

sealed class DecksterUiState {
    class Error(throwable: Throwable?) : DecksterUiState()
    object Loading : DecksterUiState()
    data class Success(
        val games: List<Game>,
        val choiceGames: List<Game>,
        val filter: StatusOptions
    ) : DecksterUiState()
}

