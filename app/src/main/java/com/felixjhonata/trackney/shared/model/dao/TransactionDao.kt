package com.felixjhonata.trackney.shared.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.felixjhonata.trackney.shared.model.TransactionWithCategory
import com.felixjhonata.trackney.shared.model.entity.Transaction as TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionWithCategoryById(id: Int): TransactionWithCategory?

    @Transaction
    @Query("SELECT * FROM transactions WHERE dateTime >= :start AND dateTime <= :end ORDER BY dateTime DESC")
    fun getTransactionsByDateRange(start: Long, end: Long): Flow<List<TransactionWithCategory>>

    @Query("SELECT EXISTS(SELECT 1 FROM transactions WHERE category_id = :categoryId)")
    suspend fun hasTransactionsWithCategoryId(categoryId: Int): Boolean
}