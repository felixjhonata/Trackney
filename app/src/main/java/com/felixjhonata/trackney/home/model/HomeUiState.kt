package com.felixjhonata.trackney.home.model

import androidx.annotation.StringRes
import com.felixjhonata.trackney.shared.model.TransactionType

data class HomeUiState(
    val selectedMonthYear: String = "",
    val totalBalance: String = "",
    val totalIncome: String = "",
    val totalExpense: String = "",
    val groupedTransactions: List<TransactionGroup> = emptyList(),
    val isExporting: Boolean = false,
    val isImporting: Boolean = false
)

data class TransactionGroup(
    val date: String,
    val totalAmount: String,
    val totalAmountType: TransactionType,
    val transactions: List<TransactionItemUiState>
)

data class TransactionItemUiState(
    val id: Int,
    val category: String,
    val time: String,
    val amount: String,
    val type: TransactionType,
    val note: String
)

sealed interface HomeUserEvent {
    data object PreviousMonth : HomeUserEvent
    data object NextMonth : HomeUserEvent
    data object AddTransactionClicked : HomeUserEvent
    data class EditTransactionClicked(val transactionId: Int) : HomeUserEvent
    data class ExportData(val uri: String) : HomeUserEvent
    data class ImportData(val uri: String) : HomeUserEvent
}

sealed interface HomeUiEvent {
    data object NavigateToAdd : HomeUiEvent
    data class NavigateToEdit(val transactionId: Int) : HomeUiEvent
    data class ShowSnackbar(
        @param:StringRes val messageRes: Int,
        val formatArg: String? = null
    ) : HomeUiEvent
}
