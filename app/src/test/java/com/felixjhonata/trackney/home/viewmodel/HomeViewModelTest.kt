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
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

import com.felixjhonata.trackney.shared.domain.ExportBackupUseCase
import com.felixjhonata.trackney.shared.domain.ImportBackupUseCase
import com.felixjhonata.trackney.shared.util.BackupStreamProvider
import com.felixjhonata.trackney.R

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val transactionRepository: TransactionRepository = mockk()
    private val exportBackupUseCase: ExportBackupUseCase = mockk(relaxed = true)
    private val importBackupUseCase: ImportBackupUseCase = mockk(relaxed = true)
    private val backupStreamProvider: BackupStreamProvider = mockk(relaxed = true)
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

        val viewModel = HomeViewModel(transactionRepository, exportBackupUseCase, importBackupUseCase, backupStreamProvider)

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

        val viewModel = HomeViewModel(transactionRepository, exportBackupUseCase, importBackupUseCase, backupStreamProvider)

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

        val viewModel = HomeViewModel(transactionRepository, exportBackupUseCase, importBackupUseCase, backupStreamProvider)

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

        val viewModel = HomeViewModel(transactionRepository, exportBackupUseCase, importBackupUseCase, backupStreamProvider)

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

    @Test
    fun testExportBackupSuccess() = runTest(testDispatcher) {
        val uri: android.net.Uri = mockk()
        val outputStream = java.io.ByteArrayOutputStream()
        
        every { backupStreamProvider.openOutputStream(uri) } returns outputStream
        every { transactionRepository.getTransactionsByDateRange(any(), any()) } returns flowOf(emptyList())

        val viewModel = HomeViewModel(transactionRepository, exportBackupUseCase, importBackupUseCase, backupStreamProvider)

        val states = mutableListOf<HomeUiState>()
        val collectJob = launch { viewModel.uiState.collect { states.add(it) } }
        
        val events = mutableListOf<HomeUiEvent>()
        val eventJob = launch { viewModel.uiEvent.collect { events.add(it) } }
        runCurrent()

        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns testDispatcher
        every { Dispatchers.Default } answers { callOriginal() }
        every { Dispatchers.Main } answers { callOriginal() }
        every { Dispatchers.Unconfined } answers { callOriginal() }

        try {
            viewModel.onUserEvent(HomeUserEvent.ExportData(uri))
            advanceUntilIdle()
        } finally {
            unmockkStatic(Dispatchers::class)
        }

        io.mockk.coVerify(exactly = 1) { exportBackupUseCase.invoke(outputStream) }
        assertTrue(events.contains(HomeUiEvent.ShowSnackbar(R.string.backup_exported_success)))

        eventJob.cancel()
        collectJob.cancel()
    }

    @Test
    fun testExportBackupFailure() = runTest(testDispatcher) {
        val uri: android.net.Uri = mockk()
        
        every { backupStreamProvider.openOutputStream(uri) } throws RuntimeException("Disk full")
        every { transactionRepository.getTransactionsByDateRange(any(), any()) } returns flowOf(emptyList())

        val viewModel = HomeViewModel(transactionRepository, exportBackupUseCase, importBackupUseCase, backupStreamProvider)

        val states = mutableListOf<HomeUiState>()
        val collectJob = launch { viewModel.uiState.collect { states.add(it) } }
        
        val events = mutableListOf<HomeUiEvent>()
        val eventJob = launch { viewModel.uiEvent.collect { events.add(it) } }
        runCurrent()

        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns testDispatcher
        every { Dispatchers.Default } answers { callOriginal() }
        every { Dispatchers.Main } answers { callOriginal() }
        every { Dispatchers.Unconfined } answers { callOriginal() }

        try {
            viewModel.onUserEvent(HomeUserEvent.ExportData(uri))
            advanceUntilIdle()
        } finally {
            unmockkStatic(Dispatchers::class)
        }

        assertTrue(events.any { it is HomeUiEvent.ShowSnackbar && it.messageRes == R.string.export_failed && it.formatArg == "Disk full" })

        eventJob.cancel()
        collectJob.cancel()
    }

    @Test
    fun testImportBackupSuccess() = runTest(testDispatcher) {
        val uri: android.net.Uri = mockk()
        val inputStream = java.io.ByteArrayInputStream(byteArrayOf())
        
        every { backupStreamProvider.openInputStream(uri) } returns inputStream
        every { transactionRepository.getTransactionsByDateRange(any(), any()) } returns flowOf(emptyList())

        val viewModel = HomeViewModel(transactionRepository, exportBackupUseCase, importBackupUseCase, backupStreamProvider)

        val states = mutableListOf<HomeUiState>()
        val collectJob = launch { viewModel.uiState.collect { states.add(it) } }
        
        val events = mutableListOf<HomeUiEvent>()
        val eventJob = launch { viewModel.uiEvent.collect { events.add(it) } }
        runCurrent()

        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns testDispatcher
        every { Dispatchers.Default } answers { callOriginal() }
        every { Dispatchers.Main } answers { callOriginal() }
        every { Dispatchers.Unconfined } answers { callOriginal() }

        try {
            viewModel.onUserEvent(HomeUserEvent.ImportData(uri))
            advanceUntilIdle()
        } finally {
            unmockkStatic(Dispatchers::class)
        }

        io.mockk.coVerify(exactly = 1) { importBackupUseCase.invoke(inputStream) }
        assertTrue(events.contains(HomeUiEvent.ShowSnackbar(R.string.backup_imported_success)))

        eventJob.cancel()
        collectJob.cancel()
    }

    @Test
    fun testImportBackupFailure() = runTest(testDispatcher) {
        val uri: android.net.Uri = mockk()
        
        every { backupStreamProvider.openInputStream(uri) } throws RuntimeException("Invalid JSON")
        every { transactionRepository.getTransactionsByDateRange(any(), any()) } returns flowOf(emptyList())

        val viewModel = HomeViewModel(transactionRepository, exportBackupUseCase, importBackupUseCase, backupStreamProvider)

        val states = mutableListOf<HomeUiState>()
        val collectJob = launch { viewModel.uiState.collect { states.add(it) } }
        
        val events = mutableListOf<HomeUiEvent>()
        val eventJob = launch { viewModel.uiEvent.collect { events.add(it) } }
        runCurrent()

        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns testDispatcher
        every { Dispatchers.Default } answers { callOriginal() }
        every { Dispatchers.Main } answers { callOriginal() }
        every { Dispatchers.Unconfined } answers { callOriginal() }

        try {
            viewModel.onUserEvent(HomeUserEvent.ImportData(uri))
            advanceUntilIdle()
        } finally {
            unmockkStatic(Dispatchers::class)
        }

        assertTrue(events.any { it is HomeUiEvent.ShowSnackbar && it.messageRes == R.string.import_failed && it.formatArg == "Invalid JSON" })

        eventJob.cancel()
        collectJob.cancel()
    }

    @Test
    fun testEventsBoilerplate() {
        val uri1: android.net.Uri = mockk()
        val uri2: android.net.Uri = mockk()
        
        // HomeUserEvent
        val prev = HomeUserEvent.PreviousMonth
        val next = HomeUserEvent.NextMonth
        val add = HomeUserEvent.AddTransactionClicked
        val edit1 = HomeUserEvent.EditTransactionClicked(1)
        val edit2 = HomeUserEvent.EditTransactionClicked(1)
        val edit3 = HomeUserEvent.EditTransactionClicked(2)
        val exp1 = HomeUserEvent.ExportData(uri1)
        val exp2 = HomeUserEvent.ExportData(uri1)
        val exp3 = HomeUserEvent.ExportData(uri2)
        val imp1 = HomeUserEvent.ImportData(uri1)
        val imp2 = HomeUserEvent.ImportData(uri1)
        val imp3 = HomeUserEvent.ImportData(uri2)

        assertEquals(prev, HomeUserEvent.PreviousMonth)
        assertEquals(next, HomeUserEvent.NextMonth)
        assertEquals(add, HomeUserEvent.AddTransactionClicked)
        assertEquals(edit1, edit2)
        assertNotEquals(edit1, edit3)
        assertEquals(edit1.hashCode(), edit2.hashCode())
        assertEquals("EditTransactionClicked(transactionId=1)", edit1.toString())
        
        assertEquals(exp1, exp2)
        assertNotEquals(exp1, exp3)
        assertEquals(exp1.hashCode(), exp2.hashCode())
        assertEquals("ExportData(uri=$uri1)", exp1.toString())
        
        assertEquals(imp1, imp2)
        assertNotEquals(imp1, imp3)
        assertEquals(imp1.hashCode(), imp2.hashCode())
        assertEquals("ImportData(uri=$uri1)", imp1.toString())

        // HomeUiEvent
        val navAdd = HomeUiEvent.NavigateToAdd
        val navEdit1 = HomeUiEvent.NavigateToEdit(1)
        val navEdit2 = HomeUiEvent.NavigateToEdit(1)
        val navEdit3 = HomeUiEvent.NavigateToEdit(2)
        val snack1 = HomeUiEvent.ShowSnackbar(1, "test")
        val snack2 = HomeUiEvent.ShowSnackbar(1, "test")
        val snack3 = HomeUiEvent.ShowSnackbar(2, "other")

        assertEquals(navAdd, HomeUiEvent.NavigateToAdd)
        assertEquals(navEdit1, navEdit2)
        assertNotEquals(navEdit1, navEdit3)
        assertEquals(navEdit1.hashCode(), navEdit2.hashCode())
        assertEquals("NavigateToEdit(transactionId=1)", navEdit1.toString())
        
        assertEquals(snack1, snack2)
        assertNotEquals(snack1, snack3)
        assertEquals(snack1.hashCode(), snack2.hashCode())
        assertEquals("ShowSnackbar(messageRes=1, formatArg=test)", snack1.toString())
    }
}
