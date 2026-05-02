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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.felixjhonata.trackney.R
import com.felixjhonata.trackney.add_edit_transaction.model.ModifyTransactionType
import com.felixjhonata.trackney.shared.model.Home
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
private fun DatePickerSection(modifier: Modifier = Modifier) {
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
                "30 April 2026 | 19:00",
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
private fun AmountField(modifier: Modifier = Modifier) {
    var amountInput by remember { mutableStateOf("") }

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
                    value = amountInput,
                    onValueChange = { amountInput = it },
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
private fun TypeField(modifier: Modifier = Modifier) {
    val options = listOf("Income", "Expense")

    SingleChoiceSegmentedButtonRow(modifier) {
        options.forEachIndexed { index, string ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = {},
                selected = index == 0,
                label = { Text(string) }
            )
        }
    }
}

@Composable
private fun CategorySection(modifier: Modifier = Modifier) {
    val categories = listOf("Food", "Utility", "Grocery", "Entertainment", "Others")

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

        itemsIndexed(categories) { index, category ->
            FilterChip(
                selected = index == 0,
                onClick = {},
                label = { Text(category) }
            )
        }

        item {
            Spacer(Modifier.width(4.dp))
        }
    }
}

@Composable
private fun NoteField(modifier: Modifier = Modifier) {
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
                value = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                onValueChange = {},
                textStyle = MaterialTheme.typography.bodySmall,
                maxLines = 4
            )
        }
    }
}

@Composable
private fun FooterButton(
    type: ModifyTransactionType,
    modifier: Modifier = Modifier
) {
    when (type) {
        ModifyTransactionType.ADD -> {
            Button(
                modifier = modifier,
                onClick = {}
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
                    onClick = {}
                ) {
                    Text("Edit Transaction")
                }
            }
        }
    }
}

@Composable
fun AddEditTransactionPage(
    type: ModifyTransactionType,
    navBackStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                type
            ) { navBackStack.removeLastOrNull() }

        }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {
            DatePickerSection(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                )
            )

            AmountField(
                modifier = Modifier.padding(
                    horizontal = 12.dp
                )
            )
            Spacer(Modifier.height(8.dp))

            TypeField(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            CategorySection(
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(Modifier.height(8.dp))

            NoteField(
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(Modifier.weight(1f))

            FooterButton(
                type = type,
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
    TrackneyTheme {
        AddEditTransactionPage(
            ModifyTransactionType.ADD,
            rememberNavBackStack(Home)
        )
    }
}