package com.felixjhonata.trackney.shared.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("transactions")
data class Transaction(
    @PrimaryKey val id: Int,
    val date: Long,
    val amount: Double,
    @ColumnInfo("category_id") val categoryId: Int,
    val note: String
)