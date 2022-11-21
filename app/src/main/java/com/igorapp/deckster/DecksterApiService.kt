package com.igorapp.deckster

import com.igorapp.deckster.model.Game
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DecksterApi {

    @GET(API.getAllGames)
    suspend fun loadGamePage(
        @Query("page") page: Int, @Query("size") size: Int
    ): List<Game>

    @GET(API.searchGames)
    suspend fun searchByName(@Path("q") name: String): List<Game>

    companion object {
        fun create(): DecksterApi {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .baseUrl(API.baseUrl)
                .build()
            return retrofit.create(DecksterApi::class.java)
        }
    }
}

interface DecksterApiService {
     fun loadGames(page: Int, size: Int): Flow<List<Game>>
     fun searchByGame(name: String): Flow<List<Game>>
}

class Deckster(private val api: DecksterApi) : DecksterApiService {
    override  fun loadGames(page: Int, size: Int) = flow {
        emit(api.loadGamePage(page, size))
    }

    override  fun searchByGame(name: String) = flow {
        emit(api.searchByName(name))
    }

}

object API {
    //    https://api-deckster.herokuapp.com/games?page=0&size=30
    const val getAllGames = "games"
    const val searchGames = "/search"
    const val baseUrl = "https://api-deckster.herokuapp.com/"
}