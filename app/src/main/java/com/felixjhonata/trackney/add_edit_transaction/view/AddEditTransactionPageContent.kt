package com.felixjhonata.trackney.add_edit_transaction.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felixjhonata.trackney.R
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionDialog
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUiState
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUserEvent
import com.felixjhonata.trackney.add_edit_transaction.model.AddTransactionUserEvent
import com.felixjhonata.trackney.add_edit_transaction.model.EditTransactionDialog
import com.felixjhonata.trackney.add_edit_transaction.model.EditTransactionUserEvent
import com.felixjhonata.trackney.add_edit_transaction.model.ModifyTransactionType
import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.ui.theme.TrackneyTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    type: ModifyTransactionType,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val modifyType = when (type) {
        ModifyTransactionType.ADD -> "Add"
        ModifyTransactionType.EDIT -> "Edit"
    }

    TopAppBar(
        modifier = modifier,
        title = { Text("$modifyType Transaction") },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    painterResource(R.drawable.outline_arrow_back_24),
                    "back_icon"
                )
            }
        }
    )
}

@Composable
private fun DatePickerSection(
    dateTime: String,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier) {
        Row(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.outline_calendar_today_24),
                "calendar_icon"
            )

            Spacer(Modifier.width(8.dp))

            Text(
                dateTime,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.weight(1f))

            Icon(
                painterResource(R.drawable.outline_edit_24),
                "edit_icon"
            )
        }
    }
}

@Composable
private fun AmountField(
    amount: TextFieldValue,
    onAmountChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Amount",
                style = MaterialTheme.typography.labelLarge
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Rp",
                    style = MaterialTheme.typography.headlineMedium
                )

                BasicTextField(
                    modifier = Modifier.weight(1f),
                    value = amount,
                    onValueChange = onAmountChange,
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        }
    }
}

@Composable
private fun TypeField(
    selectedType: TransactionType,
    onSelectType: (TransactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = TransactionType.entries

    SingleChoiceSegmentedButtonRow(modifier) {
        options.forEach { type ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = type.ordinal,
                    count = options.size
                ),
                onClick = { onSelectType(type) },
                selected = selectedType.ordinal == type.ordinal,
                label = { Text(type.displayName) }
            )
        }
    }
}

@Composable
private fun CategorySection(
    categories: List<Category>,
    selectedCategory: Category?,
    onSelectCategory: (Category) -> Unit,
    onManageCategoryClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Category",
            style = MaterialTheme.typography.titleLarge
        )

        IconButton(onClick = onManageCategoryClicked) {
            Icon(
                painterResource(R.drawable.outline_edit_24),
                stringResource(R.string.manage_categories)
            )
        }
    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Spacer(Modifier.width(4.dp))
        }

        items(categories) { category ->
            FilterChip(
                selected = selectedCategory?.id == category.id,
                onClick = { onSelectCategory(category) },
                label = { Text(category.name) }
            )
        }

        item {
            Spacer(Modifier.width(4.dp))
        }
    }
}

@Composable
private fun NoteField(
    note: String,
    onChangeNote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "Note (optional)",
                style = MaterialTheme.typography.labelLarge
            )

            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                value = note,
                onValueChange = onChangeNote,
                textStyle = MaterialTheme.typography.bodyMedium,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSecondaryContainer),
                minLines = 4,
                maxLines = 4
            )
        }
    }
}

@Composable
private fun FooterButton(
    type: ModifyTransactionType,
    onAddEditButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (type) {
        ModifyTransactionType.ADD -> {
            Button(
                modifier = modifier,
                onClick = onAddEditButtonClick
            ) {
                Text("Add Transaction")
            }
        }

        ModifyTransactionType.EDIT -> {
            Column(modifier) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDeleteButtonClick,
                    border = ButtonDefaults.outlinedButtonBorder().copy(
                        brush = SolidColor(MaterialTheme.colorScheme.error)
                    )
                ) {
                    Text(
                        "Delete Transaction",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onAddEditButtonClick
                ) {
                    Text("Edit Transaction")
                }
            }
        }
    }
}

