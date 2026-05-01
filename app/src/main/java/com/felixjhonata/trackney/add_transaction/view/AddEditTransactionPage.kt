package com.felixjhonata.trackney.add_transaction.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felixjhonata.trackney.R
import com.felixjhonata.trackney.ui.theme.TrackneyTheme

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
fun AddEditTransactionPage(modifier: Modifier = Modifier) {
    var amountInput by remember { mutableStateOf("") }

    Scaffold(modifier) { innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {
            DatePickerSection(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                )
            )

            OutlinedCard(
                modifier = Modifier.padding(
                    horizontal = 12.dp
                )
            ) {
                Column(
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
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
    }
}

@Preview
@Composable
private fun AddEditTransactionPagePreview() {
    TrackneyTheme {
        AddEditTransactionPage()
    }
}