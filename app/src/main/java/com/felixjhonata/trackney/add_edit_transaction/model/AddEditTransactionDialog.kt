package com.felixjhonata.trackney.add_edit_transaction.model

import java.time.LocalDate

sealed interface AddEditTransactionDialog {
    object None: AddEditTransactionDialog
    object DatePickerDialog: AddEditTransactionDialog
    data class TimePickerDialog(val selectedDate: LocalDate): AddEditTransactionDialog
}