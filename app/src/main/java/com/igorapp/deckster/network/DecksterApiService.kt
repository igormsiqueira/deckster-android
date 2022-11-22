package com.igorapp.deckster.network

import com.igorapp.deckster.model.Game
import kotlinx.coroutines.flow.Flow

interface DecksterApiService {
     fun loadGames(page: Int, size: Int): Flow<List<Game>>
     fun searchByGame(name: String): Flow<List<Game>>
    fun loadChoiceGames(): Flow<List<Game>>
}
