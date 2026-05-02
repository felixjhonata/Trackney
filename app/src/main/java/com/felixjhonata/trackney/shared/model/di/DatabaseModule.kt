package com.felixjhonata.trackney.shared.model.di

import android.content.Context
import androidx.room.Room
import com.felixjhonata.trackney.shared.model.database.TrackneyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideDatabase(
        @ApplicationContext applicationContext: Context
    ) = Room.databaseBuilder(
        applicationContext,
        TrackneyDatabase::class.java, "trackney-database"
    ).build()
}