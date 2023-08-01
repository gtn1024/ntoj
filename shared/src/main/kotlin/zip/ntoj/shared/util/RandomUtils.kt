package zip.ntoj.shared.util

import java.util.UUID

fun randomString() = UUID.randomUUID().toString().replace("-", "")
