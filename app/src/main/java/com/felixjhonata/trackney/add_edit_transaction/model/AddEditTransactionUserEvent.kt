package com.felixjhonata.trackney.add_edit_transaction.model

import androidx.compose.ui.text.input.TextFieldValue
import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.entity.Category
import java.time.LocalDate
import java.time.LocalDateTime

sealed interface AddEditTransactionUserEvent {
    object BackPressed: AddEditTransactionUserEvent
    data class ChangeAmount(val amount: TextFieldValue): AddEditTransactionUserEvent
    data class ChangeTransactionType(val type: TransactionType): AddEditTransactionUserEvent
    data class ChangeSelectedCategory(val category: Category): AddEditTransactionUserEvent
    data class ChangeNote(val note: String): AddEditTransactionUserEvent
    object NavigateToManageCategory: AddEditTransactionUserEvent
    object DismissDialog: AddEditTransactionUserEvent
    object ShowDatePickerDialog: AddEditTransactionUserEvent
    data class ShowTimePickerDialog(val selectedDate: LocalDate): AddEditTransactionUserEvent
    data class ChangeDateTime(val selectedDateTime: LocalDateTime): AddEditTransactionUserEvent
}