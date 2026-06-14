package com.felixjhonata.trackney.shared.util

import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupStreamProvider @Inject constructor(
    @param:ApplicationContext private val context: Context
) : BackupStreamResolver {
    override fun openInputStream(uriString: String): InputStream? {
        val uri = uriString.toUri()
        return context.contentResolver.openInputStream(uri)
    }

    override fun openOutputStream(uriString: String): OutputStream? {
        val uri = uriString.toUri()
        return context.contentResolver.openOutputStream(uri)
    }
}
