package com.felixjhonata.trackney.category.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixjhonata.trackney.category.model.ManageCategoriesUiEvent
import com.felixjhonata.trackney.category.model.ManageCategoriesUiState
import com.felixjhonata.trackney.category.model.ManageCategoriesUserEvent
import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.shared.model.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageCategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageCategoriesUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ManageCategoriesUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getCategoriesAsFlow().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    fun onUserEvent(event: ManageCategoriesUserEvent) {
        when (event) {
            ManageCategoriesUserEvent.OnAddCategoryClicked -> {
                _uiState.update {
                    it.copy(
                        showAddEditDialog = true,
                        categoryToEdit = null,
                        dialogCategoryName = "",
                        dialogTransactionType = TransactionType.EXPENSE,
                        isDialogNameError = false
                    )
                }
            }
            is ManageCategoriesUserEvent.OnEditCategoryClicked -> {
                _uiState.update {
                    it.copy(
                        showAddEditDialog = true,
                        categoryToEdit = event.category,
                        dialogCategoryName = event.category.name,
                        dialogTransactionType = event.category.type,
                        isDialogNameError = false
                    )
                }
            }
            is ManageCategoriesUserEvent.OnDeleteCategoryClicked -> {
                viewModelScope.launch {
                    categoryRepository.deleteCategory(event.category)
                }
            }
            ManageCategoriesUserEvent.OnDismissDialog -> {
                _uiState.update { it.copy(showAddEditDialog = false) }
            }
            is ManageCategoriesUserEvent.OnDialogNameChanged -> {
                _uiState.update {
                    it.copy(
                        dialogCategoryName = event.name,
                        isDialogNameError = false
                    )
                }
            }
            is ManageCategoriesUserEvent.OnDialogTypeChanged -> {
                _uiState.update { it.copy(dialogTransactionType = event.type) }
            }
            ManageCategoriesUserEvent.OnSaveCategory -> {
                val state = _uiState.value
                if (state.dialogCategoryName.isBlank()) {
                    _uiState.update { it.copy(isDialogNameError = true) }
                    return
                }

                viewModelScope.launch {
                    val categoryToEdit = state.categoryToEdit
                    if (categoryToEdit != null) {
                        val updatedCategory = categoryToEdit.copy(
                            name = state.dialogCategoryName,
                            type = state.dialogTransactionType
                        )
                        categoryRepository.updateCategory(updatedCategory)
                    } else {
                        val newCategory = Category(
                            id = 0, // Id will be generated correctly by the repository based on maxId
                            name = state.dialogCategoryName,
                            type = state.dialogTransactionType
                        )
                        categoryRepository.insertCategory(newCategory)
                    }
                    _uiState.update { it.copy(showAddEditDialog = false) }
                }
            }
        }
    }
}
