package com.felixjhonata.trackney.shared.domain

import com.felixjhonata.trackney.shared.model.dto.BackupDataDto
import com.felixjhonata.trackney.shared.model.entity.Category
import com.felixjhonata.trackney.shared.model.entity.Transaction
import com.felixjhonata.trackney.shared.model.repository.TransactionRepository
import kotlinx.serialization.json.Json
import java.io.InputStream
import javax.inject.Inject
import com.felixjhonata.trackney.shared.model.annotations.IoDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream

class ImportBackupUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    @param:IoDispatchers private val ioDispatcher: CoroutineDispatcher
) {
    @OptIn(ExperimentalSerializationApi::class)
    suspend operator fun invoke(inputStream: InputStream) = withContext(ioDispatcher) {
        val backupData = Json.decodeFromStream<BackupDataDto>(inputStream)

        val categories = backupData.categories.map {
            Category(it.id, it.name, it.type)
        }
        val transactions = backupData.transactions.map {
            Transaction(it.id, it.dateTime, it.amount, it.categoryId, it.note)
        }

        transactionRepository.restoreBackup(categories, transactions)
    }
}
