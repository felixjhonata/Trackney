package com.felixjhonata.trackney.manage_category.model

import com.felixjhonata.trackney.shared.model.entity.Category

sealed interface ManageCategoryDialog {
    object None: ManageCategoryDialog
    object DeleteFailed: ManageCategoryDialog
    data class ConfirmDelete(val category: Category): ManageCategoryDialog
    object AddCategory: ManageCategoryDialog
    data class EditCategory(val category: Category): ManageCategoryDialog
}