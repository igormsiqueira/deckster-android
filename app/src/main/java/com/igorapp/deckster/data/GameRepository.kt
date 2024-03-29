package com.igorapp.deckster.data

import com.igorapp.deckster.model.Game
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GameRepository @Inject constructor(private val gameDao: GameDao) {
    suspend fun addGames(games: List<Game>) = gameDao.insertAll(games)
    suspend fun updateGame(id: String, saved: Boolean) = gameDao.updateGame(id, saved)
    fun getBacklogGames(): Flow<List<Game>> = gameDao.getBookmarks()
    fun searchGameById(id: String): Flow<Game> = gameDao.getGameById(id)
    fun getGamesByFilter(filter: Int) = gameDao.getGamesByFilter(filter)
    fun getSpotlightGames() = gameDao.getSpotlightGames()
    fun getGames() = gameDao.getAll()
    suspend fun getGamesCount() = gameDao.getGamesCount()
}