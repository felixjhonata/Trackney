package com.felixjhonata.trackney.add_edit_transaction.model

sealed interface AddEditTransactionUserEvent {
    object BackPressed: AddEditTransactionUserEvent
    object AddTransactionButtonPressed: AddEditTransactionUserEvent
    object EditTransactionButtonPressed: AddEditTransactionUserEvent
    object DeleteTransactionButtonPressed: AddEditTransactionUserEvent
}