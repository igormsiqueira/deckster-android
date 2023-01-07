package com.igorapp.deckster.network

import com.igorapp.deckster.ui.home.GameStatus
import kotlinx.coroutines.flow.flow

class Deckster(private val api: DecksterApi) : DecksterApiService {
    override fun loadGames(page: Int, size: Int, gameStatus: GameStatus) = flow {
        emit(api.loadGamePage(page, size,gameStatus.code.toString()))
    }

    override fun loadChoiceGames() = flow {
        emit(api.loadChoiceGames())
    }

    override fun searchByGame(name: String) = flow {
        emit(api.searchByName(name))
    }

    override fun searchById(name: String) = flow {
        emit(api.searchById(name))
    }

}