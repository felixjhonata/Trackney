package com.felixjhonata.trackney.home.viewmodel

import com.felixjhonata.trackney.home.model.HomeUiEvent
import com.felixjhonata.trackney.home.model.HomeUiState
import com.felixjhonata.trackney.home.model.HomeUserEvent
import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.TransactionWithCategory
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.shared.model.entity.Transaction
import com.felixjhonata.trackney.shared.model.repository.TransactionRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

import android.content.Context
import com.felixjhonata.trackney.shared.domain.ExportBackupUseCase
import com.felixjhonata.trackney.shared.domain.ImportBackupUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val transactionRepository: TransactionRepository = mockk()
    private val exportBackupUseCase: ExportBackupUseCase = mockk(relaxed = true)
    private val importBackupUseCase: ImportBackupUseCase = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var originalLocale: Locale

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        originalLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialStateAndDateFormatting() = runTest(testDispatcher) {
        every { transactionRepository.getTransactionsByDateRange(any(), any()) } returns flowOf(emptyList())

        val viewModel = HomeViewModel(transactionRepository, exportBackupUseCase, importBackupUseCase, context)

        // Collect uiState to activate WhileSubscribed
        val states = mutableListOf<HomeUiState>()
        val collectJob = launch {
            viewModel.uiState.collect { states.add(it) }
        }
        runCurrent()

        val expectedMonthYear = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US))
        assertEquals(expectedMonthYear, viewModel.uiState.value.selectedMonthYear)
        assertTrue(viewModel.uiState.value.totalBalance.contains("0"))

        collectJob.cancel()
    }

    @Test
    fun testTransactionsMappingAndBudgetsCalculation() = runTest(testDispatcher) {
        val now = LocalDateTime.now().withHour(10).withMinute(30)
        val transaction1 = Transaction(id = 1, dateTime = now, amount = 150000.0, categoryId = 10, note = "Salary")
        val category1 = Category(id = 10, name = "Job", type = TransactionType.INCOME)

        val transaction2 = Transaction(id = 2, dateTime = now.plusHours(1), amount = 50000.0, categoryId = 20, note = "Dinner")
        val category2 = Category(id = 20, name = "Food", type = TransactionType.EXPENSE)

        val transactions = listOf(
            TransactionWithCategory(transaction1, category1),
            TransactionWithCategory(transaction2, category2)
        )

        every { transactionRepository.getTransactionsByDateRange(any(), any()) } returns flowOf(transactions)

        val viewModel = HomeViewModel(transactionRepository, exportBackupUseCase, importBackupUseCase, context)

        val states = mutableListOf<HomeUiState>()
        val collectJob = launch {
            viewModel.uiState.collect { states.add(it) }
        }
        runCurrent()

        val state = viewModel.uiState.value
        assertTrue(state.totalIncome.contains("150.000"))
        assertTrue(state.totalExpense.contains("50.000"))
        assertTrue(state.totalBalance.contains("100.000"))

        assertEquals(1, state.groupedTransactions.size)
        val group = state.groupedTransactions.first()
        val expectedDateStr = now.toLocalDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.US))
        assertEquals(expectedDateStr, group.date)
        assertEquals(TransactionType.INCOME, group.totalAmountType)
        assertTrue(group.totalAmount.contains("100.000"))

        assertEquals(2, group.transactions.size)
        val jobItem = group.transactions.first { it.category == "Job" }
        assertEquals("10:30", jobItem.time)
        assertTrue(jobItem.amount.contains("150.000"))
        assertEquals(TransactionType.INCOME, jobItem.type)
        assertEquals("Salary", jobItem.note)
        collectJob.cancel()
    }

    @Test
    fun testNavigationUserEvents() = runTest(testDispatcher) {
        every { transactionRepository.getTransactionsByDateRange(any(), any()) } returns flowOf(emptyList())

        val viewModel = HomeViewModel(transactionRepository, exportBackupUseCase, importBackupUseCase, context)

        val states = mutableListOf<HomeUiState>()
        val collectJob = launch {
            viewModel.uiState.collect { states.add(it) }
        }
        runCurrent()

        val events = mutableListOf<HomeUiEvent>()
        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        viewModel.onUserEvent(HomeUserEvent.AddTransactionClicked)
        runCurrent()
        assertEquals(1, events.size)
        assertEquals(HomeUiEvent.NavigateToAdd, events.first())

        viewModel.onUserEvent(HomeUserEvent.EditTransactionClicked(42))
        runCurrent()
        assertEquals(2, events.size)
        assertEquals(HomeUiEvent.NavigateToEdit(42), events[1])

        job.cancel()
        collectJob.cancel()
    }

    @Test
    fun testMonthNavigation() = runTest(testDispatcher) {
        every { transactionRepository.getTransactionsByDateRange(any(), any()) } returns flowOf(emptyList())

        val viewModel = HomeViewModel(transactionRepository, exportBackupUseCase, importBackupUseCase, context)

        val states = mutableListOf<HomeUiState>()
        val collectJob = launch {
            viewModel.uiState.collect { states.add(it) }
        }
        runCurrent()

        val currentLocalDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)

        assertEquals(currentLocalDate.format(formatter), viewModel.uiState.value.selectedMonthYear)

        viewModel.onUserEvent(HomeUserEvent.NextMonth)
        runCurrent()
        assertEquals(currentLocalDate.plusMonths(1).format(formatter), viewModel.uiState.value.selectedMonthYear)

        viewModel.onUserEvent(HomeUserEvent.PreviousMonth)
        runCurrent()
        assertEquals(currentLocalDate.format(formatter), viewModel.uiState.value.selectedMonthYear)

        viewModel.onUserEvent(HomeUserEvent.PreviousMonth)
        runCurrent()
        assertEquals(currentLocalDate.minusMonths(1).format(formatter), viewModel.uiState.value.selectedMonthYear)

        collectJob.cancel()
    }
}
