package com.felixjhonata.trackney.category.model

import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.shared.model.TransactionType

data class ManageCategoriesUiState(
    val categories: List<Category> = emptyList(),
    val showAddEditDialog: Boolean = false,
    val categoryToEdit: Category? = null,
    val dialogCategoryName: String = "",
    val dialogTransactionType: TransactionType = TransactionType.EXPENSE,
    val isDialogNameError: Boolean = false
)

sealed interface ManageCategoriesUserEvent {
    data object OnAddCategoryClicked : ManageCategoriesUserEvent
    data class OnEditCategoryClicked(val category: Category) : ManageCategoriesUserEvent
    data class OnDeleteCategoryClicked(val category: Category) : ManageCategoriesUserEvent
    data object OnDismissDialog : ManageCategoriesUserEvent
    data class OnDialogNameChanged(val name: String) : ManageCategoriesUserEvent
    data class OnDialogTypeChanged(val type: TransactionType) : ManageCategoriesUserEvent
    data object OnSaveCategory : ManageCategoriesUserEvent
}

sealed interface ManageCategoriesUiEvent {
    data object NavigateBack : ManageCategoriesUiEvent
}
