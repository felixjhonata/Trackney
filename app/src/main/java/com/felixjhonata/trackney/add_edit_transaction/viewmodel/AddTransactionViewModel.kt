package com.felixjhonata.trackney.add_edit_transaction.viewmodel

import androidx.lifecycle.ViewModel
import com.felixjhonata.trackney.add_edit_transaction.model.AddTransactionUiState
import com.felixjhonata.trackney.shared.model.entity.Transaction
import com.felixjhonata.trackney.shared.model.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState = _uiState.asStateFlow()

    fun insertTransaction() {
        val uiStateValue = uiState.value
        transactionRepository.insertTransaction(
            Transaction(
                dateTime = uiStateValue.dateTime,
                amount = uiStateValue.amount,
                categoryId = uiStateValue.category?.id ?: 0,
                note = uiStateValue.note
            )
        )
    }
}