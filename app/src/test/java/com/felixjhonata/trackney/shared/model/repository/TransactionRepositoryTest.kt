package com.felixjhonata.trackney.shared.model.repository

import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.TransactionWithCategory
import com.felixjhonata.trackney.shared.model.dao.TransactionDao
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.shared.model.entity.Transaction
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

import com.felixjhonata.trackney.shared.model.dao.CategoryDao
import com.felixjhonata.trackney.shared.model.database.TrackneyDatabase

class TransactionRepositoryTest {

    private val transactionDao: TransactionDao = mockk(relaxed = true)
    private val categoryDao: CategoryDao = mockk(relaxed = true)
    private val database: TrackneyDatabase = mockk(relaxed = true)
    private val repository = TransactionRepository(transactionDao, categoryDao, database)

    @Test
    fun insertTransaction() = runTest {
        val transaction = Transaction(id = 1, dateTime = LocalDateTime.now(), amount = 100.0, categoryId = 2, note = "Test")
        coEvery { transactionDao.insertTransaction(transaction) } returns Unit

        repository.insertTransaction(transaction)

        coVerify(exactly = 1) { transactionDao.insertTransaction(transaction) }
    }

    @Test
    fun updateTransaction() = runTest {
        val transaction = Transaction(id = 1, dateTime = LocalDateTime.now(), amount = 150.0, categoryId = 2, note = "Updated Test")
        coEvery { transactionDao.updateTransaction(transaction) } returns Unit

        repository.updateTransaction(transaction)

        coVerify(exactly = 1) { transactionDao.updateTransaction(transaction) }
    }

    @Test
    fun deleteTransaction() = runTest {
        val transaction = Transaction(id = 1, dateTime = LocalDateTime.now(), amount = 100.0, categoryId = 2, note = "Test")
        coEvery { transactionDao.deleteTransaction(transaction) } returns Unit

        repository.deleteTransaction(transaction)

        coVerify(exactly = 1) { transactionDao.deleteTransaction(transaction) }
    }

    @Test
    fun getTransactionWithCategoryById() = runTest {
        val transaction = Transaction(id = 1, dateTime = LocalDateTime.now(), amount = 100.0, categoryId = 2, note = "Test")
        val category = Category(id = 2, name = "Food", type = TransactionType.EXPENSE)
        val expected = TransactionWithCategory(transaction, category)
        coEvery { transactionDao.getTransactionWithCategoryById(1) } returns expected

        val result = repository.getTransactionWithCategoryById(1)

        assertEquals(expected, result)
        coVerify(exactly = 1) { transactionDao.getTransactionWithCategoryById(1) }
    }

    @Test
    fun getTransactionsByDateRange() {
        val transaction = Transaction(id = 1, dateTime = LocalDateTime.now(), amount = 100.0, categoryId = 2, note = "Test")
        val category = Category(id = 2, name = "Food", type = TransactionType.EXPENSE)
        val expectedList = listOf(TransactionWithCategory(transaction, category))
        every { transactionDao.getTransactionsByDateRange(1000L, 2000L) } returns flowOf(expectedList)

        val result = repository.getTransactionsByDateRange(1000L, 2000L)

        runTest {
            result.collect { list ->
                assertEquals(expectedList, list)
            }
        }
        verify(exactly = 1) { transactionDao.getTransactionsByDateRange(1000L, 2000L) }
    }

    @Test
    fun hasTransactionsWithCategoryId() = runTest {
        coEvery { transactionDao.hasTransactionsWithCategoryId(2) } returns true

        val result = repository.hasTransactionsWithCategoryId(2)

        assertTrue(result)
        coVerify(exactly = 1) { transactionDao.hasTransactionsWithCategoryId(2) }
    }
}