@Composable
private fun DateDialog(
    initialSelectedDate: Long,
    onDismiss: () -> Unit,
    showTimePicker: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDate
    )

    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis ?: 0

                    showTimePicker(
                        Instant.ofEpochMilli(
                            selectedDateMillis
                        ).atOffset(
                            ZoneOffset.UTC
                        ).toLocalDate()
                    )
                }
            ) {
                Text("Next")
            }
        }
    ) {
        DatePicker(datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onDone: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    val timePickerState = rememberTimePickerState(
        initialHour,
        initialMinute
    )
    TimePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onDone(
                        LocalTime.of(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                    )
                }
            ) {
                Text("Done")
            }
        },
        title = { Text("Select time") }
    ) {
        TimePicker(timePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfirmDeleteTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text("Are you sure?")
        },
        text = {
            Text("You are going to delete the transaction record permanently")
        },
        confirmButton = {
            Button(onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            OutlinedButton(onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddEditTransactionPageContent(
    type: ModifyTransactionType,
    snackbarHostState: SnackbarHostState,
    uiState: AddEditTransactionUiState,
    onUserEvent: (AddEditTransactionUserEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                type,
                onBack = {
                    onUserEvent(AddEditTransactionUserEvent.BackPressed)
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->
        when (uiState.dialog) {
            AddEditTransactionDialog.None -> Unit
            AddEditTransactionDialog.DatePickerDialog -> {
                DateDialog(
                    initialSelectedDate = uiState.selectedLocalDateTime.toLocalDate().atStartOfDay(
                        ZoneOffset.UTC
                    ).toInstant().toEpochMilli(),
                    onDismiss = { onUserEvent(AddEditTransactionUserEvent.DismissDialog) },
                    showTimePicker = { localDate ->
                        onUserEvent(
                            AddEditTransactionUserEvent.ShowTimePickerDialog(
                                localDate
                            )
                        )
                    }
                )
            }
            is AddEditTransactionDialog.TimePickerDialog -> {
                TimeDialog(
                    uiState.selectedLocalDateTime.hour,
                    uiState.selectedLocalDateTime.minute,
                    onDismiss = { onUserEvent(AddEditTransactionUserEvent.DismissDialog) },
                    onDone = { selectedTime ->
                        onUserEvent(
                            AddEditTransactionUserEvent.ChangeDateTime(
                                LocalDateTime.of(
                                    uiState.dialog.selectedDate,
                                    selectedTime
                                )
                            )
                        )
                    }
                )
            }
            EditTransactionDialog.ConfirmDeleteDialog -> {
                ConfirmDeleteTransactionDialog(
                    { onUserEvent(AddEditTransactionUserEvent.DismissDialog) },
                    { onUserEvent(EditTransactionUserEvent.DeleteTransactionConfirmed) }
                )
            }
        }

        Column(
            Modifier.padding(innerPadding)
        ) {
            DatePickerSection(
                uiState.dateTime,
                modifier = Modifier
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
                    .clickable {
                        onUserEvent(AddEditTransactionUserEvent.ShowDatePickerDialog)
                    }
            )

            AmountField(
                uiState.amount,
                modifier = Modifier.padding(
                    horizontal = 12.dp
                ),
                onAmountChange = {
                    onUserEvent(
                        AddEditTransactionUserEvent.ChangeAmount(it)
                    )
                }
            )
            Spacer(Modifier.height(8.dp))

            TypeField(
                uiState.type,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(),
                onSelectType = {
                    onUserEvent(
                        AddEditTransactionUserEvent.ChangeTransactionType(
                            it
                        )
                    )
                }
            )
            Spacer(Modifier.height(12.dp))

            CategorySection(
                uiState.categories,
                uiState.selectedCategory,
                modifier = Modifier.padding(horizontal = 12.dp).fillMaxWidth(),
                onSelectCategory = {
                    onUserEvent(
                        AddEditTransactionUserEvent.ChangeSelectedCategory(it)
                    )
                },
                onManageCategoryClicked = {
                    onUserEvent(AddEditTransactionUserEvent.NavigateToManageCategory)
                }
            )
            Spacer(Modifier.height(8.dp))

            NoteField(
                uiState.note,
                modifier = Modifier.padding(horizontal = 12.dp),
                onChangeNote = {
                    onUserEvent(
                        AddEditTransactionUserEvent.ChangeNote(it)
                    )
                }
            )
            Spacer(Modifier.weight(1f))

            FooterButton(
                type = type,
                onAddEditButtonClick = {
                    val event = if (type == ModifyTransactionType.ADD) {
                        AddTransactionUserEvent.AddTransactionButtonPressed
                    } else {
                        EditTransactionUserEvent.EditTransactionButtonPressed
                    }

                    onUserEvent(event)
                },
                onDeleteButtonClick = {
                    onUserEvent(EditTransactionUserEvent.DeleteTransactionButtonPressed)
                },
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun AddEditTransactionPagePreview() {
    val categories = listOf(
        Category(
            0,
            "Food",
            TransactionType.EXPENSE
        ),
        Category(
            1,
            "Utility",
            TransactionType.EXPENSE
        ),
        Category(
            2,
            "Grocery",
            TransactionType.EXPENSE
        ),
        Category(
            3,
            "Entertainment",
            TransactionType.EXPENSE
        ),
        Category(
            4,
            "Others",
            TransactionType.EXPENSE
        )
    )

    TrackneyTheme {
        AddEditTransactionPageContent(
            ModifyTransactionType.ADD,
            SnackbarHostState(),
            AddEditTransactionUiState(
                dateTime = "30 April 2026 | 19:00",
                amount = TextFieldValue("12,000,000"),
                type = TransactionType.EXPENSE,
                categories = categories,
                selectedCategory = categories.first(),
                note = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do " +
                        "eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim " +
                        "ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut " +
                        "aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                        "in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur " +
                        "sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt " +
                        "mollit anim id est laborum."
            ),
            {}
        )
    }
}