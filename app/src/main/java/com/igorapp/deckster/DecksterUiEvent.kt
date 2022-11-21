package com.igorapp.deckster


sealed interface DecksterUiEvent {
    object OnLoadMore : DecksterUiEvent
}
