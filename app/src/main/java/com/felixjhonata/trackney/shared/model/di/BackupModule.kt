package com.felixjhonata.trackney.shared.model.di

import com.felixjhonata.trackney.shared.util.BackupStreamProvider
import com.felixjhonata.trackney.shared.util.BackupStreamResolver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BackupModule {
    @Binds
    @Singleton
    abstract fun bindBackupStreamResolver(
        backupStreamProvider: BackupStreamProvider
    ): BackupStreamResolver
}
