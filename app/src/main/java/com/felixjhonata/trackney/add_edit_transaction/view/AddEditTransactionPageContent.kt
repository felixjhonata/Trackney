package com.felixjhonata.trackney.add_edit_transaction.view

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felixjhonata.trackney.R
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUiState
import com.felixjhonata.trackney.add_edit_transaction.model.AddEditTransactionUserEvent
import com.felixjhonata.trackney.add_edit_transaction.model.ModifyTransactionType
import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.ui.theme.TrackneyTheme

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
    amount: String,
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
                    onValueChange = {},
                    textStyle = MaterialTheme.typography.headlineMedium,
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
                onClick = {},
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
    modifier: Modifier = Modifier
) {
    Text(
        "Category",
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Spacer(Modifier.width(4.dp))
        }

        items(categories) { category ->
            FilterChip(
                selected = selectedCategory?.id == category.id,
                onClick = {},
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
                "Note",
                style = MaterialTheme.typography.labelLarge
            )

            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                value = note,
                onValueChange = {},
                textStyle = MaterialTheme.typography.bodySmall,
                minLines = 4,
                maxLines = 4
            )
        }
    }
}

@Composable
private fun FooterButton(
    type: ModifyTransactionType,
    onPrimaryButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (type) {
        ModifyTransactionType.ADD -> {
            Button(
                modifier = modifier,
                onClick = onPrimaryButtonClick
            ) {
                Text("Add Transaction")
            }
        }

        ModifyTransactionType.EDIT -> {
            Column(modifier) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {},
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
                    onClick = onPrimaryButtonClick
                ) {
                    Text("Edit Transaction")
                }
            }
        }
    }
}

@Composable
fun AddEditTransactionPageContent(
    type: ModifyTransactionType,
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
        }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {
            DatePickerSection(
                uiState.dateTime,
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                )
            )

            AmountField(
                uiState.amount,
                modifier = Modifier.padding(
                    horizontal = 12.dp
                )
            )
            Spacer(Modifier.height(8.dp))

            TypeField(
                uiState.type,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            CategorySection(
                uiState.categories,
                uiState.category,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(Modifier.height(8.dp))

            NoteField(
                uiState.note,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(Modifier.weight(1f))

            FooterButton(
                type = type,
                onPrimaryButtonClick = {
                    val event = if (type == ModifyTransactionType.ADD) {
                        AddEditTransactionUserEvent.AddTransactionButtonPressed
                    } else {
                        AddEditTransactionUserEvent.EditTransactionButtonPressed
                    }

                    onUserEvent(event)
                },
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Preview
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
            AddEditTransactionUiState(
                "30 April 2026 | 19:00",
                "12,000,000",
                TransactionType.EXPENSE,
                categories,
                categories.first(),
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do " +
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