package com.felixjhonata.trackney.shared.domain

import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.shared.model.entity.Transaction
import com.felixjhonata.trackney.shared.model.repository.CategoryRepository
import com.felixjhonata.trackney.shared.model.repository.TransactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

import kotlinx.coroutines.Dispatchers

class BackupUseCaseTest {

    private val categoryRepository: CategoryRepository = mockk()
    private val transactionRepository: TransactionRepository = mockk()

    private val exportBackupUseCase = ExportBackupUseCase(categoryRepository, transactionRepository, Dispatchers.IO)
    private val importBackupUseCase = ImportBackupUseCase(transactionRepository, Dispatchers.IO)

    @Test
    fun exportBackupUseCase_serializesDataCorrectly() = runTest {
        val testDateTime = LocalDateTime.of(2026, 6, 14, 12, 0, 5)
        val categories = listOf(
            Category(id = 1, name = "Food", type = TransactionType.EXPENSE)
        )
        val transactions = listOf(
            Transaction(id = 10, dateTime = testDateTime, amount = 15000.0, categoryId = 1, note = "Lunch")
        )

        coEvery { categoryRepository.getAllCategoriesList() } returns categories
        coEvery { transactionRepository.getAllTransactions() } returns transactions

        val outputStream = ByteArrayOutputStream()
        exportBackupUseCase(outputStream)

        val jsonResult = outputStream.toString("UTF-8")

        // Assert that the JSON contains the core elements
        assertTrue(jsonResult.contains("\"name\":\"Food\""))
        assertTrue(jsonResult.contains("\"type\":\"EXPENSE\""))
        assertTrue(jsonResult.contains("\"amount\":15000.0"))
        assertTrue(jsonResult.contains("\"dateTime\":\"2026-06-14T12:00:05\""))
        assertTrue(jsonResult.contains("\"note\":\"Lunch\""))
    }

    @Test
    fun importBackupUseCase_deserializesAndRestoresCorrectly() = runTest {
        val jsonInput = """
            {
                "categories": [
                    { "id": 1, "name": "Salary", "type": "INCOME" }
                ],
                "transactions": [
                    { "id": 100, "dateTime": "2026-06-14T12:00:05", "amount": 5000000.0, "categoryId": 1, "note": "Monthly Pay" }
                ]
            }
        """.trimIndent()

        val inputStream = ByteArrayInputStream(jsonInput.toByteArray(Charsets.UTF_8))

        coEvery { transactionRepository.restoreBackup(any(), any()) } returns Unit

        importBackupUseCase(inputStream)

        coVerify(exactly = 1) {
            transactionRepository.restoreBackup(
                match { categories ->
                    categories.size == 1 &&
                            categories[0].id == 1 &&
                            categories[0].name == "Salary" &&
                            categories[0].type == TransactionType.INCOME
                },
                match { transactions ->
                    transactions.size == 1 &&
                            transactions[0].id == 100 &&
                            transactions[0].amount == 5000000.0 &&
                            transactions[0].categoryId == 1 &&
                            transactions[0].note == "Monthly Pay" &&
                            transactions[0].dateTime == LocalDateTime.of(2026, 6, 14, 12, 0, 5)
                }
            )
        }
    }
}
