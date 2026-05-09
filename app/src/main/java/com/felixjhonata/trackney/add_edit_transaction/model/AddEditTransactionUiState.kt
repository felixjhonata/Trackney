package com.felixjhonata.trackney.add_edit_transaction.model

import androidx.compose.ui.text.input.TextFieldValue
import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.entity.Category

data class AddEditTransactionUiState(
    val dateTime: String = "",
    val amount: TextFieldValue = TextFieldValue("0"),
    val type: TransactionType = TransactionType.EXPENSE,
    val categories: List<Category> = emptyList(),
    val category: Category? = null,
    val note: String = ""
)
