package com.felixjhonata.trackney.shared.util

import java.io.InputStream
import java.io.OutputStream

interface BackupStreamResolver {
    fun openInputStream(uriString: String): InputStream?
    fun openOutputStream(uriString: String): OutputStream?
}
