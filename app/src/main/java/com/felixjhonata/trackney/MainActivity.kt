package com.felixjhonata.trackney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.felixjhonata.trackney.add_edit_transaction.view.AddTransactionPage
import com.felixjhonata.trackney.add_edit_transaction.view.EditTransactionPage
import com.felixjhonata.trackney.home.view.HomePage
import com.felixjhonata.trackney.shared.model.AddTransaction
import com.felixjhonata.trackney.category.view.ManageCategoriesPage
import com.felixjhonata.trackney.shared.model.EditTransaction
import com.felixjhonata.trackney.shared.model.Home
import com.felixjhonata.trackney.shared.model.ManageCategories
import com.felixjhonata.trackney.ui.theme.TrackneyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
                            AddTransactionPage(navBackStack)
                        }

                        entry<EditTransaction> { key ->
                            EditTransactionPage(navBackStack, key = key)
                        }

                        entry<ManageCategories> {
                            ManageCategoriesPage(navBackStack)
                        }
                    },
                    transitionSpec = {
                        // Slide in from right when navigating forward
                        slideInHorizontally(initialOffsetX = { it }) togetherWith
                                slideOutHorizontally(targetOffsetX = { -it })
                    },
                    popTransitionSpec = {
                        // Slide in from left when navigating back
                        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                                slideOutHorizontally(targetOffsetX = { it })
                    },
                    predictivePopTransitionSpec = {
                        // Slide in from left when navigating back
                        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                                slideOutHorizontally(targetOffsetX = { it })
                    }
                )
            }
        }
    }
}