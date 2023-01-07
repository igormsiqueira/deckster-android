package com.igorapp.deckster.network

import com.igorapp.deckster.model.Game
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DecksterApi {

    @GET(API.getAllGames)
    suspend fun loadGamePage(
        @Query("page") page: Int, @Query("size") size: Int, @Query("filter") filter: String
    ): List<Game>

    @GET(API.getChoiceGames)
    suspend fun loadChoiceGames(
    ): List<Game>

    @GET(API.searchGames)
    suspend fun searchByName(@Query("q") name: String): List<Game>

    @GET(API.searchGamesById)
    suspend fun searchById(@Query("q") name: String): Game

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


    object API {
        const val getAllGames = "games"
        const val getChoiceGames = "choice"
        const val searchGames = "search"
        const val searchGamesById = "search/id"
        const val baseUrl = "https://api-deckster-v1.lm.r.appspot.com/"
    }
}

// const val baseUrl = "http://192.168.1.25:8082/"
// const val baseUrl = "https://api-deckster.herokuapp.com/"
