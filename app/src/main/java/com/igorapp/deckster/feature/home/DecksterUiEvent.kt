package com.igorapp.deckster.feature.home


sealed interface DecksterUiEvent {
    object OnLoadMore : DecksterUiEvent
    class OnSearch(val term: String) : DecksterUiEvent
    class OnSearchToggle(val searchIsVisible: Boolean) : DecksterUiEvent
    class OnFilterChange(val option: String) : DecksterUiEvent
}
