package com.github.ntoj.app.shared.util

/**
 * Trim the string every line.
 */
fun String.trimByLine() = this.lines().joinToString("\n") { it.trimEnd() }

/**
 * Remove the last empty line.
 */
fun String.removeLastEmptyLine() = this.lines().dropLastWhile { it.isBlank() }.joinToString("\n")
