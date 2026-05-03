package com.felixjhonata.trackney.add_edit_transaction.model

import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.entity.Category
import java.time.LocalDateTime

data class AddTransactionUiState(
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val amount: Double = 0.0,
    val type: TransactionType? = null,
    val category: Category? = null,
    val note: String = ""
)
