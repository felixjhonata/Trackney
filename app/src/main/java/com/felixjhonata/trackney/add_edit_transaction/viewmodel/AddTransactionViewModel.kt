package com.felixjhonata.trackney.add_edit_transaction.viewmodel

import androidx.lifecycle.viewModelScope
import com.felixjhonata.trackney.add_edit_transaction.model.AddTransactionUserEvent
import com.felixjhonata.trackney.shared.model.annotations.IoDispatchers
import com.felixjhonata.trackney.shared.model.entity.Transaction
import com.felixjhonata.trackney.shared.model.repository.CategoryRepository
import com.felixjhonata.trackney.shared.model.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    @param:IoDispatchers private val ioDispatchers: CoroutineDispatcher
): AddEditTransactionViewModel(
    categoryRepository
) {
    override fun handleAddUserEvent(event: AddTransactionUserEvent) {
        when(event) {
            AddTransactionUserEvent.AddTransactionButtonPressed -> addTransaction()
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

        viewModelScope.launch(ioDispatchers) {
            transactionRepository.insertTransaction(newTransaction)
            onBack()
        }
    }
}