package com.felixjhonata.trackney.add_edit_transaction.viewmodel

import androidx.lifecycle.viewModelScope
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionDialog
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUserEvent
import com.felixjhonata.trackney.add_edit_transaction.model.EditTransactionDialog
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
        val error = checkField()

        if (error != null) {
            showSnackbar(error)
            return
        }

        val uiStateValue = uiState.value

        viewModelScope.launch(ioDispatchers) {
            try {
                transactionRepository.updateTransaction(
                    transaction.copy(
                        amount = amount,
                        dateTime = uiStateValue.selectedLocalDateTime,
                        categoryId = uiStateValue.selectedCategory?.id ?: transaction.categoryId,
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
        showDialog(AddEditTransactionDialog.None)
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

    private fun showConfirmDeleteDialog() {
        if (currentTransaction == null) return
        showDialog(EditTransactionDialog.ConfirmDeleteDialog)
    }

    override fun handleChildrenUserEvent(event: AddEditTransactionUserEvent) {
        (event as? EditTransactionUserEvent)?.let { editEvent ->
            when(editEvent) {
                EditTransactionUserEvent.EditTransactionButtonPressed -> editTransaction()
                EditTransactionUserEvent.DeleteTransactionButtonPressed -> showConfirmDeleteDialog()
                EditTransactionUserEvent.DeleteTransactionConfirmed -> deleteTransaction()
            }
        }
    }
}
