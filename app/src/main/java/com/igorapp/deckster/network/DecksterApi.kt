package com.igorapp.deckster.network

import com.igorapp.deckster.model.Game
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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

    @GET(API.getChoiceGames)
    suspend fun loadChoiceGames(
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


    object API {
        const val getAllGames = "games"
        const val getChoiceGames = "choice"
        const val searchGames = "/search"
        const val baseUrl = "https://api-deckster.herokuapp.com/"
    }
}