package com.ghostbusters.companion.di

import android.content.Context
import androidx.room.Room
import com.ghostbusters.companion.data.database.AppDatabase
import com.ghostbusters.companion.data.database.dao.CharacterDao
import com.ghostbusters.companion.data.database.dao.GameInstanceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ghostbusters_companion_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideGameInstanceDao(database: AppDatabase): GameInstanceDao {
        return database.gameInstanceDao()
    }

    @Provides
    fun provideCharacterDao(database: AppDatabase): CharacterDao {
        return database.characterDao()
    }
}

