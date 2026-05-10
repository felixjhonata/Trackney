package com.felixjhonata.trackney.add_edit_transaction.model

sealed interface EditTransactionUserEvent: AddEditTransactionUserEvent {
    object EditTransactionButtonPressed: EditTransactionUserEvent
    object DeleteTransactionButtonPressed: EditTransactionUserEvent
}