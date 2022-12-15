package com.igorapp.deckster.data

import com.igorapp.deckster.model.Game
import javax.inject.Inject

class GameRepository @Inject constructor(private val gameDao: GameDao) {
    suspend fun addGames(games: List<Game>) = gameDao.insertAll(games)
    suspend fun updateGame(game: Game) = gameDao.updateGame(game)
    fun getGames() = gameDao.getAll()
    fun getBacklogGames() = gameDao.getBookmarks()
    fun getGamesByFilter(filter: Int) = gameDao.getGamesByFilter(filter)
}