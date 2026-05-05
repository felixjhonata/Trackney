package com.felixjhonata.trackney.add_edit_transaction.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUiEvent
import com.felixjhonata.trackney.add_edit_transaction.model.ModifyTransactionType
import com.felixjhonata.trackney.add_edit_transaction.viewmodel.AddEditTransactionViewModel

@Composable
fun AddTransactionPage(
    navBackStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
    viewModel: AddEditTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { uiEvent ->
            when(uiEvent) {
                AddEditTransactionUiEvent.NavigateBack -> {
                    navBackStack.removeLastOrNull()
                }
            }
        }
    }

    AddEditTransactionPageContent(
        ModifyTransactionType.ADD,
        uiState,
        viewModel::onUserEvent,
        modifier = modifier
    )
}