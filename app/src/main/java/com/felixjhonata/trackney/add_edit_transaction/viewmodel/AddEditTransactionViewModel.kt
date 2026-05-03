package com.felixjhonata.trackney.add_edit_transaction.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUiState
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.shared.model.entity.Transaction
import com.felixjhonata.trackney.shared.model.repository.CategoryRepository
import com.felixjhonata.trackney.shared.model.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
): ViewModel() {
    private val formatter = DateTimeFormatter.ofPattern(
        "dd MMMM yyyy | HH:mm",
        Locale.getDefault()
    )

    private var dateTime = LocalDateTime.now()
    private var amount = 0.0
    private lateinit var categories: List<Category>

    private val _uiState = MutableStateFlow(
        AddEditTransactionUiState(
            dateTime = dateTime.format(formatter)
        )
    )
    val uiState = _uiState.asStateFlow()

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

    fun insertTransaction() {
        val uiStateValue = uiState.value
        transactionRepository.insertTransaction(
            Transaction(
                dateTime = dateTime,
                amount = amount,
                categoryId = uiStateValue.category?.id ?: 0,
                note = uiStateValue.note
            )
        )
    }
}