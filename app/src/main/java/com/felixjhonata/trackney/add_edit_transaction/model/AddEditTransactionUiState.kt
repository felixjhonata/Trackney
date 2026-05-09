package com.felixjhonata.trackney.add_edit_transaction.model

import androidx.compose.ui.text.input.TextFieldValue
import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.entity.Category
import java.time.LocalDateTime

data class AddEditTransactionUiState(
    val dateTime: String = "",
    val selectedLocalDateTime: LocalDateTime = LocalDateTime.now(),
    val amount: TextFieldValue = TextFieldValue("0"),
    val type: TransactionType = TransactionType.EXPENSE,
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val note: String = "",
    val dialog: AddEditTransactionDialog = AddEditTransactionDialog.None
)
