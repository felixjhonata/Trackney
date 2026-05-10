package com.felixjhonata.trackney.add_edit_transaction.viewmodel

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionDialog
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUiEvent
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUiState
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUserEvent
import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.annotations.IoDispatchers
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.shared.model.entity.Transaction
import com.felixjhonata.trackney.shared.model.repository.CategoryRepository
import com.felixjhonata.trackney.shared.model.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    @param:IoDispatchers private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val formatter = DateTimeFormatter.ofPattern(
        "dd MMMM yyyy | HH:mm",
        Locale.getDefault()
    )
    private var amount = 0.0
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
    private var categories: List<Category> = emptyList()

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
            categories = categoryRepository.getCategories()

            _uiState.update {
                it.copy(
                    categories = categories.filter { category ->
                        category.type == uiState.value.type
                    }
                )
            }
        }
    }

    private fun onBack() {
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
                categories = categories.filter { category ->
                    category.type == type
                },
                selectedCategory = null
            )
        }
    }

    private fun showSnackbar(message: String) {
        viewModelScope.launch {
            _uiEvent.emit(AddEditTransactionUiEvent.ShowSnackbar(message))
        }
    }

    private fun addTransaction() {
        val uiStateValue = uiState.value

        if (uiStateValue.selectedCategory == null) {
            showSnackbar("Category isn't selected yet")
            return
        }

        val newTransaction = Transaction(
            dateTime = uiStateValue.selectedLocalDateTime,
            amount = amount,
            categoryId = uiStateValue.selectedCategory.id,
            note = uiStateValue.note
        )

        viewModelScope.launch(ioDispatcher) {
            transactionRepository.insertTransaction(newTransaction)
            onBack()
        }
    }

    fun onUserEvent(event: AddEditTransactionUserEvent) {
        when(event) {
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
                _uiState.update {
                    it.copy(dialog = AddEditTransactionDialog.None)
                }
            }
            AddEditTransactionUserEvent.ShowDatePickerDialog -> {
                _uiState.update {
                    it.copy(dialog = AddEditTransactionDialog.DatePickerDialog)
                }
            }
            is AddEditTransactionUserEvent.ShowTimePickerDialog -> {
                _uiState.update {
                    it.copy(
                        dialog = AddEditTransactionDialog.TimePickerDialog(
                            event.selectedDate
                        )
                    )
                }
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
            AddEditTransactionUserEvent.AddTransactionButtonPressed -> addTransaction()
            AddEditTransactionUserEvent.EditTransactionButtonPressed -> {
                // todo implement edit transaction operation
            }
            AddEditTransactionUserEvent.DeleteTransactionButtonPressed -> {
                // todo implement delete transaction operation
            }
        }
    }
}