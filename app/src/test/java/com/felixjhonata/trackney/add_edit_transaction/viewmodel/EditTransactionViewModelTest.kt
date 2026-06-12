package com.felixjhonata.trackney.add_edit_transaction.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionDialog
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUiEvent
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUserEvent
import com.felixjhonata.trackney.add_edit_transaction.model.EditTransactionDialog
import com.felixjhonata.trackney.add_edit_transaction.model.EditTransactionUserEvent
import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.TransactionWithCategory
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.shared.model.entity.Transaction
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
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class EditTransactionViewModelTest {

    private val categoryRepository: CategoryRepository = mockk(relaxed = true)
    private val transactionRepository: TransactionRepository = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var originalLocale: Locale

    private val now = LocalDateTime.of(2026, 6, 12, 10, 30, 0)
    private val transaction = Transaction(id = 101, dateTime = now, amount = 150000.0, categoryId = 10, note = "Initial Salary")
    private val category = Category(id = 10, name = "Salary", type = TransactionType.INCOME)
    private val transactionWithCategory = TransactionWithCategory(transaction, category)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        originalLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
        every { categoryRepository.getCategories() } returns flowOf(listOf(category))
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
        Dispatchers.resetMain()
    }

    @Test
    fun testSetTransactionIdInitializesState() = runTest(testDispatcher) {
        coEvery { transactionRepository.getTransactionWithCategoryById(101) } returns transactionWithCategory

        val viewModel = EditTransactionViewModel(categoryRepository, transactionRepository, testDispatcher)
        viewModel.setTransactionId(101)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals("12 June 2026 | 10:30", state.dateTime)
        assertEquals(now, state.selectedLocalDateTime)
        assertEquals("150,000", state.amount.text)
        assertEquals(TransactionType.INCOME, state.type)
        assertEquals(category, state.selectedCategory)
        assertEquals("Initial Salary", state.note)

        // Setting same transaction ID again shouldn't reload/refetch
        viewModel.setTransactionId(101)
        runCurrent()
        coVerify(exactly = 1) { transactionRepository.getTransactionWithCategoryById(101) }
    }

    @Test
    fun testEditTransactionSuccess() = runTest(testDispatcher) {
        coEvery { transactionRepository.getTransactionWithCategoryById(101) } returns transactionWithCategory
        coEvery { transactionRepository.updateTransaction(any()) } returns Unit

        val viewModel = EditTransactionViewModel(categoryRepository, transactionRepository, testDispatcher)
        viewModel.setTransactionId(101)
        runCurrent()

        val events = mutableListOf<AddEditTransactionUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        viewModel.onUserEvent(AddEditTransactionUserEvent.ChangeAmount(TextFieldValue("200000")))
        viewModel.onUserEvent(AddEditTransactionUserEvent.ChangeNote("Bonus"))
        viewModel.onUserEvent(EditTransactionUserEvent.EditTransactionButtonPressed)
        runCurrent()

        coVerify {
            transactionRepository.updateTransaction(
                match {
                    it.id == 101 &&
                            it.amount == 200000.0 &&
                            it.note == "Bonus" &&
                            it.dateTime == now &&
                            it.categoryId == 10
                }
            )
        }
        assertEquals(1, events.size)
        assertEquals(AddEditTransactionUiEvent.NavigateBack, events.first())

        job.cancel()
    }

    @Test
    fun testEditTransactionFailure() = runTest(testDispatcher) {
        coEvery { transactionRepository.getTransactionWithCategoryById(101) } returns transactionWithCategory
        coEvery { transactionRepository.updateTransaction(any()) } throws Exception("DB Error")

        val viewModel = EditTransactionViewModel(categoryRepository, transactionRepository, testDispatcher)
        viewModel.setTransactionId(101)
        runCurrent()

        val events = mutableListOf<AddEditTransactionUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        viewModel.onUserEvent(AddEditTransactionUserEvent.ChangeAmount(TextFieldValue("200000")))
        viewModel.onUserEvent(EditTransactionUserEvent.EditTransactionButtonPressed)
        runCurrent()

        assertEquals(1, events.size)
        assertEquals(AddEditTransactionUiEvent.ShowSnackbar("Failed to update transaction"), events.first())

        job.cancel()
    }

    @Test
    fun testEditTransactionValidationError() = runTest(testDispatcher) {
        coEvery { transactionRepository.getTransactionWithCategoryById(101) } returns transactionWithCategory

        val viewModel = EditTransactionViewModel(categoryRepository, transactionRepository, testDispatcher)
        viewModel.setTransactionId(101)
        runCurrent()

        val events = mutableListOf<AddEditTransactionUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        viewModel.onUserEvent(AddEditTransactionUserEvent.ChangeAmount(TextFieldValue("0")))
        viewModel.onUserEvent(EditTransactionUserEvent.EditTransactionButtonPressed)
        runCurrent()

        assertEquals(1, events.size)
        assertEquals(AddEditTransactionUiEvent.ShowSnackbar("Amount should be more than 0"), events.first())

        job.cancel()
    }

    @Test
    fun testDeleteTransactionConfirmAndSuccess() = runTest(testDispatcher) {
        coEvery { transactionRepository.getTransactionWithCategoryById(101) } returns transactionWithCategory
        coEvery { transactionRepository.deleteTransaction(any()) } returns Unit

        val viewModel = EditTransactionViewModel(categoryRepository, transactionRepository, testDispatcher)
        viewModel.setTransactionId(101)
        runCurrent()

        val events = mutableListOf<AddEditTransactionUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        // 1. Show confirmation dialog
        viewModel.onUserEvent(EditTransactionUserEvent.DeleteTransactionButtonPressed)
        assertEquals(EditTransactionDialog.ConfirmDeleteDialog, viewModel.uiState.value.dialog)

        // 2. Confirm and delete
        viewModel.onUserEvent(EditTransactionUserEvent.DeleteTransactionConfirmed)
        runCurrent()

        coVerify { transactionRepository.deleteTransaction(transaction) }
        assertEquals(AddEditTransactionDialog.None, viewModel.uiState.value.dialog)
        assertEquals(1, events.size)
        assertEquals(AddEditTransactionUiEvent.NavigateBack, events.first())

        job.cancel()
    }

    @Test
    fun testDeleteTransactionFailure() = runTest(testDispatcher) {
        coEvery { transactionRepository.getTransactionWithCategoryById(101) } returns transactionWithCategory
        coEvery { transactionRepository.deleteTransaction(any()) } throws Exception("DB Error")

        val viewModel = EditTransactionViewModel(categoryRepository, transactionRepository, testDispatcher)
        viewModel.setTransactionId(101)
        runCurrent()

        val events = mutableListOf<AddEditTransactionUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        viewModel.onUserEvent(EditTransactionUserEvent.DeleteTransactionButtonPressed)
        viewModel.onUserEvent(EditTransactionUserEvent.DeleteTransactionConfirmed)
        runCurrent()

        assertEquals(1, events.size)
        assertEquals(AddEditTransactionUiEvent.ShowSnackbar("Failed to delete transaction"), events.first())

        job.cancel()
    }

    @Test
    fun testActionWhenCurrentTransactionIsNull() = runTest(testDispatcher) {
        val viewModel = EditTransactionViewModel(categoryRepository, transactionRepository, testDispatcher)

        // Edit Transaction Button when currentTransaction is null should return early and do nothing
        viewModel.onUserEvent(EditTransactionUserEvent.EditTransactionButtonPressed)
        runCurrent()
        coVerify(exactly = 0) { transactionRepository.updateTransaction(any()) }

        // Delete Transaction Button when currentTransaction is null should return early and do nothing
        viewModel.onUserEvent(EditTransactionUserEvent.DeleteTransactionButtonPressed)
        assertEquals(AddEditTransactionDialog.None, viewModel.uiState.value.dialog)

        // Delete Transaction Confirmed when currentTransaction is null should return early and do nothing
        viewModel.onUserEvent(EditTransactionUserEvent.DeleteTransactionConfirmed)
        runCurrent()
        coVerify(exactly = 0) { transactionRepository.deleteTransaction(any()) }
    }
}
