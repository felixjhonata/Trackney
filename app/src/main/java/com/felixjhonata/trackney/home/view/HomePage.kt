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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.felixjhonata.trackney.R
import com.felixjhonata.trackney.home.model.TransactionType
import com.felixjhonata.trackney.shared.model.AddTransaction
import com.felixjhonata.trackney.shared.model.EditTransaction
import com.felixjhonata.trackney.shared.model.Home
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
            containerColor = MaterialTheme.colorScheme.secondaryContainer
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
    modifier: Modifier = Modifier
) {
    val iconSurfaceColor = when (type) {
        TransactionType.INCOME -> MaterialTheme.colorScheme.primary
        TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
    }

    OutlinedCard(modifier) {
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
}

@Composable
fun HomePage(
    navBackStack: NavBackStack<NavKey>,
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
                    "April 2026",
                    {},
                    {},
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                BalanceDetailCard(
                    "Rp 12,000,000",
                    "Rp 20,000,000",
                    "Rp 3,000,000",
                    modifier = Modifier.padding(
                        horizontal = 12.dp
                    )
                )
                Spacer(Modifier.height(16.dp))
            }

            item {
                TransactionTitle(
                    { navBackStack.add(AddTransaction) },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                DateCard(
                    "28 April 2026",
                    "+Rp 3,000,000",
                    modifier = Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 12.dp
                    )
                )
            }

            item {
                TransactionCard(
                    "Food",
                    "19:00",
                    "-Rp 150,000",
                    TransactionType.EXPENSE,
                    modifier = Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 12.dp
                    ).clickable {
                        navBackStack.add(EditTransaction)
                    }
                )
            }

            item {
                TransactionCard(
                    "Food",
                    "19:00",
                    "-Rp 25,000",
                    TransactionType.EXPENSE,
                    modifier = Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 12.dp
                    )
                )
            }

            item {
                TransactionCard(
                    "Food",
                    "19:00",
                    "-Rp 25,000",
                    TransactionType.EXPENSE,
                    modifier = Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 12.dp
                    )
                )
            }

            item {
                TransactionCard(
                    "Salary",
                    "19:00",
                    "+Rp 3,200,000",
                    TransactionType.INCOME,
                    modifier = Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 12.dp
                    )
                )
            }

            item {
                DateCard(
                    "27 April 2026",
                    "-Rp 150,000",
                    modifier = Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 12.dp
                    )
                )
            }

            item {
                TransactionCard(
                    "Food",
                    "19:00",
                    "-Rp 150,000",
                    TransactionType.EXPENSE,
                    modifier = Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 12.dp
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomePagePreview() {
    TrackneyTheme {
        HomePage(rememberNavBackStack(Home))
    }
}