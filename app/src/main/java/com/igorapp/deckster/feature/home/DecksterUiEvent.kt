package com.igorapp.deckster.feature.home

import com.igorapp.deckster.model.Game
import model.GameInfoResult


sealed interface DecksterUiEvent {
    object OnLoadMore : DecksterUiEvent
    class OnSearch(val term: String) : DecksterUiEvent
    class OnBookmarkToggle(val game: Game) : DecksterUiEvent
    class OnFilterChange(val option: String) : DecksterUiEvent
}



sealed interface DecksterDetailUiEvent {
    class OnDetailsBookmarkToggle(val game: Game) : DecksterDetailUiEvent
    class OnGameDetails(val game: GameInfoResult) : DecksterDetailUiEvent
}
