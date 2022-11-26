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
    fun getBookmarks(): List<Game>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<Game>)

}
