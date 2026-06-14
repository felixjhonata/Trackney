package com.felixjhonata.trackney.shared.util

import android.content.Context
import android.content.ContentResolver
import android.net.Uri
import androidx.core.net.toUri
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
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
        val uriString = "content://test/backup.json"

        mockkStatic(Uri::class)
        every { uriString.toUri() } returns uri
        every { context.contentResolver } returns contentResolver
        every { contentResolver.openInputStream(uri) } returns inputStream

        try {
            val provider = BackupStreamProvider(context)
            val result = provider.openInputStream(uriString)

            assertEquals(inputStream, result)
            verify(exactly = 1) { contentResolver.openInputStream(uri) }
        } finally {
            unmockkStatic(Uri::class)
        }
    }

    @Test
    fun openOutputStream_callsContentResolver() {
        val context: Context = mockk()
        val contentResolver: ContentResolver = mockk()
        val uri: Uri = mockk()
        val outputStream: OutputStream = mockk()
        val uriString = "content://test/backup.json"

        mockkStatic(Uri::class)
        every { uriString.toUri() } returns uri
        every { context.contentResolver } returns contentResolver
        every { contentResolver.openOutputStream(uri) } returns outputStream

        try {
            val provider = BackupStreamProvider(context)
            val result = provider.openOutputStream(uriString)

            assertEquals(outputStream, result)
            verify(exactly = 1) { contentResolver.openOutputStream(uri) }
        } finally {
            unmockkStatic(Uri::class)
        }
    }
}
