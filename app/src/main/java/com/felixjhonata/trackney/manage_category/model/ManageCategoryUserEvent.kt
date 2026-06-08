package com.felixjhonata.trackney.manage_category.model

import com.felixjhonata.trackney.shared.model.entity.Category

sealed interface ManageCategoryUserEvent {
    data class RequestDelete(val category: Category): ManageCategoryUserEvent
    data object ConfirmDelete: ManageCategoryUserEvent
    data object Back: ManageCategoryUserEvent
    data object HideDialog: ManageCategoryUserEvent
    data object ShowAddCategoryDialog: ManageCategoryUserEvent
    data class ShowEditCategoryDialog(val category: Category): ManageCategoryUserEvent
    data class AddCategory(val categoryName: String): ManageCategoryUserEvent
    data class EditCategory(
        val categoryName: String,
        val oldCategory: Category
    ): ManageCategoryUserEvent
}