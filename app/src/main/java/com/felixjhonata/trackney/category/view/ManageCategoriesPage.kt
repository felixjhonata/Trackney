package com.felixjhonata.trackney.category.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.felixjhonata.trackney.category.model.ManageCategoriesUiEvent
import com.felixjhonata.trackney.category.model.ManageCategoriesUiState
import com.felixjhonata.trackney.category.model.ManageCategoriesUserEvent
import com.felixjhonata.trackney.category.viewmodel.ManageCategoriesViewModel
import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.entity.Category
import androidx.compose.material3.RadioButton
import androidx.compose.foundation.layout.height

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesPage(
    navBackStack: NavBackStack<NavKey>,
    viewModel: ManageCategoriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                ManageCategoriesUiEvent.NavigateBack -> navBackStack.removeLastOrNull()
            }
        }
    }

    ManageCategoriesPageContent(
        uiState = uiState,
        onUserEvent = viewModel::onUserEvent,
        onNavigateBack = { navBackStack.removeLastOrNull() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesPageContent(
    uiState: ManageCategoriesUiState,
    onUserEvent: (ManageCategoriesUserEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Categories") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onUserEvent(ManageCategoriesUserEvent.OnAddCategoryClicked) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Category")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.categories) { category ->
                CategoryCard(
                    category = category,
                    onEdit = { onUserEvent(ManageCategoriesUserEvent.OnEditCategoryClicked(category)) },
                    onDelete = { onUserEvent(ManageCategoriesUserEvent.OnDeleteCategoryClicked(category)) }
                )
            }
        }

        if (uiState.showAddEditDialog) {
            AddEditCategoryDialog(
                uiState = uiState,
                onUserEvent = onUserEvent
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = category.type.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (category.type == TransactionType.INCOME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun AddEditCategoryDialog(
    uiState: ManageCategoriesUiState,
    onUserEvent: (ManageCategoriesUserEvent) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onUserEvent(ManageCategoriesUserEvent.OnDismissDialog) },
        title = {
            Text(text = if (uiState.categoryToEdit == null) "Add Category" else "Edit Category")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = uiState.dialogCategoryName,
                    onValueChange = { onUserEvent(ManageCategoriesUserEvent.OnDialogNameChanged(it)) },
                    label = { Text("Name") },
                    isError = uiState.isDialogNameError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (uiState.isDialogNameError) {
                    Text(
                        text = "Name cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onUserEvent(ManageCategoriesUserEvent.OnDialogTypeChanged(TransactionType.EXPENSE)) }
                ) {
                    RadioButton(
                        selected = uiState.dialogTransactionType == TransactionType.EXPENSE,
                        onClick = { onUserEvent(ManageCategoriesUserEvent.OnDialogTypeChanged(TransactionType.EXPENSE)) }
                    )
                    Text("Expense")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onUserEvent(ManageCategoriesUserEvent.OnDialogTypeChanged(TransactionType.INCOME)) }
                ) {
                    RadioButton(
                        selected = uiState.dialogTransactionType == TransactionType.INCOME,
                        onClick = { onUserEvent(ManageCategoriesUserEvent.OnDialogTypeChanged(TransactionType.INCOME)) }
                    )
                    Text("Income")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onUserEvent(ManageCategoriesUserEvent.OnSaveCategory) }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onUserEvent(ManageCategoriesUserEvent.OnDismissDialog) }
            ) {
                Text("Cancel")
            }
        }
    )
}
