package com.felixjhonata.trackney.shared.model.di

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.felixjhonata.trackney.shared.model.database.TrackneyDatabase
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
    fun provideDatabase(
        @ApplicationContext applicationContext: Context
    ) = Room.databaseBuilder(
        applicationContext,
        TrackneyDatabase::class.java,
        "trackney-database"
    ).addCallback(
        object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                val categories = listOf(
                    Pair("Entertainment", "EXPENSE"),
                    Pair("Food", "EXPENSE"),
                    Pair("Grocery", "EXPENSE"),
                    Pair("Salary", "INCOME"),
                    Pair("Transport", "EXPENSE"),
                    Pair("Utility", "EXPENSE"),
                    Pair("Others", "EXPENSE"),
                    Pair("Others", "INCOME")
                )

                categories.forEach { (name, type) ->
                    val values = ContentValues().apply {
                        put("name", name)
                        put("type", type)
                    }
                    db.insert(
                        "categories",
                        SQLiteDatabase.CONFLICT_IGNORE,
                        values
                    )
                }
            }
        }
    ).build()

    @Provides
    @Singleton
    fun provideCategoryDao(
        database: TrackneyDatabase
    ) = database.categoryDao()

    @Provides
    @Singleton
    fun provideTransactionDao(
        database: TrackneyDatabase
    ) = database.transactionDao()
}
