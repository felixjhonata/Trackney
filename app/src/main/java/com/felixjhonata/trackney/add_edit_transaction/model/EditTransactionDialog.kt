package com.felixjhonata.trackney.add_edit_transaction.model

sealed interface EditTransactionDialog: AddEditTransactionDialog {
    object ConfirmDeleteDialog: EditTransactionDialog
}