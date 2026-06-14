package com.felixjhonata.trackney.manage_category.viewmodel

import com.felixjhonata.trackney.manage_category.model.ManageCategoryDialog
import com.felixjhonata.trackney.manage_category.model.ManageCategoryUiEvent
import com.felixjhonata.trackney.manage_category.model.ManageCategoryUserEvent
import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.shared.model.repository.CategoryRepository
import com.felixjhonata.trackney.shared.model.repository.TransactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ManageCategoryViewModelTest {

    private val categoryRepository: CategoryRepository = mockk(relaxed = true)
    private val transactionRepository: TransactionRepository = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSetTransactionTypeAndObserveCategories() = runTest(testDispatcher) {
        val categoriesList = listOf(
            Category(id = 1, name = "Food", type = TransactionType.EXPENSE),
            Category(id = 2, name = "Salary", type = TransactionType.INCOME)
        )
        every { categoryRepository.getCategories() } returns flowOf(categoriesList)

        val viewModel = ManageCategoryViewModel(categoryRepository, transactionRepository)

        // Initial observe categories
        viewModel.setTransactionType(TransactionType.EXPENSE)
        assertEquals(TransactionType.EXPENSE, viewModel.uiState.value.type)
        assertEquals(1, viewModel.uiState.value.categories.size)
        assertEquals("Food", viewModel.uiState.value.categories.first().name)

        // Change transaction type
        viewModel.setTransactionType(TransactionType.INCOME)
        assertEquals(TransactionType.INCOME, viewModel.uiState.value.type)
        assertEquals(1, viewModel.uiState.value.categories.size)
        assertEquals("Salary", viewModel.uiState.value.categories.first().name)
    }

    @Test
    fun testDialogToggles() = runTest(testDispatcher) {
        val viewModel = ManageCategoryViewModel(categoryRepository, transactionRepository)
        val category = Category(id = 1, name = "Food", type = TransactionType.EXPENSE)

        assertEquals(ManageCategoryDialog.None, viewModel.uiState.value.dialog)

        // Show Add Category
        viewModel.onUserEvent(ManageCategoryUserEvent.ShowAddCategoryDialog)
        assertEquals(ManageCategoryDialog.AddCategory, viewModel.uiState.value.dialog)

        // Hide Dialog
        viewModel.onUserEvent(ManageCategoryUserEvent.HideDialog)
        assertEquals(ManageCategoryDialog.None, viewModel.uiState.value.dialog)

        // Show Edit Category
        viewModel.onUserEvent(ManageCategoryUserEvent.ShowEditCategoryDialog(category))
        assertEquals(ManageCategoryDialog.EditCategory(category), viewModel.uiState.value.dialog)

        // Request Delete
        viewModel.onUserEvent(ManageCategoryUserEvent.RequestDelete(category))
        assertEquals(ManageCategoryDialog.ConfirmDelete(category), viewModel.uiState.value.dialog)
    }

    @Test
    fun testAddCategoryValidations() = runTest(testDispatcher) {
        val viewModel = ManageCategoryViewModel(categoryRepository, transactionRepository)
        viewModel.setTransactionType(TransactionType.EXPENSE)

        // 1. Blank category name
        viewModel.onUserEvent(ManageCategoryUserEvent.AddCategory("  "))
        assertEquals("Name cannot be empty", viewModel.uiState.value.nameErrorMessage)

        // 2. Name already exists
        coEvery { categoryRepository.existsByName("Food", TransactionType.EXPENSE) } returns true
        viewModel.onUserEvent(ManageCategoryUserEvent.AddCategory("Food"))
        assertEquals("Category name already exists", viewModel.uiState.value.nameErrorMessage)

        // 3. Successful insert
        coEvery { categoryRepository.existsByName("Snacks", TransactionType.EXPENSE) } returns false
        coEvery { categoryRepository.insertCategory(any()) } returns longArrayOf(1L)

        viewModel.onUserEvent(ManageCategoryUserEvent.ShowAddCategoryDialog)
        viewModel.onUserEvent(ManageCategoryUserEvent.AddCategory(" Snacks "))

        coVerify { categoryRepository.insertCategory(Category(name = "Snacks", type = TransactionType.EXPENSE)) }
        assertEquals(ManageCategoryDialog.None, viewModel.uiState.value.dialog)
        assertNull(viewModel.uiState.value.nameErrorMessage)
    }

    @Test
    fun testEditCategoryValidations() = runTest(testDispatcher) {
        val viewModel = ManageCategoryViewModel(categoryRepository, transactionRepository)
        val oldCategory = Category(id = 1, name = "Food", type = TransactionType.EXPENSE)

        // 1. Blank name
        viewModel.onUserEvent(ManageCategoryUserEvent.EditCategory("  ", oldCategory))
        assertEquals("Name cannot be empty", viewModel.uiState.value.nameErrorMessage)

        // 2. No changes in name
        viewModel.onUserEvent(ManageCategoryUserEvent.ShowEditCategoryDialog(oldCategory))
        viewModel.onUserEvent(ManageCategoryUserEvent.EditCategory("Food", oldCategory))
        assertEquals(ManageCategoryDialog.None, viewModel.uiState.value.dialog)
        assertNull(viewModel.uiState.value.nameErrorMessage)

        // 3. Name already exists
        coEvery { categoryRepository.existsByName("Drinks", TransactionType.EXPENSE) } returns true
        viewModel.onUserEvent(ManageCategoryUserEvent.ShowEditCategoryDialog(oldCategory))
        viewModel.onUserEvent(ManageCategoryUserEvent.EditCategory("Drinks", oldCategory))
        assertEquals("Category name already exists", viewModel.uiState.value.nameErrorMessage)

        // 4. Successful update
        coEvery { categoryRepository.existsByName("Drinks", TransactionType.EXPENSE) } returns false
        coEvery { categoryRepository.updateCategory(any()) } returns Unit

        viewModel.onUserEvent(ManageCategoryUserEvent.ShowEditCategoryDialog(oldCategory))
        viewModel.onUserEvent(ManageCategoryUserEvent.EditCategory(" Drinks ", oldCategory))

        coVerify { categoryRepository.updateCategory(oldCategory.copy(name = "Drinks")) }
        assertEquals(ManageCategoryDialog.None, viewModel.uiState.value.dialog)
        assertNull(viewModel.uiState.value.nameErrorMessage)
    }

    @Test
    fun testDeleteCategoryPaths() = runTest(testDispatcher) {
        val viewModel = ManageCategoryViewModel(categoryRepository, transactionRepository)
        val category = Category(id = 1, name = "Food", type = TransactionType.EXPENSE)

        // 1. Has transactions (Delete Fails)
        coEvery { transactionRepository.hasTransactionsWithCategoryId(1) } returns true
        viewModel.onUserEvent(ManageCategoryUserEvent.RequestDelete(category))
        viewModel.onUserEvent(ManageCategoryUserEvent.ConfirmDelete)

        assertEquals(ManageCategoryDialog.DeleteFailed, viewModel.uiState.value.dialog)

        // 2. Does not have transactions (Delete Succeeds)
        coEvery { transactionRepository.hasTransactionsWithCategoryId(1) } returns false
        coEvery { categoryRepository.deleteCategory(category) } returns Unit

        viewModel.onUserEvent(ManageCategoryUserEvent.RequestDelete(category))
        viewModel.onUserEvent(ManageCategoryUserEvent.ConfirmDelete)

        coVerify { categoryRepository.deleteCategory(category) }
        assertEquals(ManageCategoryDialog.None, viewModel.uiState.value.dialog)
    }

    @Test
    fun testBackButtonEvent() = runTest(testDispatcher) {
        val viewModel = ManageCategoryViewModel(categoryRepository, transactionRepository)
        val events = mutableListOf<ManageCategoryUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        viewModel.onUserEvent(ManageCategoryUserEvent.Back)
        assertEquals(1, events.size)
        assertEquals(ManageCategoryUiEvent.BackPressed, events.first())

        job.cancel()
    }
}
