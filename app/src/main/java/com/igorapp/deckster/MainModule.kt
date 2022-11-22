package com.igorapp.deckster

import android.content.Context
import com.igorapp.deckster.model.GameDao
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
