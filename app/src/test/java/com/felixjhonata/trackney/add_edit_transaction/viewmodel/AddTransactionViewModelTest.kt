package com.felixjhonata.trackney.add_edit_transaction.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionDialog
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUiEvent
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUserEvent
import com.felixjhonata.trackney.add_edit_transaction.model.AddTransactionUserEvent
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class AddTransactionViewModelTest {

    private val categoryRepository: CategoryRepository = mockk(relaxed = true)
    private val transactionRepository: TransactionRepository = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var originalLocale: Locale

    private val categories = listOf(
        Category(id = 1, name = "Food", type = TransactionType.EXPENSE),
        Category(id = 2, name = "Salary", type = TransactionType.INCOME)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        originalLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
        every { categoryRepository.getCategories() } returns flowOf(categories)
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() = runTest(testDispatcher) {
        val viewModel = AddTransactionViewModel(categoryRepository, transactionRepository, testDispatcher)

        val state = viewModel.uiState.value
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy | HH:mm", Locale.US)
        // Check formatted date string is reasonably close to LocalDateTime.now()
        val nowFormatted = LocalDateTime.now().format(formatter)
        assertEquals(nowFormatted, state.dateTime)
        assertEquals("0", state.amount.text)
        assertEquals(TransactionType.EXPENSE, state.type)
        assertEquals(1, state.categories.size)
        assertEquals("Food", state.categories.first().name)
        assertNull(state.selectedCategory)
        assertEquals("", state.note)
        assertEquals(AddEditTransactionDialog.None, state.dialog)
    }

    @Test
    fun testAmountInputChanges() = runTest(testDispatcher) {
        val viewModel = AddTransactionViewModel(categoryRepository, transactionRepository, testDispatcher)

        viewModel.onUserEvent(AddEditTransactionUserEvent.ChangeAmount(TextFieldValue("150,000")))
        assertEquals("150,000", viewModel.uiState.value.amount.text)

        viewModel.onUserEvent(AddEditTransactionUserEvent.ChangeAmount(TextFieldValue("invalid")))
        assertEquals("0", viewModel.uiState.value.amount.text)
    }

    @Test
    fun testTransactionTypeChanges() = runTest(testDispatcher) {
        val viewModel = AddTransactionViewModel(categoryRepository, transactionRepository, testDispatcher)

        viewModel.onUserEvent(AddEditTransactionUserEvent.ChangeTransactionType(TransactionType.INCOME))
        assertEquals(TransactionType.INCOME, viewModel.uiState.value.type)
        assertEquals(1, viewModel.uiState.value.categories.size)
        assertEquals("Salary", viewModel.uiState.value.categories.first().name)
        assertNull(viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun testNoteAndCategoryChanges() = runTest(testDispatcher) {
        val viewModel = AddTransactionViewModel(categoryRepository, transactionRepository, testDispatcher)
        val selectedCat = categories.first()

        viewModel.onUserEvent(AddEditTransactionUserEvent.ChangeSelectedCategory(selectedCat))
        assertEquals(selectedCat, viewModel.uiState.value.selectedCategory)

        viewModel.onUserEvent(AddEditTransactionUserEvent.ChangeNote("Weekly grocery shopping"))
        assertEquals("Weekly grocery shopping", viewModel.uiState.value.note)
    }

    @Test
    fun testDatePickerAndTimePickerDialogs() = runTest(testDispatcher) {
        val viewModel = AddTransactionViewModel(categoryRepository, transactionRepository, testDispatcher)

        viewModel.onUserEvent(AddEditTransactionUserEvent.ShowDatePickerDialog)
        assertEquals(AddEditTransactionDialog.DatePickerDialog, viewModel.uiState.value.dialog)

        val selectedDate = LocalDate.of(2026, 6, 15)
        viewModel.onUserEvent(AddEditTransactionUserEvent.ShowTimePickerDialog(selectedDate))
        assertEquals(AddEditTransactionDialog.TimePickerDialog(selectedDate), viewModel.uiState.value.dialog)

        viewModel.onUserEvent(AddEditTransactionUserEvent.DismissDialog)
        assertEquals(AddEditTransactionDialog.None, viewModel.uiState.value.dialog)

        val selectedDateTime = LocalDateTime.of(2026, 6, 15, 14, 45)
        viewModel.onUserEvent(AddEditTransactionUserEvent.ChangeDateTime(selectedDateTime))
        assertEquals("15 June 2026 | 14:45", viewModel.uiState.value.dateTime)
        assertEquals(selectedDateTime, viewModel.uiState.value.selectedLocalDateTime)
        assertEquals(AddEditTransactionDialog.None, viewModel.uiState.value.dialog)
    }

    @Test
    fun testNavigateToManageCategoryAndBack() = runTest(testDispatcher) {
        val viewModel = AddTransactionViewModel(categoryRepository, transactionRepository, testDispatcher)
        val events = mutableListOf<AddEditTransactionUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        viewModel.onUserEvent(AddEditTransactionUserEvent.NavigateToManageCategory)
        assertEquals(1, events.size)
        assertEquals(AddEditTransactionUiEvent.NavigateToManageCategory(TransactionType.EXPENSE), events.first())

        viewModel.onUserEvent(AddEditTransactionUserEvent.BackPressed)
        assertEquals(2, events.size)
        assertEquals(AddEditTransactionUiEvent.NavigateBack, events[1])

        job.cancel()
    }

    @Test
    fun testAddTransactionValidations() = runTest(testDispatcher) {
        val viewModel = AddTransactionViewModel(categoryRepository, transactionRepository, testDispatcher)
        val events = mutableListOf<AddEditTransactionUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        // 1. Amount is 0
        viewModel.onUserEvent(AddTransactionUserEvent.AddTransactionButtonPressed)
        assertEquals(1, events.size)
        assertEquals(AddEditTransactionUiEvent.ShowSnackbar("Amount should be more than 0"), events.first())

        // 2. Category is null
        viewModel.onUserEvent(AddEditTransactionUserEvent.ChangeAmount(TextFieldValue("50000")))
        viewModel.onUserEvent(AddTransactionUserEvent.AddTransactionButtonPressed)
        assertEquals(2, events.size)
        assertEquals(AddEditTransactionUiEvent.ShowSnackbar("Category need to be selected"), events[1])

        job.cancel()
    }

    @Test
    fun testAddTransactionSuccess() = runTest(testDispatcher) {
        val viewModel = AddTransactionViewModel(categoryRepository, transactionRepository, testDispatcher)
        val events = mutableListOf<AddEditTransactionUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        viewModel.onUserEvent(AddEditTransactionUserEvent.ChangeAmount(TextFieldValue("50000")))
        viewModel.onUserEvent(AddEditTransactionUserEvent.ChangeSelectedCategory(categories.first()))
        viewModel.onUserEvent(AddEditTransactionUserEvent.ChangeNote("Snacks"))

        coEvery { transactionRepository.insertTransaction(any()) } returns Unit

        val expectedDateTime = viewModel.uiState.value.selectedLocalDateTime

        viewModel.onUserEvent(AddTransactionUserEvent.AddTransactionButtonPressed)

        coVerify {
            transactionRepository.insertTransaction(
                match {
                    it.amount == 50000.0 &&
                            it.categoryId == categories.first().id &&
                            it.note == "Snacks" &&
                            it.dateTime == expectedDateTime
                }
            )
        }
        assertEquals(1, events.size)
        assertEquals(AddEditTransactionUiEvent.NavigateBack, events.first())

        job.cancel()
    }

    @Test
    fun testAddTransactionFailure() = runTest(testDispatcher) {
        val viewModel = AddTransactionViewModel(categoryRepository, transactionRepository, testDispatcher)
        val events = mutableListOf<AddEditTransactionUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        viewModel.onUserEvent(AddEditTransactionUserEvent.ChangeAmount(TextFieldValue("50000")))
        viewModel.onUserEvent(AddEditTransactionUserEvent.ChangeSelectedCategory(categories.first()))

        coEvery { transactionRepository.insertTransaction(any()) } throws Exception("DB Error")

        viewModel.onUserEvent(AddTransactionUserEvent.AddTransactionButtonPressed)

        assertEquals(1, events.size)
        assertEquals(AddEditTransactionUiEvent.ShowSnackbar("Failed to add transaction"), events.first())

        job.cancel()
    }
}
