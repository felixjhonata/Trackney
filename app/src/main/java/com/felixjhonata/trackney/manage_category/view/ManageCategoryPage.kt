package com.felixjhonata.trackney.manage_category.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.felixjhonata.trackney.R
import com.felixjhonata.trackney.manage_category.model.ManageCategoryDialog
import com.felixjhonata.trackney.manage_category.model.ManageCategoryUiEvent
import com.felixjhonata.trackney.manage_category.model.ManageCategoryUiState
import com.felixjhonata.trackney.manage_category.model.ManageCategoryUserEvent
import com.felixjhonata.trackney.manage_category.viewmodel.ManageCategoryViewModel
import com.felixjhonata.trackney.shared.model.ManageCategory
import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.ui.theme.TrackneyTheme
import java.util.Locale

@Composable
private fun IconBtn(
    @DrawableRes id: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "",
    tint: Color = LocalContentColor.current
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painterResource(id),
            contentDescription,
            tint = tint
        )
    }
}

@Composable
private fun ListItem(
    name: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        IconBtn(
            R.drawable.outline_edit_24,
            onClick = onEdit,
            contentDescription = "Edit button"
        )
        IconBtn(
            R.drawable.outline_delete_24,
            onClick = onDelete,
            contentDescription = "Delete button",
            tint = MaterialTheme.colorScheme.error
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    type: TransactionType,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val transactionType = type.toString().lowercase().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
    }
    TopAppBar(
        modifier = modifier,
        title = { Text("$transactionType Category") },
        navigationIcon = {
            IconBtn(
                R.drawable.outline_arrow_back_24,
                onClick = onBack,
                contentDescription = "Back button"
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfirmDeleteDialog(
    categoryName: String,
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss
    ) {
        ConfirmDeleteDialogContent(
            categoryName = categoryName,
            onConfirm = onConfirm,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun ConfirmDeleteDialogContent(
    categoryName: String,
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                "Delete category",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Are you sure you want to delete \"$categoryName\"?",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = onConfirm
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

@Preview
@Composable
private fun ConfirmDeleteDialogContentPreview() {
    TrackneyTheme {
        ConfirmDeleteDialogContent(
            categoryName = "Transport",
            onConfirm = {},
            onDismiss = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteFailedDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss
    ) {
        DeleteFailedDialogContent(onDismiss = onDismiss)
    }
}

@Composable
private fun DeleteFailedDialogContent(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                "Delete failed",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Category can't be deleted because it is still used in transaction(s)",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(24.dp))

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = onDismiss
            ) {
                Text("Okay")
            }
        }
    }
}

@Preview
@Composable
private fun DeleteFailedDialogContentPreview() {
    TrackneyTheme {
        DeleteFailedDialogContent { }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditCategoryDialog(
    modifier: Modifier = Modifier,
    category: Category? = null,
    nameErrorMessage: String? = null,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = false
        )
    ) {
        AddEditCategoryDialogContent(
            category,
            nameErrorMessage = nameErrorMessage,
            onDismiss = onDismiss,
            onConfirm = onConfirm
        )
    }
}

@Composable
fun AddEditCategoryDialogContent(
    category: Category?,
    onConfirm: (String) -> Unit,
    modifier: Modifier = Modifier,
    nameErrorMessage: String? = null,
    onDismiss: () -> Unit
) {
    val type = if (category == null) "Add" else "Edit"

    var value by rememberSaveable(category) { mutableStateOf(category?.name.orEmpty()) }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                "$type category",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                placeholder = { Text("Category name...") },
                isError = nameErrorMessage != null,
                singleLine = true,
                supportingText = nameErrorMessage?.let {
                    { Text(it) }
                }
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = { onConfirm(value) }
                ) {
                    Text(type)
                }
            }
        }
    }
}

@Preview
@Composable
private fun AddEditCategoryDialogContentPreview() {
    TrackneyTheme { 
        AddEditCategoryDialogContent(
            null,
            onConfirm = {}
        ) { }
    }
}

@Composable
fun ManageCategoryPageContent(
    uiState: ManageCategoryUiState,
    onUserEvent: (ManageCategoryUserEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(uiState.type) {
                onUserEvent(ManageCategoryUserEvent.Back)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onUserEvent(ManageCategoryUserEvent.ShowAddCategoryDialog) }
            ) {
                Icon(
                    painterResource(R.drawable.outline_add_24),
                    "add floating action button"
                )
            }
        }
    ) { innerPadding ->
        when(uiState.dialog) {
            ManageCategoryDialog.None -> Unit
            ManageCategoryDialog.DeleteFailed -> {
                DeleteFailedDialog {
                    onUserEvent(ManageCategoryUserEvent.HideDialog)
                }
            }
            is ManageCategoryDialog.ConfirmDelete -> {
                ConfirmDeleteDialog(
                    categoryName = uiState.dialog.category.name,
                    onConfirm = {
                        onUserEvent(ManageCategoryUserEvent.ConfirmDelete)
                    },
                    onDismiss = {
                        onUserEvent(ManageCategoryUserEvent.HideDialog)
                    }
                )
            }
            ManageCategoryDialog.AddCategory -> {
                AddEditCategoryDialog(
                    nameErrorMessage = uiState.nameErrorMessage,
                    onConfirm = {
                        onUserEvent(ManageCategoryUserEvent.AddCategory(it))
                    }
                ) { onUserEvent(ManageCategoryUserEvent.HideDialog) }
            }
            is ManageCategoryDialog.EditCategory -> {
                val oldCategory = uiState.dialog.category

                AddEditCategoryDialog(
                    category = oldCategory,
                    nameErrorMessage = uiState.nameErrorMessage,
                    onConfirm = {
                        onUserEvent(
                            ManageCategoryUserEvent.EditCategory(it, oldCategory)
                        )
                    }
                ) { onUserEvent(ManageCategoryUserEvent.HideDialog) }
            }
        }

        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            items(
                items = uiState.categories,
                key = { it.id }
            ) {
                ListItem(
                    modifier = Modifier.padding(
                        start = 18.dp,
                        end = 8.dp
                    ),
                    name = it.name,
                    onEdit = { onUserEvent(ManageCategoryUserEvent.ShowEditCategoryDialog(it)) },
                    onDelete = { onUserEvent(ManageCategoryUserEvent.RequestDelete(it)) }
                )
            }
        }
    }
}

@Composable
fun ManageCategoryPage(
    navBackStack: NavBackStack<NavKey>,
    key: ManageCategory,
    modifier: Modifier = Modifier,
    viewModel: ManageCategoryViewModel = hiltViewModel()
) {
    LaunchedEffect(key.type) {
        viewModel.setTransactionType(key.type)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is ManageCategoryUiEvent.BackPressed -> {
                    navBackStack.removeLastOrNull()
                }
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ManageCategoryPageContent(
        uiState = uiState,
        onUserEvent = viewModel::onUserEvent,
        modifier = modifier
    )
}

@Preview
@Composable
private fun ManageCategoryPageContentPreview() {
    val categories = listOf(
        Category(0, "Transport", TransactionType.EXPENSE),
        Category(1, "Food & Drinks", TransactionType.EXPENSE),
        Category(2, "Entertainment", TransactionType.EXPENSE),
        Category(3, "Utility", TransactionType.EXPENSE),
        Category(4, "Others", TransactionType.EXPENSE)
    )

    TrackneyTheme {
        ManageCategoryPageContent(
            uiState = ManageCategoryUiState(
                categories = categories
            ),
            onUserEvent = {}
        )
    }
}