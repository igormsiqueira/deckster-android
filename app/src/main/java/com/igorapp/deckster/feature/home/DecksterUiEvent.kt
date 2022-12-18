package com.igorapp.deckster.feature.home

import com.igorapp.deckster.model.Game


sealed interface DecksterUiEvent {
    object OnLoadMore : DecksterUiEvent
    class OnSearch(val term: String) : DecksterUiEvent
    class OnBookmarkToggle(val game: Game) : DecksterUiEvent
    class OnFilterChange(val option: String) : DecksterUiEvent
}
