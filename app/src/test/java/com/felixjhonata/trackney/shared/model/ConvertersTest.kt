package com.felixjhonata.trackney.shared.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun testLongToLocalDateTime() {
        val epochSecond = 1700000000L // arbitrary fixed timestamp
        val expected = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochSecond(epochSecond),
            ZoneId.systemDefault()
        )
        val result = converters.longToLocalDateTime(epochSecond)
        assertEquals(expected, result)
    }

    @Test
    fun testLocalDateTimeToLong() {
        val localDateTime = LocalDateTime.of(2023, 11, 14, 12, 0, 0)
        val offset = ZoneId.systemDefault().rules.getOffset(localDateTime)
        val expected = localDateTime.toEpochSecond(offset)
        val result = converters.localDateTimeToLong(localDateTime)
        assertEquals(expected, result)
    }
}
