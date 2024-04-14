package com.github.ntoj.app.shared.util

import java.io.File
import java.util.zip.ZipFile

object ZipUtils {
    fun getFilenamesFromZip(zipFile: File): List<String> {
        val filenames = mutableListOf<String>()
        ZipFile(zipFile).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                filenames.add(entry.name)
            }
        }
        return filenames
    }
}
