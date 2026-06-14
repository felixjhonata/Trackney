package com.felixjhonata.trackney.home.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
            stringResource(R.string.previous_month_desc),
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
            stringResource(R.string.next_month_desc),
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
                stringResource(R.string.total_balance),
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
                    stringResource(R.string.income),
                    income,
                    modifier = Modifier.weight(1f)
                )

                BalanceDetailSubcard(
                    stringResource(R.string.expense),
                    expense,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TransactionsSectionTitle(
    modifier: Modifier = Modifier
) {
    Text(
        stringResource(R.string.transactions),
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
        TransactionsSectionTitle()

        Button(
            onClick = onAdd
        ) {
            Icon(
                painterResource(R.drawable.outline_add_24),
                stringResource(R.string.add_icon)
            )

            Spacer(Modifier.width(4.dp))

            Text(stringResource(R.string.add_lbl))
        }
    }
}

@Composable
private fun BackupSpeedDialFab(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        label = "FAB Icon Rotation"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Import Button
                ExtendedFloatingActionButton(
                    onClick = onImport,
                    icon = {
                        Icon(
                            painterResource(R.drawable.outline_download_24),
                            contentDescription = stringResource(R.string.import_backup)
                        )
                    },
                    text = { Text(stringResource(R.string.import_backup)) },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )

                // Export Button
                ExtendedFloatingActionButton(
                    onClick = onExport,
                    icon = {
                        Icon(
                            painterResource(R.drawable.outline_upload_24),
                            contentDescription = stringResource(R.string.export_backup)
                        )
                    },
                    text = { Text(stringResource(R.string.export_backup)) },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )

                Spacer(Modifier.height(8.dp))
            }
        }

        FloatingActionButton(
            onClick = onToggle,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                painter = painterResource(
                    if (isExpanded) R.drawable.outline_close_24
                    else R.drawable.baseline_menu_24
                ),
                contentDescription = stringResource(R.string.backup_menu),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.rotate(rotationAngle)
            )
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
                        stringResource(R.string.dollar_icon),
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
    snackbarHostState: SnackbarHostState,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFabExpanded by remember { mutableStateOf(false) }
    var showImportConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            BackupSpeedDialFab(
                isExpanded = isFabExpanded,
                onToggle = { isFabExpanded = !isFabExpanded },
                onExport = {
                    isFabExpanded = false
                    onExportBackup()
                },
                onImport = {
                    isFabExpanded = false
                    showImportConfirmation = true
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LazyColumn {
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
                                .clickable {
                                    onUserEvent(
                                        HomeUserEvent.EditTransactionClicked(
                                            transaction.id
                                        )
                                    )
                                }
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isFabExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isFabExpanded = false
                        }
                )
            }

            if (showImportConfirmation) {
                AlertDialog(
                    onDismissRequest = { showImportConfirmation = false },
                    title = { Text(stringResource(R.string.import_backup_confirmation_title)) },
                    text = { Text(stringResource(R.string.import_backup_confirmation_message)) },
                    confirmButton = {
                        Button(
                            onClick = {
                                showImportConfirmation = false
                                onImportBackup()
                            }
                        ) {
                            Text(stringResource(R.string.import_action))
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { showImportConfirmation = false }
                        ) {
                            Text(stringResource(R.string.cancel_action))
                        }
                    }
                )
            }

            if (uiState.isExporting || uiState.isImporting) {
                Dialog(
                    onDismissRequest = {},
                    properties = DialogProperties(
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(150.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.medium
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = if (uiState.isExporting) {
                                    stringResource(R.string.exporting)
                                } else {
                                    stringResource(R.string.importing)
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
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
    val snackbarHostState = remember { SnackbarHostState() }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.onUserEvent(HomeUserEvent.ExportData(it)) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.onUserEvent(HomeUserEvent.ImportData(it)) }
    }

    val exportSuccess = stringResource(R.string.backup_exported_success)
    val importSuccess = stringResource(R.string.backup_imported_success)
    val exportFailedFormat = stringResource(R.string.export_failed)
    val importFailedFormat = stringResource(R.string.import_failed)

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                HomeUiEvent.NavigateToAdd -> navBackStack.add(AddTransaction)
                is HomeUiEvent.NavigateToEdit -> navBackStack.add(EditTransaction(event.transactionId))
                is HomeUiEvent.ShowSnackbar -> {
                    val message = when (event.messageRes) {
                        R.string.backup_exported_success -> exportSuccess
                        R.string.backup_imported_success -> importSuccess
                        R.string.export_failed -> exportFailedFormat.format(event.formatArg)
                        R.string.import_failed -> importFailedFormat.format(event.formatArg)
                        else -> ""
                    }
                    snackbarHostState.showSnackbar(message)
                }
            }
        }
    }

    HomePageContent(
        uiState = uiState,
        onUserEvent = viewModel::onUserEvent,
        snackbarHostState = snackbarHostState,
        onExportBackup = { exportLauncher.launch("trackney_backup.json") },
        onImportBackup = {
            importLauncher.launch(
                arrayOf(
                    "application/json",
                    "application/octet-stream"
                )
            )
        },
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
            onUserEvent = {},
            snackbarHostState = remember { SnackbarHostState() },
            onExportBackup = {},
            onImportBackup = {}
        )
    }
}