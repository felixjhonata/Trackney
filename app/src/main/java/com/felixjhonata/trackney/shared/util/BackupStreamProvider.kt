package com.felixjhonata.trackney.shared.util

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupStreamProvider @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun openInputStream(uri: Uri): InputStream? = context.contentResolver.openInputStream(uri)
    fun openOutputStream(uri: Uri): OutputStream? = context.contentResolver.openOutputStream(uri)
}
