package com.felixjhonata.trackney.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixjhonata.trackney.home.model.HomeUiEvent
import com.felixjhonata.trackney.home.model.HomeUiState
import com.felixjhonata.trackney.home.model.HomeUserEvent
import com.felixjhonata.trackney.home.model.TransactionGroup
import com.felixjhonata.trackney.home.model.TransactionItemUiState
import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.repository.TransactionRepository
import com.felixjhonata.trackney.shared.domain.ExportBackupUseCase
import com.felixjhonata.trackney.shared.domain.ImportBackupUseCase
import com.felixjhonata.trackney.shared.util.BackupStreamResolver
import com.felixjhonata.trackney.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val exportBackupUseCase: ExportBackupUseCase,
    private val importBackupUseCase: ImportBackupUseCase,
    private val backupStreamResolver: BackupStreamResolver
) : ViewModel() {
    private val _isExporting = MutableStateFlow(false)
    private val _isImporting = MutableStateFlow(false)
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    private val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    private val currencyFormatter = NumberFormat.getCurrencyInstance(
        Locale.forLanguageTag("id-ID")
    ).apply { maximumFractionDigits = 0 }

    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _transactions = _selectedDate.flatMapLatest { date ->
        val startOfMonth = date.withDayOfMonth(1).atStartOfDay()
        val endOfMonth = date.withDayOfMonth(date.lengthOfMonth()).atTime(23, 59, 59)
        
        val zoneId = ZoneId.systemDefault()
        val startEpoch = startOfMonth.atZone(zoneId).toEpochSecond()
        val endEpoch = endOfMonth.atZone(zoneId).toEpochSecond()

        transactionRepository.getTransactionsByDateRange(startEpoch, endEpoch)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        _selectedDate,
        _transactions,
        _isExporting,
        _isImporting
    ) { date, transactions, exporting, importing ->
        val income = transactions
            .filter { it.category.type == TransactionType.INCOME }
            .sumOf { it.transaction.amount }
        val expense = transactions
            .filter { it.category.type == TransactionType.EXPENSE }
            .sumOf { it.transaction.amount }

        val grouped = transactions.groupBy { it.transaction.dateTime.toLocalDate() }
            .map { (date, transactions) ->
                val dayTotal = transactions.sumOf {
                    if (it.category.type == TransactionType.INCOME) it.transaction.amount
                    else -it.transaction.amount
                }
                TransactionGroup(
                    date = date.format(dateFormatter),
                    totalAmount = formatAmount(
                        abs(dayTotal),
                        if (dayTotal >= 0) TransactionType.INCOME else TransactionType.EXPENSE
                    ),
                    totalAmountType = if (dayTotal >= 0) TransactionType.INCOME else TransactionType.EXPENSE,
                    transactions = transactions.map {
                        TransactionItemUiState(
                            id = it.transaction.id,
                            category = it.category.name,
                            time = it.transaction.dateTime.format(timeFormatter),
                            amount = formatAmount(it.transaction.amount, it.category.type),
                            type = it.category.type,
                            note = it.transaction.note
                        )
                    }
                )
            }

        HomeUiState(
            selectedMonthYear = date.format(monthYearFormatter),
            totalIncome = formatAmount(income),
            totalExpense = formatAmount(expense),
            totalBalance = formatAmount(income - expense),
            groupedTransactions = grouped,
            isExporting = exporting,
            isImporting = importing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    private fun formatAmount(amount: Double, type: TransactionType? = null): String {
        val formatted = currencyFormatter.format(amount)
        return when (type) {
            TransactionType.INCOME -> "+$formatted"
            TransactionType.EXPENSE -> "-$formatted"
            null -> formatted
        }
    }

    fun onUserEvent(event: HomeUserEvent) {
        when (event) {
            HomeUserEvent.NextMonth -> _selectedDate.update { it.plusMonths(1) }
            HomeUserEvent.PreviousMonth -> _selectedDate.update { it.minusMonths(1) }
            HomeUserEvent.AddTransactionClicked -> {
                viewModelScope.launch {
                    _uiEvent.emit(HomeUiEvent.NavigateToAdd)
                }
            }
            is HomeUserEvent.EditTransactionClicked -> {
                viewModelScope.launch {
                    _uiEvent.emit(HomeUiEvent.NavigateToEdit(event.transactionId))
                }
            }
            is HomeUserEvent.ExportData -> performExport(event.uri)
            is HomeUserEvent.ImportData -> performImport(event.uri)
        }
    }

    private fun performExport(uriString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isExporting.value = true
            try {
                backupStreamResolver.openOutputStream(uriString)?.use { outputStream ->
                    exportBackupUseCase(outputStream)
                }
                _uiEvent.emit(HomeUiEvent.ShowSnackbar(R.string.backup_exported_success))
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.ShowSnackbar(R.string.export_failed, e.localizedMessage))
            } finally {
                _isExporting.value = false
            }
        }
    }

    private fun performImport(uriString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isImporting.value = true
            try {
                backupStreamResolver.openInputStream(uriString)?.use { inputStream ->
                    importBackupUseCase(inputStream)
                }
                _uiEvent.emit(HomeUiEvent.ShowSnackbar(R.string.backup_imported_success))
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.ShowSnackbar(R.string.import_failed, e.localizedMessage))
            } finally {
                _isImporting.value = false
            }
        }
    }
}
