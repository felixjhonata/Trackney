package com.felixjhonata.trackney.manage_category.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixjhonata.trackney.manage_category.model.ManageCategoryDialog
import com.felixjhonata.trackney.manage_category.model.ManageCategoryUiEvent
import com.felixjhonata.trackney.manage_category.model.ManageCategoryUiState
import com.felixjhonata.trackney.manage_category.model.ManageCategoryUserEvent
import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.shared.model.repository.CategoryRepository
import com.felixjhonata.trackney.shared.model.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ManageCategoryUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ManageCategoryUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var isListening = false

    fun setTransactionType(type: TransactionType) {
        _uiState.update {
            it.copy(type = type)
        }
        if (!isListening) {
            isListening = true
            observeCategories()
        }
    }

    private fun observeCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories()
                .combine(
                    uiState.map { it.type }.distinctUntilChanged()
                ) { allCategories, type ->
                    allCategories.filter { it.type == type }
                }
                .collect { filteredCategories ->
                    _uiState.update {
                        it.copy(
                            categories = filteredCategories
                        )
                    }
                }
        }
    }

    fun onUserEvent(userEvent: ManageCategoryUserEvent) {
        when (userEvent) {
            is ManageCategoryUserEvent.RequestDelete -> {
                toggleDialog(ManageCategoryDialog.ConfirmDelete(userEvent.category))
            }

            ManageCategoryUserEvent.ConfirmDelete -> {
                val dialog = uiState.value.dialog
                if (dialog is ManageCategoryDialog.ConfirmDelete) {
                    deleteCategory(dialog.category)
                }
            }

            ManageCategoryUserEvent.Back -> {
                viewModelScope.launch {
                    _uiEvent.emit(ManageCategoryUiEvent.BackPressed)
                }
            }

            ManageCategoryUserEvent.HideDialog -> {
                toggleDialog(ManageCategoryDialog.None)
            }

            ManageCategoryUserEvent.ShowAddCategoryDialog -> {
                toggleDialog(ManageCategoryDialog.AddCategory)
            }

            is ManageCategoryUserEvent.ShowEditCategoryDialog -> {
                toggleDialog(ManageCategoryDialog.EditCategory(userEvent.category))
            }

            is ManageCategoryUserEvent.AddCategory -> {
                addCategory(userEvent.categoryName)
            }

            is ManageCategoryUserEvent.EditCategory -> {
                editCategory(userEvent.categoryName, userEvent.oldCategory)
            }
        }
    }

    private fun addCategory(categoryName: String) {
        if (categoryName.isBlank()) {
            _uiState.update { it.copy(nameErrorMessage = "Name cannot be empty") }
            return
        }
        viewModelScope.launch {
            val trimmedName = categoryName.trim()
            if (categoryRepository.existsByName(trimmedName, uiState.value.type)) {
                _uiState.update { it.copy(nameErrorMessage = "Category name already exists") }
                return@launch
            }
            categoryRepository.insertCategory(
                Category(name = trimmedName, type = uiState.value.type)
            )
            toggleDialog(ManageCategoryDialog.None)
        }
    }

    private fun editCategory(categoryName: String, oldCategory: Category) {
        if (categoryName.isBlank()) {
            _uiState.update { it.copy(nameErrorMessage = "Name cannot be empty") }
            return
        }
        viewModelScope.launch {
            val trimmedName = categoryName.trim()
            if (trimmedName != oldCategory.name) {
                if (categoryRepository.existsByName(trimmedName, oldCategory.type)) {
                    _uiState.update { it.copy(nameErrorMessage = "Category name already exists") }
                    return@launch
                }

                categoryRepository.updateCategory(
                    oldCategory.copy(name = trimmedName)
                )
            }

            toggleDialog(ManageCategoryDialog.None)
        }
    }

    private fun toggleDialog(dialog: ManageCategoryDialog) {
        _uiState.update {
            it.copy(
                dialog = dialog,
                nameErrorMessage = null
            )
        }
    }

    private fun deleteCategory(category: Category) {
        viewModelScope.launch {
            if (transactionRepository.hasTransactionsWithCategoryId(category.id)) {
                toggleDialog(ManageCategoryDialog.DeleteFailed)
            } else {
                categoryRepository.deleteCategory(category)
                toggleDialog(ManageCategoryDialog.None)
            }
        }
    }
}
