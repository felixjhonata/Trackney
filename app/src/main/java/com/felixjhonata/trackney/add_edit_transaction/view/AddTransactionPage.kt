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
import com.felixjhonata.trackney.add_edit_transaction.viewmodel.AddTransactionViewModel
import com.felixjhonata.trackney.shared.model.ManageCategory

@Composable
fun AddTransactionPage(
    navBackStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { uiEvent ->
            when(uiEvent) {
                AddEditTransactionUiEvent.NavigateBack -> {
                    navBackStack.removeLastOrNull()
                }
                is AddEditTransactionUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        uiEvent.message,
                        withDismissAction = true
                    )
                }
                is AddEditTransactionUiEvent.NavigateToManageCategory -> {
                    navBackStack.add(ManageCategory(uiEvent.type))
                }
            }
        }
    }

    AddEditTransactionPageContent(
        ModifyTransactionType.ADD,
        snackbarHostState,
        uiState,
        viewModel::onUserEvent,
        modifier = modifier
    )
}