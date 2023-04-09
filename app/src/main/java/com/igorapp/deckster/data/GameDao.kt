package com.igorapp.deckster.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.igorapp.deckster.model.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM game")
    fun getAll(): Flow<List<Game>>

    @Query("SELECT * FROM game where isBookmarked = 1")
    fun getBookmarks(): Flow<List<Game>>

    @Query("SELECT * FROM game where spotlight = 1")
    fun getSpotlightGames(): Flow<List<Game>>

    @Query("SELECT * FROM game where status =:filter order by game asc")
    fun getGamesByFilter(filter: Int): Flow<List<Game>>

    @Query("SELECT * FROM game  where id=:id")
    fun getGameById(id: String): Flow<Game>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(games: List<Game>)

    @Query("UPDATE game set isBookmarked=:saved where id=:id")
    suspend fun updateGame(id: String, saved: Boolean)

    @Query("SELECT COUNT(*) FROM game ")
    suspend fun getGamesCount(): Int


}

