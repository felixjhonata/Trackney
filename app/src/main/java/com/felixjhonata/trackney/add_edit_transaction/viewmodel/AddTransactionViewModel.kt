package com.felixjhonata.trackney.add_edit_transaction.viewmodel

import androidx.lifecycle.viewModelScope
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUserEvent
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
    override fun handleChildrenUserEvent(event: AddEditTransactionUserEvent) {
        (event as? AddTransactionUserEvent)?.let { addEvent ->
            when(addEvent) {
                AddTransactionUserEvent.AddTransactionButtonPressed -> addTransaction()
            }
        }
    }

    private fun addTransaction() {
        val error = checkField()

        if (error != null) {
            showSnackbar(error)
            return
        }

        val uiStateValue = uiState.value

        val newTransaction = Transaction(
            dateTime = uiStateValue.selectedLocalDateTime,
            amount = amount,
            categoryId = uiStateValue.selectedCategory!!.id,
            note = uiStateValue.note
        )

        viewModelScope.launch(ioDispatchers) {
            try {
                transactionRepository.insertTransaction(newTransaction)
                onBack()
            } catch (_: Exception) {
                showSnackbar("Failed to add transaction")
            }
        }
    }
}