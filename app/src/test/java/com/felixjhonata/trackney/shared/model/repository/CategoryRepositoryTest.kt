package com.felixjhonata.trackney.shared.model.repository

import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.dao.CategoryDao
import com.felixjhonata.trackney.shared.model.entity.Category
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

class CategoryRepositoryTest {

    private val categoryDao: CategoryDao = mockk(relaxed = true)
    private val repository = CategoryRepository(categoryDao)

    @Test
    fun getCategories() {
        val categoriesList = listOf(Category(id = 1, name = "Food", type = TransactionType.EXPENSE))
        every { categoryDao.getAll() } returns flowOf(categoriesList)

        val result = repository.getCategories()
        
        // Assertions can be simplified using standard flow tools
        runTest {
            result.collect { list ->
                assertEquals(categoriesList, list)
            }
        }
        verify(exactly = 1) { categoryDao.getAll() }
    }

    @Test
    fun existsByName() = runTest {
        coEvery { categoryDao.existsByName("Food", TransactionType.EXPENSE) } returns true

        val result = repository.existsByName("Food", TransactionType.EXPENSE)

        assertTrue(result)
        coVerify(exactly = 1) { categoryDao.existsByName("Food", TransactionType.EXPENSE) }
    }

    @Test
    fun insertCategory() = runTest {
        val category = Category(name = "Food", type = TransactionType.EXPENSE)
        coEvery { categoryDao.insertCategory(category) } returns longArrayOf(1L)

        repository.insertCategory(category)

        coVerify(exactly = 1) { categoryDao.insertCategory(category) }
    }

    @Test
    fun deleteCategory() = runTest {
        val category = Category(id = 1, name = "Food", type = TransactionType.EXPENSE)
        coEvery { categoryDao.deleteCategory(category) } returns Unit

        repository.deleteCategory(category)

        coVerify(exactly = 1) { categoryDao.deleteCategory(category) }
    }

    @Test
    fun updateCategory() = runTest {
        val category = Category(id = 1, name = "Food & Drink", type = TransactionType.EXPENSE)
        coEvery { categoryDao.updateCategory(category) } returns Unit

        repository.updateCategory(category)

        coVerify(exactly = 1) { categoryDao.updateCategory(category) }
    }

    @Test
    fun getAllCategoriesList() = runTest {
        val categoriesList = listOf(Category(id = 1, name = "Food", type = TransactionType.EXPENSE))
        coEvery { categoryDao.getAllList() } returns categoriesList

        val result = repository.getAllCategoriesList()

        assertEquals(categoriesList, result)
        coVerify(exactly = 1) { categoryDao.getAllList() }
    }
}
