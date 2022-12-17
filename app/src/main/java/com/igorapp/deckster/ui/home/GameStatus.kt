package com.igorapp.deckster.ui.home

enum class GameStatus(val code: Int) {
    Verified(3),
    Playable(2),
    /*Unplayable(1),*/
    Backlog(0)
}