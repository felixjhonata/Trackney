package com.felixjhonata.trackney.manage_category.model

sealed interface ManageCategoryUiEvent {
    object BackPressed: ManageCategoryUiEvent
}