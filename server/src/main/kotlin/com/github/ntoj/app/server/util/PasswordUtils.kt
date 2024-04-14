package com.github.ntoj.app.server.util

import org.apache.commons.codec.digest.DigestUtils
import com.github.ntoj.app.shared.util.randomString

fun getSalt() = randomString()

fun hashPassword(
    password: String,
    salt: String,
): String {
    return DigestUtils.sha256Hex(password + salt)
}

fun checkPassword(
    password: String,
    salt: String,
    hash: String,
): Boolean {
    return hashPassword(password, salt) == hash
}
