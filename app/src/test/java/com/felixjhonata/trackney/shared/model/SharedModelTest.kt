package com.felixjhonata.trackney.shared.model

import com.felixjhonata.trackney.add_edit_transaction.model.ModifyTransactionType
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.shared.model.entity.Transaction
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.LocalDateTime

class SharedModelTest {

    @Test
    fun testModifyTransactionType() {
        assertEquals(ModifyTransactionType.ADD, ModifyTransactionType.valueOf("ADD"))
        assertEquals(ModifyTransactionType.EDIT, ModifyTransactionType.valueOf("EDIT"))
        assertEquals(2, ModifyTransactionType.entries.size)
    }

    @Test
    fun testNavDestKeys() {
        // Test Home object
        val homeDest = Home
        assertNotNull(homeDest)
        val homeJson = Json.encodeToString(Home.serializer(), Home)
        val homeDecoded = Json.decodeFromString(Home.serializer(), homeJson)
        assertEquals(Home, homeDecoded)

        // Test AddTransaction object
        val addDest = AddTransaction
        assertNotNull(addDest)
        val addJson = Json.encodeToString(AddTransaction.serializer(), AddTransaction)
        val addDecoded = Json.decodeFromString(AddTransaction.serializer(), addJson)
        assertEquals(AddTransaction, addDecoded)

        // Test EditTransaction class
        val editDest = EditTransaction(101)
        assertEquals(101, editDest.transactionId)
        val editCopy = editDest.copy(transactionId = 202)
        assertEquals(202, editCopy.transactionId)
        assertEquals(editDest, EditTransaction(101))
        assertEquals("EditTransaction(transactionId=101)", editDest.toString())

        val editJson = Json.encodeToString(EditTransaction.serializer(), editDest)
        val editDecoded = Json.decodeFromString(EditTransaction.serializer(), editJson)
        assertEquals(101, editDecoded.transactionId)

        // Test ManageCategory class
        val manageDest = ManageCategory(TransactionType.EXPENSE)
        assertEquals(TransactionType.EXPENSE, manageDest.type)
        val manageCopy = manageDest.copy(type = TransactionType.INCOME)
        assertEquals(TransactionType.INCOME, manageCopy.type)
        assertEquals(manageDest, ManageCategory(TransactionType.EXPENSE))
        assertEquals("ManageCategory(type=EXPENSE)", manageDest.toString())

        val manageJson = Json.encodeToString(ManageCategory.serializer(), manageDest)
        val manageDecoded = Json.decodeFromString(ManageCategory.serializer(), manageJson)
        assertEquals(TransactionType.EXPENSE, manageDecoded.type)
    }

    @Test
    fun testTransactionTypeEnum() {
        assertEquals("Expense", TransactionType.EXPENSE.displayName)
        assertEquals("Income", TransactionType.INCOME.displayName)
        assertEquals(TransactionType.EXPENSE, TransactionType.valueOf("EXPENSE"))
        assertEquals(2, TransactionType.entries.size)

        val jsonVal = Json.encodeToString(TransactionType.serializer(), TransactionType.EXPENSE)
        val decodedVal = Json.decodeFromString(TransactionType.serializer(), jsonVal)
        assertEquals(TransactionType.EXPENSE, decodedVal)
    }

    @Test
    fun testCategoryEntity() {
        val category = Category(id = 1, name = "Food", type = TransactionType.EXPENSE)
        assertEquals(1, category.id)
        assertEquals("Food", category.name)
        assertEquals(TransactionType.EXPENSE, category.type)

        val categoryCopy = category.copy(name = "Drinks")
        assertEquals(1, categoryCopy.id)
        assertEquals("Drinks", categoryCopy.name)
        assertEquals(TransactionType.EXPENSE, categoryCopy.type)
        assertEquals(category, Category(id = 1, name = "Food", type = TransactionType.EXPENSE))
        assertEquals("Category(id=1, name=Food, type=EXPENSE)", category.toString())
        assertEquals(category.hashCode(), Category(id = 1, name = "Food", type = TransactionType.EXPENSE).hashCode())
    }

    @Test
    fun testTransactionEntity() {
        val now = LocalDateTime.now()
        val transaction = Transaction(id = 2, dateTime = now, amount = 1500.0, categoryId = 1, note = "Lunch")
        assertEquals(2, transaction.id)
        assertEquals(now, transaction.dateTime)
        assertEquals(1500.0, transaction.amount, 0.0)
        assertEquals(1, transaction.categoryId)
        assertEquals("Lunch", transaction.note)

        val transactionCopy = transaction.copy(amount = 2000.0)
        assertEquals(2, transactionCopy.id)
        assertEquals(2000.0, transactionCopy.amount, 0.0)
        assertEquals(transaction, Transaction(id = 2, dateTime = now, amount = 1500.0, categoryId = 1, note = "Lunch"))
        assertEquals("Transaction(id=2, dateTime=$now, amount=1500.0, categoryId=1, note=Lunch)", transaction.toString())
        assertEquals(transaction.hashCode(), Transaction(id = 2, dateTime = now, amount = 1500.0, categoryId = 1, note = "Lunch").hashCode())
    }

    @Test
    fun testTransactionWithCategory() {
        val now = LocalDateTime.now()
        val transaction = Transaction(id = 2, dateTime = now, amount = 1500.0, categoryId = 1, note = "Lunch")
        val category = Category(id = 1, name = "Food", type = TransactionType.EXPENSE)

        val twc = TransactionWithCategory(transaction = transaction, category = category)
        assertEquals(transaction, twc.transaction)
        assertEquals(category, twc.category)

        val twcCopy = twc.copy(category = category.copy(name = "Dinner"))
        assertEquals("Dinner", twcCopy.category.name)
        assertEquals(twc, TransactionWithCategory(transaction = transaction, category = category))
        assertEquals("TransactionWithCategory(transaction=$transaction, category=$category)", twc.toString())
        assertEquals(twc.hashCode(), TransactionWithCategory(transaction = transaction, category = category).hashCode())
    }
}
