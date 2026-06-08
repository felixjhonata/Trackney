package com.felixjhonata.trackney.shared.model.repository

import com.felixjhonata.trackney.shared.model.dao.TransactionDao
import com.felixjhonata.trackney.shared.model.entity.Transaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    suspend fun insertTransaction(
        transaction: Transaction
    ) = transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(
        transaction: Transaction
    ) = transactionDao.updateTransaction(transaction)

    suspend fun deleteTransaction(
        transaction: Transaction
    ) = transactionDao.deleteTransaction(transaction)

    suspend fun getTransactionWithCategoryById(
        id: Int
    ) = transactionDao.getTransactionWithCategoryById(id)

    fun getTransactionsByDateRange(start: Long, end: Long) =
        transactionDao.getTransactionsByDateRange(start, end)

    suspend fun hasTransactionsWithCategoryId(categoryId: Int) =
        transactionDao.hasTransactionsWithCategoryId(categoryId)
}