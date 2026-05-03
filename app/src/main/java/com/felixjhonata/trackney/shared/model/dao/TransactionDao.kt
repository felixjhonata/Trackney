package com.felixjhonata.trackney.shared.model.dao

import androidx.room.Dao
import androidx.room.Insert
import com.felixjhonata.trackney.shared.model.entity.Transaction

@Dao
interface TransactionDao {
    @Insert
    fun insertTransaction(transaction: Transaction)
}