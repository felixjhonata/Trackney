package com.felixjhonata.trackney.shared.domain

import com.felixjhonata.trackney.shared.model.dto.BackupDataDto
import com.felixjhonata.trackney.shared.model.dto.CategoryBackupDto
import com.felixjhonata.trackney.shared.model.dto.TransactionBackupDto
import com.felixjhonata.trackney.shared.model.repository.CategoryRepository
import com.felixjhonata.trackney.shared.model.repository.TransactionRepository
import kotlinx.serialization.json.Json
import java.io.OutputStream
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExportBackupUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(outputStream: OutputStream) = withContext(Dispatchers.IO) {
        val categories = categoryRepository.getAllCategoriesList().map {
            CategoryBackupDto(it.id, it.name, it.type)
        }
        val transactions = transactionRepository.getAllTransactions().map {
            TransactionBackupDto(it.id, it.dateTime, it.amount, it.categoryId, it.note)
        }
        val backupData = BackupDataDto(categories, transactions)
        val jsonString = Json.encodeToString(backupData)
        outputStream.write(jsonString.toByteArray(Charsets.UTF_8))
        outputStream.flush()
    }
}
