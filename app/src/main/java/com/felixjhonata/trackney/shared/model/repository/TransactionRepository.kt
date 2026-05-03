package com.felixjhonata.trackney.shared.model.repository

import com.felixjhonata.trackney.shared.model.dao.TransactionDao
import com.felixjhonata.trackney.shared.model.entity.Transaction
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    fun insertTransaction(
        transaction: Transaction
    ) = transactionDao.insertTransaction(transaction)
}