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
            val existingCategories = categoryDao.getAllList()
            val existingMap = existingCategories.associateBy { it.name to it.type }

            val categoryIdMap = mutableMapOf<Int, Int>()
            val newCategoriesToInsert = mutableListOf<Category>()
            val oldIdsToInsertIndex = mutableListOf<Int>()

            for (category in backupCategories) {
                val existing = existingMap[category.name to category.type]
                if (existing != null) {
                    categoryIdMap[category.id] = existing.id
                } else {
                    newCategoriesToInsert.add(category.copy(id = 0))
                    oldIdsToInsertIndex.add(category.id)
                }
            }

            if (newCategoriesToInsert.isNotEmpty()) {
                val insertedIds = categoryDao.insertCategory(*newCategoriesToInsert.toTypedArray())
                for (i in newCategoriesToInsert.indices) {
                    val oldId = oldIdsToInsertIndex[i]
                    val newId = insertedIds[i].toInt()
                    categoryIdMap[oldId] = newId
                }
            }

            val transactionsToInsert = mutableListOf<Transaction>()
            for (transaction in backupTransactions) {
                val newCategoryId = categoryIdMap[transaction.categoryId] ?: continue
                transactionsToInsert.add(
                    transaction.copy(
                        id = 0, // Auto-generate new transaction ID to prevent collisions
                        categoryId = newCategoryId
                    )
                )
            }

            if (transactionsToInsert.isNotEmpty()) {
                transactionDao.insertTransaction(*transactionsToInsert.toTypedArray())
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