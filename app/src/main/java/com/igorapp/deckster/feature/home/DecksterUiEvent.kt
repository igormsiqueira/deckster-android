package com.igorapp.deckster.feature.home

import com.igorapp.deckster.ui.home.StatusOptions


sealed interface DecksterUiEvent {
    object OnLoadMore : DecksterUiEvent
    class OnSearch(val term: String) : DecksterUiEvent
    class OnFilterChange(val option: String) : DecksterUiEvent
}
