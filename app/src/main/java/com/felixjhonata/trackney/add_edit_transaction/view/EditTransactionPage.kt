package com.felixjhonata.trackney.add_edit_transaction.view

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUiEvent
import com.felixjhonata.trackney.add_edit_transaction.model.ModifyTransactionType
import com.felixjhonata.trackney.add_edit_transaction.viewmodel.EditTransactionViewModel
import com.felixjhonata.trackney.shared.model.EditTransaction

@Composable
fun EditTransactionPage(
    navBackStack: NavBackStack<NavKey>,
    key: EditTransaction,
    modifier: Modifier = Modifier,
    viewModel: EditTransactionViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key.transactionId) {
        viewModel.setTransactionId(key.transactionId)
    }

    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { uiEvent ->
            when (uiEvent) {
                AddEditTransactionUiEvent.NavigateBack -> {
                    navBackStack.removeLastOrNull()
                }
                is AddEditTransactionUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        uiEvent.message,
                        withDismissAction = true
                    )
                }
            }
        }
    }

    AddEditTransactionPageContent(
        ModifyTransactionType.EDIT,
        snackbarHostState,
        uiState,
        viewModel::onUserEvent,
        modifier = modifier
    )
}