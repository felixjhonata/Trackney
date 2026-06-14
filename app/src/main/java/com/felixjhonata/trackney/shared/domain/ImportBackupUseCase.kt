package com.felixjhonata.trackney.shared.domain

import com.felixjhonata.trackney.shared.model.dto.BackupDataDto
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.shared.model.entity.Transaction
import com.felixjhonata.trackney.shared.model.repository.TransactionRepository
import kotlinx.serialization.json.Json
import java.io.InputStream
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImportBackupUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(inputStream: InputStream) = withContext(Dispatchers.IO) {
        val jsonString = inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val backupData = Json.decodeFromString<BackupDataDto>(jsonString)

        val categories = backupData.categories.map {
            Category(it.id, it.name, it.type)
        }
        val transactions = backupData.transactions.map {
            Transaction(it.id, it.dateTime, it.amount, it.categoryId, it.note)
        }

        transactionRepository.restoreBackup(categories, transactions)
    }
}
