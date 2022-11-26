package com.igorapp.deckster.feature.home

import com.igorapp.deckster.model.Game

sealed interface DecksterUiState {
    class Error(throwable: Throwable?) : DecksterUiState
    object Loading : DecksterUiState
    data class Success(val games: List<Game>, val choiceGames: List<Game>) : DecksterUiState
}