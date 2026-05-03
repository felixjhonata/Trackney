package com.felixjhonata.trackney.add_edit_transaction.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.felixjhonata.trackney.add_edit_transaction.model.ModifyTransactionType
import com.felixjhonata.trackney.add_edit_transaction.viewmodel.AddTransactionViewModel

@Composable
fun AddTransactionPage(
    navBackStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    AddEditTransactionPageContent(
        ModifyTransactionType.ADD,
        { navBackStack.removeLastOrNull() },
        { viewModel.insertTransaction() },
        modifier = modifier
    )
}