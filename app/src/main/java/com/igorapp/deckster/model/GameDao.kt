package com.igorapp.deckster.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@Dao
interface GameDao {

    @Query("SELECT * FROM game")
     fun getAll(): Flow<List<Game>>

    @Query("SELECT * FROM game where isBookmarked = 1")
    fun getBookmarks(): List<Game>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<Game>)

}

class GameRepository @Inject constructor(private val gameDao: GameDao) {

    suspend fun addGames(games: List<Game>) {
        gameDao.insertAll(games)
    }

     fun getGames() =  gameDao.getAll()
}
