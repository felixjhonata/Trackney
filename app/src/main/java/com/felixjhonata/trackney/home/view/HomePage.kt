package com.felixjhonata.trackney.home.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felixjhonata.trackney.R
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
fun HomePage(modifier: Modifier = Modifier) {
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
                Spacer(Modifier.height(18.dp))
            }

            item {
                SectionTitle(
                    "Transactions",
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomePagePreview() {
    TrackneyTheme {
        HomePage()
    }
}