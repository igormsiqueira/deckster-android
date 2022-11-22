package com.igorapp.deckster.network

import kotlinx.coroutines.flow.flow

class Deckster(private val api: DecksterApi) : DecksterApiService {
    override  fun loadGames(page: Int, size: Int) = flow {
        emit(api.loadGamePage(page, size))
    }

    override  fun loadChoiceGames() = flow {
        emit(api.loadChoiceGames())
    }

    override  fun searchByGame(name: String) = flow {
        emit(api.searchByName(name))
    }

}