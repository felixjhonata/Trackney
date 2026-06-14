package com.felixjhonata.trackney.shared.model.dto

import com.felixjhonata.trackney.shared.model.TransactionType
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.LocalDateTime

class BackupDtoTest {

    @Test
    fun testLocalDateTimeSerializer() {
        val testDateTime = LocalDateTime.of(2026, 6, 14, 12, 0, 5)
        
        // Test descriptor
        assertNotNull(LocalDateTimeSerializer.descriptor)
        assertEquals("LocalDateTime", LocalDateTimeSerializer.descriptor.serialName)

        // Test serialization/deserialization directly
        val jsonString = Json.encodeToString(LocalDateTimeSerializer, testDateTime)
        assertEquals("\"2026-06-14T12:00:05\"", jsonString)

        val deserialized = Json.decodeFromString(LocalDateTimeSerializer, jsonString)
        assertEquals(testDateTime, deserialized)
    }

    @Test
    fun testCategoryBackupDto() {
        val category = CategoryBackupDto(id = 1, name = "Food", type = TransactionType.EXPENSE)
        
        // Properties
        assertEquals(1, category.id)
        assertEquals("Food", category.name)
        assertEquals(TransactionType.EXPENSE, category.type)

        // copy
        val copy = category.copy(name = "Drinks")
        assertEquals(1, copy.id)
        assertEquals("Drinks", copy.name)
        assertEquals(TransactionType.EXPENSE, copy.type)

        // equals & hashCode
        val equalCategory = CategoryBackupDto(id = 1, name = "Food", type = TransactionType.EXPENSE)
        val differentCategory = CategoryBackupDto(id = 2, name = "Food", type = TransactionType.EXPENSE)
        assertEquals(category, equalCategory)
        assertNotEquals(category, differentCategory)
        assertEquals(category.hashCode(), equalCategory.hashCode())

        // toString
        assertEquals("CategoryBackupDto(id=1, name=Food, type=EXPENSE)", category.toString())

        // serialization
        val json = Json.encodeToString(CategoryBackupDto.serializer(), category)
        val decoded = Json.decodeFromString(CategoryBackupDto.serializer(), json)
        assertEquals(category, decoded)
    }

    @Test
    fun testTransactionBackupDto() {
        val testDateTime = LocalDateTime.of(2026, 6, 14, 12, 0, 5)
        val transaction = TransactionBackupDto(
            id = 10,
            dateTime = testDateTime,
            amount = 15000.0,
            categoryId = 1,
            note = "Lunch"
        )

        // Properties
        assertEquals(10, transaction.id)
        assertEquals(testDateTime, transaction.dateTime)
        assertEquals(15000.0, transaction.amount, 0.0)
        assertEquals(1, transaction.categoryId)
        assertEquals("Lunch", transaction.note)

        // copy
        val copy = transaction.copy(amount = 20000.0)
        assertEquals(10, copy.id)
        assertEquals(testDateTime, copy.dateTime)
        assertEquals(20000.0, copy.amount, 0.0)
        assertEquals(1, copy.categoryId)
        assertEquals("Lunch", copy.note)

        // equals & hashCode
        val equalTransaction = TransactionBackupDto(
            id = 10,
            dateTime = testDateTime,
            amount = 15000.0,
            categoryId = 1,
            note = "Lunch"
        )
        val differentTransaction = TransactionBackupDto(
            id = 11,
            dateTime = testDateTime,
            amount = 15000.0,
            categoryId = 1,
            note = "Lunch"
        )
        assertEquals(transaction, equalTransaction)
        assertNotEquals(transaction, differentTransaction)
        assertEquals(transaction.hashCode(), equalTransaction.hashCode())

        // toString
        assertEquals(
            "TransactionBackupDto(id=10, dateTime=2026-06-14T12:00:05, amount=15000.0, categoryId=1, note=Lunch)",
            transaction.toString()
        )

        // serialization
        val json = Json.encodeToString(TransactionBackupDto.serializer(), transaction)
        val decoded = Json.decodeFromString(TransactionBackupDto.serializer(), json)
        assertEquals(transaction, decoded)
    }

    @Test
    fun testBackupDataDto() {
        val testDateTime = LocalDateTime.of(2026, 6, 14, 12, 0, 5)
        val categories = listOf(CategoryBackupDto(id = 1, name = "Food", type = TransactionType.EXPENSE))
        val transactions = listOf(
            TransactionBackupDto(
                id = 10,
                dateTime = testDateTime,
                amount = 15000.0,
                categoryId = 1,
                note = "Lunch"
            )
        )
        val backupData = BackupDataDto(categories = categories, transactions = transactions)

        // Properties
        assertEquals(categories, backupData.categories)
        assertEquals(transactions, backupData.transactions)

        // copy
        val copy = backupData.copy(categories = emptyList())
        assertEquals(emptyList<CategoryBackupDto>(), copy.categories)
        assertEquals(transactions, copy.transactions)

        // equals & hashCode
        val equalBackup = BackupDataDto(categories = categories, transactions = transactions)
        val differentBackup = BackupDataDto(categories = emptyList(), transactions = transactions)
        assertEquals(backupData, equalBackup)
        assertNotEquals(backupData, differentBackup)
        assertEquals(backupData.hashCode(), equalBackup.hashCode())

        // toString
        assertEquals(
            "BackupDataDto(categories=$categories, transactions=$transactions)",
            backupData.toString()
        )

        // serialization
        val json = Json.encodeToString(BackupDataDto.serializer(), backupData)
        val decoded = Json.decodeFromString(BackupDataDto.serializer(), json)
        assertEquals(backupData, decoded)
    }
}
