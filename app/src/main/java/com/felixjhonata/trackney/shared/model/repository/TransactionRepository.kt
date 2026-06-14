package com.felixjhonata.trackney.shared.model.repository

import com.felixjhonata.trackney.shared.model.dao.TransactionDao
import com.felixjhonata.trackney.shared.model.dao.CategoryDao
import com.felixjhonata.trackney.shared.model.database.TrackneyDatabase
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.shared.model.entity.Transaction
import javax.inject.Inject
import javax.inject.Singleton
import androidx.room.withTransaction

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val database: TrackneyDatabase
) {
    suspend fun getAllTransactions(): List<Transaction> = transactionDao.getAllTransactions()

    suspend fun restoreBackup(
        backupCategories: List<Category>,
        backupTransactions: List<Transaction>
    ) {
        database.withTransaction {
            val categoryIdMap = mutableMapOf<Int, Int>()

            // 1. Map and insert categories
            for (category in backupCategories) {
                val existingCategory = categoryDao.getByNameAndType(category.name, category.type)
                if (existingCategory != null) {
                    categoryIdMap[category.id] = existingCategory.id
                } else {
                    // Category doesn't exist, insert it. Since categoryDao.insertCategory returns Unit, 
                    // we need to insert and then query the generated ID, or modify Dao to return Long.
                    // Let's modify CategoryDao to return Long for insert, which is standard in Room.
                    // Alternatively, we can insert and query again. Let's insert and query again to avoid modifying other classes unnecessarily, 
                    // or let's inspect if categoryDao returns Long. It currently returns Unit. Let's insert and query.
                    categoryDao.insertCategory(category.copy(id = 0))
                    val newCategory = categoryDao.getByNameAndType(category.name, category.type)
                    if (newCategory != null) {
                        categoryIdMap[category.id] = newCategory.id
                    }
                }
            }

            // 2. Map and insert transactions
            for (transaction in backupTransactions) {
                val newCategoryId = categoryIdMap[transaction.categoryId] ?: continue
                val mappedTransaction = transaction.copy(
                    id = 0, // Auto-generate new transaction ID to prevent collisions
                    categoryId = newCategoryId
                )
                transactionDao.insertTransaction(mappedTransaction)
            }
        }
    }

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