package com.igorapp.deckster.di

import android.content.Context
import com.igorapp.deckster.data.AppDatabase
import com.igorapp.deckster.data.GameDao
import com.igorapp.deckster.network.Deckster
import com.igorapp.deckster.network.DecksterApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun providesApi() = Deckster(DecksterApi.create())

}

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    fun provideDao(@ApplicationContext appContext: Context): GameDao {
        return AppDatabase.getDatabase(appContext).gameDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }

}
