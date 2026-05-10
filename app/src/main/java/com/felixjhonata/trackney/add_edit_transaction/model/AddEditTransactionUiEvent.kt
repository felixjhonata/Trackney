package com.felixjhonata.trackney.add_edit_transaction.model

sealed interface AddEditTransactionUiEvent {
    object NavigateBack: AddEditTransactionUiEvent
    data class ShowSnackbar(val message: String): AddEditTransactionUiEvent
}