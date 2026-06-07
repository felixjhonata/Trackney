package com.felixjhonata.trackney.add_edit_transaction.model

import com.felixjhonata.trackney.shared.model.TransactionType

sealed interface AddEditTransactionUiEvent {
    object NavigateBack: AddEditTransactionUiEvent
    data class ShowSnackbar(val message: String): AddEditTransactionUiEvent
    data class NavigateToManageCategory(val type: TransactionType): AddEditTransactionUiEvent
}