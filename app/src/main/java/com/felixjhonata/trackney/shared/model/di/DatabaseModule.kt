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

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
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
                    Triple(1, "Entertainment", "EXPENSE"),
                    Triple(2, "Food", "EXPENSE"),
                    Triple(3, "Grocery", "EXPENSE"),
                    Triple(4, "Salary", "INCOME"),
                    Triple(5, "Utility", "EXPENSE"),
                    Triple(6, "Others", "EXPENSE"),
                    Triple(7, "Others", "INCOME")
                )

                categories.forEach { (id, name, type) ->
                    val values = ContentValues().apply {
                        put("id", id)
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
    fun provideCategoryDao(
        database: TrackneyDatabase
    ) = database.categoryDao()

    @Provides
    fun provideTransactionDao(
        database: TrackneyDatabase
    ) = database.transactionDao()
}
