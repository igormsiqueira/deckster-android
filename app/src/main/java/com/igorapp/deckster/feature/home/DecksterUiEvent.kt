package com.igorapp.deckster.feature.home


sealed interface DecksterUiEvent {
    object OnLoadMore : DecksterUiEvent
    class OnSearch(val term: String) : DecksterUiEvent
    object OnSearchToggle : DecksterUiEvent
    class OnFilterChange(val option: String) : DecksterUiEvent
}
