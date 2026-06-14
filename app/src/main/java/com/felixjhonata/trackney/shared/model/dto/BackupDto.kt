package com.felixjhonata.trackney.shared.model.dto

import com.felixjhonata.trackney.shared.model.TransactionType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString())
    }
}

@Serializable
data class CategoryBackupDto(
    val id: Int,
    val name: String,
    val type: TransactionType
)

@Serializable
data class TransactionBackupDto(
    val id: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dateTime: LocalDateTime,
    val amount: Double,
    val categoryId: Int,
    val note: String
)

@Serializable
data class BackupDataDto(
    val categories: List<CategoryBackupDto>,
    val transactions: List<TransactionBackupDto>
)
