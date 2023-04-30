package com.igorapp.deckster.ui.home

enum class GameStatus(val code: Int) {
    AllGames(3),//old Verified
    Playable(2),
    Unknown(1),
    Backlog(0)
}