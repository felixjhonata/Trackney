package com.felixjhonata.trackney.shared.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.felixjhonata.trackney.shared.model.TransactionType

@Entity("categories")
data class Category(
    @PrimaryKey val id: Int,
    val name: String,
    val type: TransactionType
)
