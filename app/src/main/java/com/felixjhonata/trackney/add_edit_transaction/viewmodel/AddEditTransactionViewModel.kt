package com.felixjhonata.trackney.add_edit_transaction.viewmodel

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionDialog
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUiEvent
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUiState
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUserEvent
import com.felixjhonata.trackney.add_edit_transaction.model.AddTransactionUserEvent
import com.felixjhonata.trackney.add_edit_transaction.model.EditTransactionUserEvent
import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.shared.model.entity.Transaction
import com.felixjhonata.trackney.shared.model.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

abstract class AddEditTransactionViewModel(
    categoryRepository: CategoryRepository
) : ViewModel() {
    private val formatter = DateTimeFormatter.ofPattern(
        "dd MMMM yyyy | HH:mm",
        Locale.getDefault()
    )
    protected var amount = 0.0
        set(value) {
            val stringValue = formatNumber(value)
            _uiState.update {
                it.copy(
                    amount = TextFieldValue(
                        stringValue,
                        selection = TextRange(stringValue.length)
                    )
                )
            }

            field = value
        }

    private val _uiState = MutableStateFlow(
        AddEditTransactionUiState(
            dateTime = LocalDateTime.now().format(formatter)
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AddEditTransactionUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getCategories()
                .combine(
                    uiState.map { it.type }.distinctUntilChanged()
                ) { allCategories, type ->
                    allCategories.filter { it.type == type }
                }
                .collect { filteredCategories ->
                    _uiState.update {
                        it.copy(categories = filteredCategories)
                    }
                }
        }
    }

    protected fun onBack() {
        viewModelScope.launch {
            _uiEvent.emit(AddEditTransactionUiEvent.NavigateBack)
        }
    }

    private fun formatNumber(number: Number): String {
        return NumberFormat.getInstance(Locale.getDefault()).format(number)
    }

    private fun changeTransactionType(type: TransactionType) {
        _uiState.update {
            it.copy(
                type = type,
                selectedCategory = null
            )
        }
    }

    protected fun showSnackbar(message: String) {
        viewModelScope.launch {
            _uiEvent.emit(AddEditTransactionUiEvent.ShowSnackbar(message))
        }
    }

    protected fun initializeState(transaction: Transaction, category: Category) {
        amount = transaction.amount
        _uiState.update {
            it.copy(
                dateTime = transaction.dateTime.format(formatter),
                selectedLocalDateTime = transaction.dateTime,
                type = category.type,
                selectedCategory = category,
                note = transaction.note
            )
        }
    }

    protected fun checkField() = when {
        amount <= 0 -> "Amount should be more than 0"
        uiState.value.selectedCategory == null -> "Category need to be selected"
        else -> null
    }

    protected fun showDialog(dialog: AddEditTransactionDialog) {
        _uiState.update {
            it.copy(dialog = dialog)
        }
    }

    protected abstract fun handleChildrenUserEvent(event: AddEditTransactionUserEvent)

    fun onUserEvent(event: AddEditTransactionUserEvent) {
        when (event) {
            is AddEditTransactionUserEvent.ChangeAmount -> {
                amount = event.amount.text.filterNot {
                    it == ','
                }.toDoubleOrNull() ?: 0.0
            }

            is AddEditTransactionUserEvent.ChangeTransactionType -> {
                changeTransactionType(event.type)
            }

            is AddEditTransactionUserEvent.ChangeSelectedCategory -> {
                _uiState.update {
                    it.copy(
                        selectedCategory = event.category
                    )
                }
            }

            is AddEditTransactionUserEvent.ChangeNote -> {
                _uiState.update {
                    it.copy(
                        note = event.note
                    )
                }
            }

            AddEditTransactionUserEvent.DismissDialog -> {
                showDialog(AddEditTransactionDialog.None)
            }

            AddEditTransactionUserEvent.ShowDatePickerDialog -> {
                showDialog(AddEditTransactionDialog.DatePickerDialog)
            }

            is AddEditTransactionUserEvent.ShowTimePickerDialog -> {
                showDialog(
                    AddEditTransactionDialog.TimePickerDialog(
                        event.selectedDate
                    )
                )
            }

            is AddEditTransactionUserEvent.ChangeDateTime -> {
                val selectedDateTime = event.selectedDateTime
                _uiState.update {
                    it.copy(
                        dateTime = formatter.format(selectedDateTime),
                        selectedLocalDateTime = selectedDateTime,
                        dialog = AddEditTransactionDialog.None
                    )
                }
            }

            AddEditTransactionUserEvent.BackPressed -> onBack()
            AddEditTransactionUserEvent.NavigateToManageCategory -> {
                viewModelScope.launch {
                    _uiEvent.emit(AddEditTransactionUiEvent.NavigateToManageCategory(uiState.value.type))
                }
            }
            is AddTransactionUserEvent -> handleChildrenUserEvent(event)
            is EditTransactionUserEvent -> handleChildrenUserEvent(event)
        }
    }
}