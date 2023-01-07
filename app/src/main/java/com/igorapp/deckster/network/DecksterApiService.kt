package com.igorapp.deckster.network

import com.igorapp.deckster.model.Game
import com.igorapp.deckster.ui.home.GameStatus
import kotlinx.coroutines.flow.Flow

interface DecksterApiService {
    fun loadGames(page: Int, size: Int, gameStatus: GameStatus): Flow<List<Game>>
    fun searchByGame(name: String): Flow<List<Game>>
    fun searchById(name: String): Flow<Game>
    fun loadChoiceGames(): Flow<List<Game>>
}
