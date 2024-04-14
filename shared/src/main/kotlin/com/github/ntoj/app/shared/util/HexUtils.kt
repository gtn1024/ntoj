package com.github.ntoj.app.shared.util

import org.apache.commons.codec.digest.DigestUtils
import java.io.InputStream

fun fileMd5(file: InputStream): String {
    return DigestUtils.md5Hex(file)
}
