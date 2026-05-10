package com.felixjhonata.trackney.add_edit_transaction.viewmodel

import androidx.lifecycle.viewModelScope
import com.felixjhonata.trackney.add_edit_transaction.model.EditTransactionUserEvent
import com.felixjhonata.trackney.shared.model.annotations.IoDispatchers
import com.felixjhonata.trackney.shared.model.entity.Transaction
import com.felixjhonata.trackney.shared.model.repository.CategoryRepository
import com.felixjhonata.trackney.shared.model.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTransactionViewModel @Inject constructor(
    categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    @param:IoDispatchers private val ioDispatchers: CoroutineDispatcher
): AddEditTransactionViewModel(categoryRepository) {
    private var transactionId: Int? = null
    private var currentTransaction: Transaction? = null

    fun setTransactionId(id: Int) {
        if (transactionId == id) return
        transactionId = id
        
        viewModelScope.launch(ioDispatchers) {
            transactionRepository.getTransactionWithCategoryById(id)?.let { transactionWithCategory ->
                currentTransaction = transactionWithCategory.transaction
                initializeState(transactionWithCategory.transaction, transactionWithCategory.category)
            }
        }
    }

    private fun editTransaction() {
        val transaction = currentTransaction ?: return
        val selectedCategory = uiState.value.selectedCategory ?: return
        
        viewModelScope.launch(ioDispatchers) {
            try {
                transactionRepository.updateTransaction(
                    transaction.copy(
                        amount = amount,
                        dateTime = uiState.value.selectedLocalDateTime,
                        categoryId = selectedCategory.id,
                        note = uiState.value.note
                    )
                )
                onBack()
            } catch (_: Exception) {
                showSnackbar("Failed to update transaction")
            }
        }
    }

    private fun deleteTransaction() {
        val transaction = currentTransaction ?: return
        
        viewModelScope.launch(ioDispatchers) {
            try {
                transactionRepository.deleteTransaction(transaction)
                onBack()
            } catch (_: Exception) {
                showSnackbar("Failed to delete transaction")
            }
        }
    }

    override fun handleEditUserEvent(event: EditTransactionUserEvent) {
        when(event) {
            EditTransactionUserEvent.EditTransactionButtonPressed -> editTransaction()
            EditTransactionUserEvent.DeleteTransactionButtonPressed -> deleteTransaction()
        }
    }
}