package com.igorapp.deckster.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.igorapp.deckster.model.Game
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var gameDao: GameDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        gameDao = database.gameDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveGames() = runBlocking {
        val games = listOf(
            Game("1", "Game One", "Playable", "Keyboard", "10h"),
            Game("2", "Game Two", "Playable", "Keyboard", "20h")
        )
        gameDao.insertAll(games)

        val fromDb = gameDao.getAll().first()
        assertEquals(games.size, fromDb.size)
        assertEquals(games.size, gameDao.getGamesCount())
    }
}
