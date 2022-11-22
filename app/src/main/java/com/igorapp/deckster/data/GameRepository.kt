package com.igorapp.deckster.data

import com.igorapp.deckster.model.Game
import javax.inject.Inject

class GameRepository @Inject constructor(private val gameDao: GameDao) {
    suspend fun addGames(games: List<Game>) = gameDao.insertAll(games)
    fun getGames() = gameDao.getAll()
}