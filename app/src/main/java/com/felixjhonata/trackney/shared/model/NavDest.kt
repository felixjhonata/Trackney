package com.felixjhonata.trackney.shared.model

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Home: NavKey

@Serializable
data object AddEditTransaction: NavKey