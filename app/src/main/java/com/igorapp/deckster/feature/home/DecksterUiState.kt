package com.igorapp.deckster.feature.home

import com.igorapp.deckster.model.Game
import com.igorapp.deckster.ui.home.GameStatus
import model.GameInfoResult

sealed class DecksterUiState {
    class Error(throwable: Throwable?) : DecksterUiState()
    object Loading : DecksterUiState()
    data class Content(
        val games: List<Game>,
        val choiceGames: List<Game>,
        val filter: GameStatus,
    ) : DecksterUiState()
}


sealed class DecksterSearchUiState {
    object Loading : DecksterSearchUiState()
    class Empty(val term: String? = null) : DecksterSearchUiState()
    class Error(throwable: Throwable?) : DecksterSearchUiState()
    data class Content(val games: List<Game> = emptyList(), val term: String? = null) :
        DecksterSearchUiState()
}


sealed class DecksterDetailUiState {
    object Loading : DecksterDetailUiState()
    class Error(val throwable: Throwable?) : DecksterDetailUiState()
    data class Content(val gameData: Pair<Game, GameInfoResult?>) : DecksterDetailUiState()
}

