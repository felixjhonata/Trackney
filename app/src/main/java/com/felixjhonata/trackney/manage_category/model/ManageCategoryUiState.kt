package com.felixjhonata.trackney.manage_category.model

import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.entity.Category

data class ManageCategoryUiState(
    val categories: List<Category> = emptyList(),
    val type: TransactionType = TransactionType.EXPENSE,
    val dialog: ManageCategoryDialog = ManageCategoryDialog.None,
    val nameErrorMessage: String? = null
)
