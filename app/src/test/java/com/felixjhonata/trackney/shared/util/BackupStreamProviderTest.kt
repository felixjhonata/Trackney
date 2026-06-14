package com.felixjhonata.trackney.shared.util

import android.content.Context
import android.content.ContentResolver
import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.InputStream
import java.io.OutputStream

class BackupStreamProviderTest {

    @Test
    fun openInputStream_callsContentResolver() {
        val context: Context = mockk()
        val contentResolver: ContentResolver = mockk()
        val uri: Uri = mockk()
        val inputStream: InputStream = mockk()

        every { context.contentResolver } returns contentResolver
        every { contentResolver.openInputStream(uri) } returns inputStream

        val provider = BackupStreamProvider(context)
        val result = provider.openInputStream(uri)

        assertEquals(inputStream, result)
        verify(exactly = 1) { contentResolver.openInputStream(uri) }
    }

    @Test
    fun openOutputStream_callsContentResolver() {
        val context: Context = mockk()
        val contentResolver: ContentResolver = mockk()
        val uri: Uri = mockk()
        val outputStream: OutputStream = mockk()

        every { context.contentResolver } returns contentResolver
        every { contentResolver.openOutputStream(uri) } returns outputStream

        val provider = BackupStreamProvider(context)
        val result = provider.openOutputStream(uri)

        assertEquals(outputStream, result)
        verify(exactly = 1) { contentResolver.openOutputStream(uri) }
    }
}
