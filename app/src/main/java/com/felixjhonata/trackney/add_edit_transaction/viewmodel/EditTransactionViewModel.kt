package com.felixjhonata.trackney.add_edit_transaction.viewmodel

import com.felixjhonata.trackney.add_edit_transaction.model.EditTransactionUserEvent
import com.felixjhonata.trackney.shared.model.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditTransactionViewModel @Inject constructor(
    categoryRepository: CategoryRepository
): AddEditTransactionViewModel(categoryRepository) {
    override fun handleEditUserEvent(event: EditTransactionUserEvent) {
        when(event) {
            EditTransactionUserEvent.EditTransactionButtonPressed -> {
                // todo
            }
            EditTransactionUserEvent.DeleteTransactionButtonPressed -> {
                // todo
            }
        }
    }
}