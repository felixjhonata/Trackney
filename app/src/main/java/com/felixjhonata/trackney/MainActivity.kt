package com.felixjhonata.trackney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.felixjhonata.trackney.add_edit_transaction.model.ModifyTransactionType
import com.felixjhonata.trackney.add_edit_transaction.view.AddEditTransactionPage
import com.felixjhonata.trackney.home.view.HomePage
import com.felixjhonata.trackney.shared.model.AddTransaction
import com.felixjhonata.trackney.shared.model.EditTransaction
import com.felixjhonata.trackney.shared.model.Home
import com.felixjhonata.trackney.ui.theme.TrackneyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrackneyTheme {
                val navBackStack = rememberNavBackStack(Home)
                NavDisplay(
                    entryDecorators = listOf(
                        // Add the default decorators for managing scenes and saving state
                        rememberSaveableStateHolderNavEntryDecorator(),
                        // Then add the view model store decorator
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    backStack = navBackStack,
                    onBack = { navBackStack.removeLastOrNull() },
                    entryProvider = entryProvider {
                        entry<Home> {
                            HomePage(navBackStack)
                        }

                        entry<AddTransaction> {
                            AddEditTransactionPage(
                                ModifyTransactionType.ADD,
                                navBackStack
                            )
                        }

                        entry<EditTransaction> {
                            AddEditTransactionPage(
                                ModifyTransactionType.EDIT,
                                navBackStack
                            )
                        }
                    }
                )
            }
        }
    }
}