package com.felixjhonata.trackney.home.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.felixjhonata.trackney.R
import com.felixjhonata.trackney.home.model.HomeUiEvent
import com.felixjhonata.trackney.home.model.HomeUiState
import com.felixjhonata.trackney.home.model.HomeUserEvent
import com.felixjhonata.trackney.home.model.TransactionGroup
import com.felixjhonata.trackney.home.model.TransactionItemUiState
import com.felixjhonata.trackney.home.viewmodel.HomeViewModel
import com.felixjhonata.trackney.shared.model.AddTransaction
import com.felixjhonata.trackney.shared.model.EditTransaction
import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.ui.theme.TrackneyTheme

@Composable
private fun ArrowIcon(
    @DrawableRes drawableId: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painterResource(drawableId),
            contentDescription
        )
    }
}

@Composable
private fun MonthPicker(
    month: String,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArrowIcon(
            R.drawable.outline_arrow_back_24,
            "previous_month",
            onPrev
        )

        Text(
            month,
            modifier = Modifier.padding(
                vertical = 4.dp,
                horizontal = 8.dp
            ),
            style = MaterialTheme.typography.bodyMedium
        )

        ArrowIcon(
            R.drawable.outline_arrow_forward_24,
            "after_month",
            onNext
        )
    }
}

@Composable
private fun BalanceDetailSubcard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                value,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
private fun BalanceDetailCard(
    totalBalance: String,
    income: String,
    expense: String,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Total Balance",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                totalBalance,
                style = MaterialTheme.typography.displaySmall
            )

            Spacer(Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BalanceDetailSubcard(
                    "Income",
                    income,
                    modifier = Modifier.weight(1f)
                )

                BalanceDetailSubcard(
                    "Expense",
                    expense,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        title,
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
private fun TransactionTitle(
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SectionTitle(
            "Transactions"
        )

        Button(
            onClick = onAdd
        ) {
            Icon(
                painterResource(R.drawable.outline_add_24),
                "add_icon"
            )

            Spacer(Modifier.width(4.dp))

            Text("Add")
        }
    }
}

@Composable
private fun DateCard(
    date: String,
    total: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = 12.dp,
                    vertical = 4.dp,
                )
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                date,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                total,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
fun TransactionCard(
    category: String,
    time: String,
    amount: String,
    type: TransactionType,
    note: String,
    modifier: Modifier = Modifier
) {
    val iconSurfaceColor = when (type) {
        TransactionType.INCOME -> MaterialTheme.colorScheme.primary
        TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = iconSurfaceColor
                ) {
                    Icon(
                        painterResource(R.drawable.outline_attach_money_24),
                        "dollar_icon",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(Modifier.width(8.dp))

                Column {
                    Text(
                        category,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        time,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.weight(1f))

                Text(
                    amount,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        if (note.isNotEmpty()) {
            Text(
                note,
                modifier = Modifier.padding(
                    vertical = 4.dp,
                    horizontal = 16.dp
                ),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun HomePageContent(
    uiState: HomeUiState,
    onUserEvent: (HomeUserEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            item {
                MonthPicker(
                    uiState.selectedMonthYear,
                    { onUserEvent(HomeUserEvent.PreviousMonth) },
                    { onUserEvent(HomeUserEvent.NextMonth) },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                BalanceDetailCard(
                    uiState.totalBalance,
                    uiState.totalIncome,
                    uiState.totalExpense,
                    modifier = Modifier.padding(
                        horizontal = 12.dp
                    )
                )
                Spacer(Modifier.height(16.dp))
            }

            item {
                TransactionTitle(
                    onAdd = { onUserEvent(HomeUserEvent.AddTransactionClicked) },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
            }

            uiState.groupedTransactions.forEach { group ->
                item {
                    DateCard(
                        group.date,
                        group.totalAmount,
                        modifier = Modifier.padding(
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 12.dp
                        )
                    )
                }

                items(group.transactions) { transaction ->
                    TransactionCard(
                        transaction.category,
                        transaction.time,
                        transaction.amount,
                        transaction.type,
                        transaction.note,
                        modifier = Modifier
                            .padding(
                                start = 12.dp,
                                end = 12.dp,
                                bottom = 12.dp
                            )
                            .clickable { onUserEvent(HomeUserEvent.EditTransactionClicked(transaction.id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun HomePage(
    navBackStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                HomeUiEvent.NavigateToAdd -> navBackStack.add(AddTransaction)
                is HomeUiEvent.NavigateToEdit -> navBackStack.add(EditTransaction(event.transactionId))
            }
        }
    }

    HomePageContent(
        uiState = uiState,
        onUserEvent = viewModel::onUserEvent,
        modifier = modifier
    )
}

@Preview
@Composable
private fun HomePagePreview() {
    TrackneyTheme {
        HomePageContent(
            uiState = HomeUiState(
                "April 2026",
                "Rp12,000,000",
                "Rp13,000,000",
                "Rp1,000,000",
                listOf(
                    TransactionGroup(
                        date = "10 May 2026",
                        totalAmount = "Rp50.000",
                        totalAmountType = TransactionType.EXPENSE,
                        transactions = listOf(
                            TransactionItemUiState(
                                id = 1,
                                category = "Food",
                                time = "12:00",
                                amount = "Rp30.000",
                                type = TransactionType.EXPENSE,
                                "McDonald"
                            ),
                            TransactionItemUiState(
                                id = 2,
                                category = "Transport",
                                time = "13:00",
                                amount = "Rp20.000",
                                type = TransactionType.EXPENSE,
                                "Transjakarta"
                            )
                        )
                    ),
                    TransactionGroup(
                        date = "09 May 2026",
                        totalAmount = "Rp100.000",
                        totalAmountType = TransactionType.INCOME,
                        transactions = listOf(
                            TransactionItemUiState(
                                id = 3,
                                category = "Salary",
                                time = "09:00",
                                amount = "Rp100.000",
                                type = TransactionType.INCOME,
                                ""
                            )
                        )
                    )
                )
            ),
            onUserEvent = {}
        )
    }
}