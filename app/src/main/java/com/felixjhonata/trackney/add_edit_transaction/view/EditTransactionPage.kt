package com.felixjhonata.trackney.add_edit_transaction.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUiState
import com.felixjhonata.trackney.add_edit_transaction.model.ModifyTransactionType

@Composable
fun EditTransactionPage(
    navBackStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier
) {
    AddEditTransactionPageContent(
        ModifyTransactionType.EDIT,
        AddEditTransactionUiState(),
        {},
        modifier = modifier
    )
}