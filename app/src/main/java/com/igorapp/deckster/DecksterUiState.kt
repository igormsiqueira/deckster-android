package com.igorapp.deckster

import com.igorapp.deckster.model.Game

sealed interface DecksterUiState {
    object Error : DecksterUiState
    object Loading : DecksterUiState
    data class Success(val games: List<Game>) : DecksterUiState
}
