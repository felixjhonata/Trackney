package com.felixjhonata.trackney.shared.model

import androidx.room.Embedded
import androidx.room.Relation
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.shared.model.entity.Transaction

data class TransactionWithCategory(
    @Embedded val transaction: Transaction,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: Category
)
