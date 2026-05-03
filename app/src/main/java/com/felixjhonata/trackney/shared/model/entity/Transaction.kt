package com.felixjhonata.trackney.shared.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity("transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateTime: LocalDateTime,
    val amount: Double,
    @ColumnInfo("category_id") val categoryId: Int,
    val note: String
)