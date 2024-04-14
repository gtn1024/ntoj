package com.github.ntoj.app.shared.util

import java.util.UUID

fun randomString() = UUID.randomUUID().toString().replace("-", "")
