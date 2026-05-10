package com.felixjhonata.trackney.shared.model.di

import com.felixjhonata.trackney.shared.model.annotations.IoDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @IoDispatchers
    fun provideIoDispatchers() = Dispatchers.IO
}