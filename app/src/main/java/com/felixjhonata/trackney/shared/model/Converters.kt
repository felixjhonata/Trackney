package com.felixjhonata.trackney.shared.model

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class Converters {
    @TypeConverter
    fun longToLocalDateTime(epochSecond: Long): LocalDateTime {
        val instant = Instant.ofEpochSecond(epochSecond)
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    }

    @TypeConverter
    fun localDateTimeToLong(localDateTime: LocalDateTime): Long {
        val offset = ZoneId.systemDefault().rules.getOffset(localDateTime)
        return localDateTime.toEpochSecond(offset)
    }
}