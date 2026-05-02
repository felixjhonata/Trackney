package com.felixjhonata.trackney.shared.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.felixjhonata.trackney.shared.model.dao.TransactionDao
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.shared.model.entity.Transaction

@Database(entities = [Transaction::class, Category::class], version = 1)
abstract class TrackneyDatabase: RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}