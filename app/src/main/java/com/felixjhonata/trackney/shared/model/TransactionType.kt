package com.felixjhonata.trackney.shared.model

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionType(val displayName: String) {
    EXPENSE("Expense"), INCOME("Income")
}