package com.felixjhonata.trackney.add_edit_transaction.model

interface AddTransactionUserEvent: AddEditTransactionUserEvent {
    object AddTransactionButtonPressed: AddTransactionUserEvent
}